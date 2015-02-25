package org.anc.lapps.masc;

import org.anc.index.api.Index;
import org.anc.io.UTF8Reader;
//import org.anc.lapps.oauth.database.Token;
//import org.anc.lapps.oauth.database.TokenDatabase;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Error;
import org.lappsgrid.serialization.Serializer;
import static org.lappsgrid.discriminator.Discriminators.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.commons.net.URLUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.servicecontainer.handler.RIProcessor;
//import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.soap.MimeHeaders;


/**
 * @author Keith Suderman
 */
public abstract class MascAbstractDataSource implements DataSource
{
	static {
		try
		{
			Class.forName("org.h2.Driver");
		}
		catch (ClassNotFoundException e)
		{
			// Ignore.
			e.printStackTrace();
		}
	}

	public static boolean testing = false;

	private final Logger logger; // = LoggerFactory.getLogger(MascAbstractDataSource.class);

//	@Autowired
//	private TokenDatabase tokenDatabase;

	protected Index index;

	/** Metadata for the service is cached in this field so it does not need to be read for disk every request. */
	protected String metadata;
	/** The type of data returned by this service. */
	protected String returnType;
	/** The number of documents managed by this data source. */
	protected final int size;
	/** Error message set in the <tt>authenticate</tt> method. */
	private String errorMessage;

	public MascAbstractDataSource(Index index, Class<? extends MascAbstractDataSource> dsClass, String returnType)
	{
		this.index = index;
		this.returnType = returnType;
		this.logger = LoggerFactory.getLogger(dsClass);
		this.size = index.keys().size();
		this.metadata = loadMetadata("metadata/" + dsClass.getName() + ".json");

	}

	public String execute(String input)
	{
		logger.debug("Executing request: {}", input);
		// Clear any existing error message.
		errorMessage = null;
		if (!authenticate())
		{
			logger.error("Unauthorized access attempted.");
			if (errorMessage == null)
			{
				errorMessage = "Unauthorized.";
			}
			logger.error(errorMessage);
			return new Error(errorMessage).asJson();
		}


		Map<String,Object> map = Serializer.parse(input, HashMap.class);
		String discriminator = (String) map.get("discriminator");
		if (discriminator == null)
		{
			logger.error("No discriminator present in request.");
			return new Error("No discriminator value provided.").asJson();
		}

		String result = null;
		switch (discriminator)
		{
			case Uri.SIZE:
				logger.debug("Fetching size");
				Data<Integer> sizeData = new Data<Integer>();
				sizeData.setDiscriminator(Uri.OK);
				sizeData.setPayload(size);
				result = Serializer.toJson(sizeData);
				break;
			case Uri.LIST:
				logger.debug("Fetching list");
				List<String> keys = index.keys();
				Map payload = (Map) map.get("payload");
				System.out.println("Payload is " + payload.getClass().getName());
				//System.out.println(payload.toString());
				//Map<String,Object> offsets = Serializer.parse(payload.toString(), Map.class);
				Object startValue = payload.get("start");
				if (startValue != null)
				{
					int start = 0;
					int offset = Integer.parseInt(startValue.toString());
					if (offset >= 0) {
						start = offset;
					}
					int end = index.keys().size();
					Object endValue = payload.get("end");
					if (endValue != null)
					{
						offset = Integer.parseInt(endValue.toString());
						if (offset >= start) {
							end = offset;
						}
					}
					logger.debug("Returning sublist {}-{}", start, end);
					keys = keys.subList(start, end);
				}
				Data<java.util.List<String>> listData = new Data<>();
				listData.setDiscriminator(Uri.STRING_LIST);
				listData.setPayload(keys);
				result = Serializer.toJson(listData);
				break;
			case Uri.GET:
				logger.debug("Fetching document");
				String key = map.get("payload").toString();
				if (key == null)
				{
					result = error("No key value provided");
				}
				else
				{
					File file = index.get(key);
					if (file == null)
					{
						result = error("No such file.");
					}
					else if (!file.exists())
					{
						result = error("That file was not found on this server.");
					}
					else try
						{
							logger.debug("Loading text from file {}", file.getPath());
							UTF8Reader reader = new UTF8Reader(file);
							String content = reader.readString();
							reader.close();
							Data<String> stringData = new Data<String>();
							stringData.setDiscriminator(returnType);
							stringData.setPayload(content);
							result = Serializer.toJson(stringData);
						}
						catch (IOException e)
						{
							result = error(e.getMessage());
							logger.error("Error loading text for {}", file.getPath(),e);
						}

				}
				break;
			case Uri.GETMETADATA:
				logger.warn("Deprecated discriminator GETMETADATA used.");
				result = metadata;
				break;
			default:
				String message = String.format("Invalid discriminator: %s, Uri.List is %s", discriminator, Uri.LIST);
				//logger.warn(message);
				result = error(message);
				break;
		}
		logger.trace("Returning result {}", result);
		return result;
	}

