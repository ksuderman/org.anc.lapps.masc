package org.anc.lapps.masc;

import org.anc.index.api.Index;
import org.anc.io.UTF8Reader;
//import org.anc.lapps.oauth.database.Token;
//import org.anc.lapps.oauth.database.TokenDatabase;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.metadata.DataSourceMetadata;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.commons.net.URLUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.servicecontainer.handler.RIProcessor;
//import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;


/**
 * @author Keith Suderman
 */
public abstract class MascAbstractDataSource implements DataSource
{
	static {
		try
		{
			// Force load the database driver.
			Class.forName("org.h2.Driver");
		}
		catch (ClassNotFoundException e)
		{
			// Ignore.  This is not an elegant place for the application
			// to fail.  We will let the first database access blow up
			// instead.
			e.printStackTrace();
		}
	}

	public static boolean testing = false;

	private final Logger logger;

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
//		this.metadata = loadMetadata("metadata/" + dsClass.getName() + ".json");

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
				Data d;

				sizeData.setDiscriminator(Uri.OK);
				sizeData.setPayload(size);
				result = Serializer.toJson(sizeData);
				break;
			case Uri.LIST:
				logger.debug("Fetching list");
				List<String> keys = index.keys();
				Map payload = (Map) map.get("payload");
				if (payload == null)
				{
					payload = new HashMap<String,String>();
				}
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
//							Data<String> stringData = new Data<String>();
//							stringData.setDiscriminator(returnType);
//							stringData.setPayload(content);
//							result = Serializer.toJson(stringData);
							result = packageContent(content);
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

	protected String packageContent(String content)
	{
		Data<String> data = new Data<String>(returnType, content);
		return data.asJson();
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
			Iterator<MimeHeader> iterator = headers.getAllHeaders();
			logger.debug("MIME Headers:");
			while (iterator.hasNext())
			{
				MimeHeader header = iterator.next();
				logger.debug("{} = {}", header.getName(), header.getValue());
			}

			String token = getToken(headers); // header.substring(7);
			if (token == null)
			{
				errorMessage = "No Authorization header found.";
				logger.debug(errorMessage);
				return false;
			}

			// Now try to find the token in the database.  If the token is not
			// in the database it has not been issued by us.
			Connection connection = null;
			Statement statement = null;
			boolean valid = false;
			try
			{
				String url = "jdbc:h2:/usr/share/h2/lapps-oauth;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE";
				String username = "lappsoauth";
				String password = "xkcdC@rt00Nz";
				connection = DriverManager.getConnection(url, username, password);
				statement = connection.createStatement();
				ResultSet result = statement.executeQuery("select * from Token t where t.token='" + token + "'");
				ResultSetMetaData metaData = result.getMetaData();
				int count = metaData.getColumnCount();

				while (result.next())
				{
					valid = true;
					Long id = result.getLong("ID");
					String access = result.getString("TOKEN");
					String clientId = result.getString("CLIENT_ID");
					logger.debug("Token from database: {} {}", clientId, access);
//					System.out.println("Token id   : " + id);
//					System.out.println("Acess token: " + access);
//					System.out.println("Client ID  : " + clientId);
				}
			}
			catch (SQLException e)
			{
				logger.error("Unable to access the database.", e);
				e.printStackTrace();
			}
			finally
			{
				if (statement != null) try {
					statement.close();
				}
				catch (SQLException ignored) { }
				if (connection != null) try
				{
					connection.close();
				}
				catch (SQLException ignored) { }
			}


			if (!valid)
			{
				errorMessage = "Invalid access token: " + token;
				//TODO this should return false. This is for testing only.
				return "123abc".equals(token);
			}
			logger.debug("Access token is valid.");
			return true;
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

//	public String getMetadata()
//	{
//		return metadata;
//	}

	protected DataSourceMetadata getCommonMetadata()
	{
		DataSourceMetadata md = new DataSourceMetadata();
		md.setAllow(Uri.ANY);
		md.setEncoding("UTF-8");
		md.setVersion(Version.getVersion());
		md.setVendor("http://www.anc.org");
		md.addLanguage("en-US");
		md.setLicense(Uri.CC_BY);
		return md;
	}

	protected String getToken(MimeHeaders headers)
	{
		String[] authorizations = headers.getHeader("Authorization");
		String token = checkAuthorizations(authorizations);
		if (token != null)
		{
			return token;
		}

		authorizations = headers.getHeader("X-Langrid-Service-Authorization");
		token = checkAuthorizations(authorizations);
		if (token != null)
		{
			return token;
		}
		logger.info("Unable to find an authorization header.");
		return null;
	}

	protected String checkAuthorizations(String[] authorizations)
	{
		if (authorizations == null || authorizations.length == 0)
		{
			return null;
		}
		for (String authorization : authorizations)
		{
			logger.debug("Checking authorization token: {}", authorization);
			if (authorization.startsWith("Bearer") || authorization.startsWith("bearer"))
			{
				return authorization.substring(7);
			}
		}
		return null;
	}

//	protected String loadMetadata(String metadataPath)
//	{
//		ClassLoader loader = this.getClass().getClassLoader();
////		System.out.println("Attempting to load metadata from " + metadataPath);
//		InputStream inputStream = loader.getResourceAsStream(metadataPath);
//		if (inputStream == null)
//		{
//			return error("Unable to locate metadata at: " + metadataPath);
//		}
//
//		String result;
//		try
//		{
//			UTF8Reader reader = new UTF8Reader(inputStream);
//			String json = reader.readString();
//			reader.close();
//			Data<String> data = new Data<String>();
//			data.setDiscriminator(Uri.META);
//			data.setPayload(json);
//			result = data.asJson();
//		}
//		catch (IOException e)
//		{
//			//return DataFactory.error("Unable to load metadata.", e);
//			result = new Error("Unable to load metadata.").asJson();
//		}
//		return result;
//	}

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
