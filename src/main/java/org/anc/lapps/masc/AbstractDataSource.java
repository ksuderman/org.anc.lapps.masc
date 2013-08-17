package org.anc.lapps.masc;

import org.anc.index.api.Index;
import org.anc.io.UTF8Reader;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Keith Suderman
 */
public abstract class AbstractDataSource implements DataSource
{
   private final Logger logger = LoggerFactory.getLogger(AbstractDataSource.class);
   protected Index index;
   protected Throwable savedException;

   public AbstractDataSource(Index index)
   {
      this.index = index;
   }

   protected Data list()
   {
      return new Data(Types.INDEX, collect(index.keys()));
   }

   protected Data get(String key)
   {
      logger.info("Getting document for {}", key);
      File file = index.get(key);
      if (file == null)
      {
         logger.error("No such file.");
         return DataFactory.error("No such file.");
      }

      if (!file.exists())
      {
         logger.error("File not found.");
         return DataFactory.error("File not found.");
      }

      UTF8Reader reader = null;
      String payload = null;
      long type = decode(key);
      try
      {
         logger.debug("Loading {}", file.getPath());
         reader = new UTF8Reader(file);
         payload = reader.readString();
         reader.close();
      }
      catch (IOException e)
      {
         logger.error("Unable to load file.", e);
         type = Types.ERROR;
         payload = e.getMessage();
      }
      logger.debug("Returning the Data object.");
      return new Data(type, payload);
   }


   @Override
   public Data query(Data query)
   {
      if (savedException != null)
      {
         return DataFactory.error(savedException.getMessage());
      }

      logger.info("Query type: {}", DiscriminatorRegistry.get(query.getDiscriminator()));
      logger.info("Query payload: {}", query.getPayload());
      Data result;
      long type = query.getDiscriminator();
      if (type == Types.QUERY)
      {
         logger.debug("Performing query: {}", query.getPayload());
         result = doQuery(query.getPayload());
      }
      else if (type == Types.LIST)
      {
         logger.debug("Listing data source.");
         result = list();
      }
      else if (type == Types.GET)
      {
         logger.debug("Performing get({}): {}", Types.GET, query.getPayload());
         result = get(query.getPayload());
      }
      else
      {
         String name = DiscriminatorRegistry.get(query.getDiscriminator());
         logger.warn("Unknown query type: {}", name);
         result = DataFactory.error("Unknown query type: " + name);
      }
      return result;

   }

   protected long decode(String key)
   {
      if (key.endsWith("txt"))
      {
         return Types.TEXT;
      }
      else if (key.endsWith("hdr"))
      {
         return Types.XML;
      }
      return Types.GRAF;
   }

   protected Data doQuery(String queryString)
   {
      List<String> list = new ArrayList<String>();
      for (String key : index.keys())
      {
         File file = index.get(key);
         if (file.getPath().contains(queryString))
         {
            list.add(key);
         }
      }
      return DataFactory.index(collect(list));
   }

//   protected long decode(String key)
//   {
//      if (key.endsWith("txt"))
//      {
//         return Types.TEXT;
//      }
//      else if (key.endsWith("hdr"))
//      {
//         return Types.XML;
//      }
//      return Types.GRAF;
//   }

   /**
    * Takes a list of String objects and concatenates them into
    * a single String. Items in the list are separated by a single
    * space character.
    *
    */
   private String collect(Collection<String> list)
   {
      StringBuilder buffer = new StringBuilder();
      Iterator<String> it = list.iterator();
      if (it.hasNext())
      {
         buffer.append(it.next());
      }
      while (it.hasNext())
      {
         buffer.append(' ');
         buffer.append(it.next());
      }
      return buffer.toString();
   }

}
