package org.anc.lapps.masc;

import org.anc.index.api.Index;
import org.anc.io.UTF8Reader;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.Uri;


/**
 * @author Keith Suderman
 */
public abstract class MascAbstractDataSource implements DataSource
{
	private final Logger logger; // = LoggerFactory.getLogger(MascAbstractDataSource.class);
	protected Index index;

	/** Metadata for the service is cached in this field so it does not need to be read for disk every request. */
	protected String metadata;
	/** The type of data returned by this service. */
	protected String returnType;
	/** The number of documents managed by this data source. */
	protected final int size;

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
		Map<String,Object> map = Serializer.parse(input, HashMap.class);
		String discriminator = (String) map.get("discriminator");
		if (discriminator == null)
		{
			logger.error("No discriminator present in request.");
			return Serializer.toJson(new org.lappsgrid.serialization.Error("No discriminator value provided."));
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
						}

				}
				break;
			case Uri.GETMETADATA:
				logger.warn("Deprecated discriminator GETMETADATA used.");
				result = metadata;
				break;
			default:
				String message = String.format("Invalid discriminator: %s, Uri.List is %s", discriminator, Uri.LIST);
				logger.warn(message);
				result = error(message);
				break;
		}
		logger.trace("Returning result {}", result);
		return result;
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
			result = Serializer.toJson(data);
		}
		catch (IOException e)
		{
			//return DataFactory.error("Unable to load metadata.", e);
			result = Serializer.toJson(new Error("Unable to load metadata."));
		}
		return result;
	}

	protected String error(String message)
	{
		return Serializer.toJson(new Error(message));
	}
}