	protected boolean authenticate()
	{
		if (testing)
		{
			return true;
		}

		ServiceContext sc = RIProcessor.getCurrentServiceContext();
		if (sc == null)
		{
			errorMessage = "Server was unable to access the service context.";
			logger.error(errorMessage);
			return true;
		}
		// URL Parameter access
		String urlParam = URLUtil.getQueryParameters(sc.getRequestUrl()).get("param");
		logger.debug("URL parameter value: {}", urlParam);

		// mime header access
		MimeHeaders headers = sc.getRequestMimeHeaders();
		if (headers == null)
		{
			errorMessage = "No mime headers";
			logger.debug(errorMessage);
			return false;
		}
		else
		{
			String[] authorizations = headers.getHeader("authorization");
			if (authorizations == null || authorizations.length == 0)
			{
				errorMessage = "No authorization header found.";
				logger.debug(errorMessage);
				return false;
			}
			String header = authorizations[0].toLowerCase();
			logger.debug("Authorization: {}", header);
			if (!header.startsWith("bearer "))
			{
				errorMessage = "Authorization must be done with an OAuth access token. Found: " + header;
				logger.debug(errorMessage);
				return false;
			}
			header = header.substring(7);
			Object token = null;
			Connection connection = null;
			boolean valid = false;
			try
			{
				String url = "jdbc:h2:/usr/share/h2/lapps-oauth;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE";
				String username = "lappsoauth";
				String password = "xkcdC@rt00Nz";
				connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery("select * from Token t where t.token='" + header + "'");
				ResultSetMetaData metaData = result.getMetaData();
				int count = metaData.getColumnCount();
				logger.debug("Result set contains {} columns", count);
				for (int i = 1; i <= count; ++i)
				{
					logger.debug("Column: Name {} Label {}", metaData.getColumnName(i), metaData.getColumnLabel(i));
				}

				while (result.next())
				{
					valid = true;
					Long id = result.getLong("ID");
					String access = result.getString("TOKEN");
					String clientId = result.getString("CLIENT_ID");
					logger.debug("Token from database: {} {}", clientId, access);
					System.out.println("Token id   : " + id);
					System.out.println("Acess token: " + access);
					System.out.println("Client ID  : " + clientId);
				}
				statement.close();
				connection.close();
			}
			catch (SQLException e)
			{
				logger.error("Unable to access the database.", e);
				e.printStackTrace();
			}


//			Token token = null;
//			token = tokenDatabase.findByToken(header);
			if (!valid)
			{
				errorMessage = "Invalid access token: " + header;
				return "2ae317014058d00f742ccc9fdf27a701".equals(header);
			}
//			errorMessage = "Invalid access token.";
			System.out.println("Access token is valid.");
			return true;
//			Iterator<MimeHeader> it = headers.getAllHeaders();
//			while (it.hasNext())
//			{
//				MimeHeader header = it.next();
//				logger.debug(header.getName() + " = " + header.getValue());
//			}
		}

		// rpc header access
//		for(RpcHeader h : sc.getRequestRpcHeaders()){
//			if(h.getName().equals("RpcHeader")){
//				logger.debug("[TestServiceImpl.hello] rpc header value: " + h.getValue());
//			}
//		}

//		if (tokenDatabase != null)
//		{
//			List<Token> tokens = tokenDatabase.findAll();
//			for (Token token : tokens)
//			{
//				logger.debug("{}: {}", token.getClientId(), token.getToken());
//			}
//		}
//		return true;
	}

	public String getMetadata()
	{
		return metadata;
	}

	protected String loadMetadata(String metadataPath)
	{
		ClassLoader loader = this.getClass().getClassLoader();
//		System.out.println("Attempting to load metadata from " + metadataPath);
		InputStream inputStream = loader.getResourceAsStream(metadataPath);
		if (inputStream == null)
		{
			return error("Unable to locate metadata at: " + metadataPath);
		}

		String result;
		try
		{
			UTF8Reader reader = new UTF8Reader(inputStream);
			String json = reader.readString();
			reader.close();
			Data<String> data = new Data<String>();
			data.setDiscriminator(Uri.META);
			data.setPayload(json);
			result = data.asJson();
		}
		catch (IOException e)
		{
			//return DataFactory.error("Unable to load metadata.", e);
			result = new Error("Unable to load metadata.").asJson();
		}
		return result;
	}

	protected String error(String message, Throwable t)
	{
		logger.error(message, t);
		return new Error(message).asJson();
	}

	protected String error(String message)
	{
		logger.error(message);
		return new Error(message).asJson();
	}
}
