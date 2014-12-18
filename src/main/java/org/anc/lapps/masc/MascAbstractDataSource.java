package org.anc.lapps.masc;

/**
 * @author Keith Suderman
 */

import org.anc.index.api.Index;
import org.anc.io.UTF8Reader;
import org.lappsgrid.discriminator.Constants;
import org.lappsgrid.serialization.*;
import org.lappsgrid.serialization.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class MascAbstractDataSource 
{
	private final Logger logger; // = LoggerFactory.getLogger(MascAbstractDataSource.class);
	protected Index index;

	protected String metadata;
	protected final int size;

	public MascAbstractDataSource(Index index, Class<? extends MascAbstractDataSource> dsClass)
	{
		this.index = index;
		this.logger = LoggerFactory.getLogger(dsClass);
		this.size = index.keys().size();
		this.metadata = loadMetadata("/metadata/" + dsClass.getName() + ".json");
	}

	public String execute(String input)
	{
		Map<String,Object> map = Serializer.parse(input, HashMap.class);
		String discriminator = (String) map.get("discriminator");
		if (discriminator == null)
		{
			return Serializer.toJson(new org.lappsgrid.serialization.Error("No discriminator value provided."));
		}

		String result = null;
		switch (discriminator)
		{
			case Constants.Uri.SIZE:
				Data<Integer> sizeData = new Data<Integer>();
				sizeData.setDiscriminator(Constants.Uri.OK);
				sizeData.setPayload(size);
				result = Serializer.toJson(sizeData);
				break;
			case Constants.Uri.LIST:
				java.util.List<String> keys = index.keys();
				Object startValue = map.get("start");
				if (startValue != null)
				{
					int start = Integer.parseInt(startValue.toString());
					int end = index.keys().size();
					Object endValue = map.get("end");
					if (endValue != null)
					{
						end = Integer.parseInt(endValue.toString());
					}
					keys = keys.subList(start, end);
				}
				Data<java.util.List<String>> listData = new Data<>();
				listData.setDiscriminator(Constants.Uri.OK);
				listData.setPayload(keys);
				result = Serializer.toJson(listData);
				break;
			case Constants.Uri.GET:
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
							UTF8Reader reader = new UTF8Reader(file);
							String content = reader.readString();
							reader.close();
							Data<String> stringData = new Data<String>();
							stringData.setDiscriminator(Constants.Uri.OK);
							stringData.setPayload(content);
							result = Serializer.toJson(stringData);
						}
						catch (IOException e)
						{
							result = error(e.getMessage());
						}

				}
				break;
			case Constants.Uri.GETMETADATA:
				Data<String> data = new Data<String>();
				data.setDiscriminator(Constants.Uri.OK);
				data.setPayload(metadata);
				result = Serializer.toJson(data);
				break;
			default:
				result = error("Invalid discriminator: " + discriminator);
				break;
		}
		return result;
	}

	protected String loadMetadata(String metadataPath)
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
		{
			loader = this.getClass().getClassLoader();
		}
//		System.out.println("Attempting to load metadata from " + metadataPath);
		InputStream inputStream = loader.getResourceAsStream(metadataPath);
		if (inputStream == null)
		{
			return error("Unable to locate metadata.");
		}

		String result;
		try
		{
			UTF8Reader reader = new UTF8Reader(inputStream);
			String json = reader.readString();
			reader.close();
			Data<String> data = new Data<String>();
			data.setDiscriminator(Constants.Uri.OK);
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
