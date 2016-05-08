package org.anc.lapps.masc;

import org.anc.io.UTF8Reader;
import org.anc.lapps.masc.index.MascJsonIndex;
import static org.lappsgrid.discriminator.Discriminators.Uri;
//import org.lappsgrid.experimental.annotations.DataSourceMetadata;
import org.lappsgrid.metadata.DataSourceMetadata;
import org.lappsgrid.serialization.*;
import org.lappsgrid.serialization.lif.Container;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Jesse Stuart
 * @author Keith Suderman
 */
//@DataSourceMetadata(
//        description = "Annotations from the ANC's Manually Annotated SubCorpus in a JSON container",
//		  vendor = "http://www.anc.org",
//		  license = "cc-by",
//		  allow = "any",
//		  format = "lapps",
//		  encoding = "utf-8",
//		  language = "en-US"
//)
public class MascJsonSource extends MascAbstractDataSource
{

   public MascJsonSource() throws IOException
   {
      super(new MascJsonIndex(), MascJsonSource.class, Uri.JSON_LD);
   }

   @Override
   public String getMetadata()
   {
      if (metadata == null)
      {
         DataSourceMetadata md = getCommonMetadata();
			md.setName(this.getClass().getName());
			md.setDescription("All annotations from MASC in a LIF container");
			md.addFormat(Uri.LAPPS);
			metadata = new Data<DataSourceMetadata>(Uri.META, md).asJson();
      }
		return metadata;
   }

	@Override
   public String execute(String input)
   {
      logger.debug("Executing request: {}", input);

      Map<String,Object> map = Serializer.parse(input, HashMap.class);
      String discriminator = (String) map.get("discriminator");
      if (discriminator == null)
      {
         logger.error("No discriminator present in request.");
         return new org.lappsgrid.serialization.Error("No discriminator value provided.").asJson();
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
							Data<Map> data = new Data<>();
							data.setDiscriminator(Uri.LAPPS);
							data.setPayload(Serializer.parse(content, Map.class));
                     result = data.asJson();
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
         case Uri.QUERY:
            result = query(map.get("payload").toString());
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

}
