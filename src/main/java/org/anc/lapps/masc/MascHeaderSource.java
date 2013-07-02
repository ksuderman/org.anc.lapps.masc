package org.anc.lapps.masc;

import org.anc.index.api.Index;
import org.anc.io.UTF8Reader;
import org.anc.lapps.masc.index.MascHeaderIndex;
import org.lappsgrid.api.Data;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

//import org.lappsgrid.discriminator.Types;

/**
 * A DataSource that returns documents from the MASC.
 * <p>
 * Each MASC document is identified by the @docId attribute in the
 * document header file.
 * @author Keith Suderman
 *
 */
public class MascHeaderSource extends AbstractDataSource
{
   private final Logger logger = LoggerFactory.getLogger(MascHeaderSource.class);
   private Index index;

   public MascHeaderSource() throws IOException
   {
      super();
      index = new MascHeaderIndex();
      logger.info("Creating a MASC data source.");
   }

   protected Data get(String key)
   {
      logger.info("Getting document for {}", key);
      Index.Entry entry = index.getById(key);
      if (entry == null)
      {
         logger.error("No such file.");
         return DataFactory.error("No such file.");
      }
      File file = new File(entry.getPath());
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
         logger.debug("Performing get: {}", query.getPayload());
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
   
}
