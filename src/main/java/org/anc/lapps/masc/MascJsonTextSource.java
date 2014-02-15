package org.anc.lapps.masc;

import org.anc.lapps.core.AbstractDataSource;
import org.anc.lapps.masc.index.MascTextIndex;
import org.anc.lapps.serialization.Container;
import org.lappsgrid.api.Data;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Keith Suderman
 */
public class MascJsonTextSource extends AbstractDataSource
{
   private static final Logger logger = LoggerFactory.getLogger(MascJsonTextSource.class);

   public MascJsonTextSource() throws IOException
   {
      super(new MascTextIndex());
   }

   @Override
   /**
    * The text data source either returns an error or the actual text.
    */
   protected Data get(String key)
   {
      logger.debug("Getting text for " + key);
      Data result = super.get(key);
      if (result.getDiscriminator() == Types.ERROR)
      {
         logger.error(result.getPayload());
      }
      else
      {
         logger.debug("Preparing JSON document.");
         result.setDiscriminator(Types.JSON);
         logger.debug("Discriminator set.");

         Container container = null;
         try
         {
            container = new Container();
         }
         catch (RuntimeException e)
         {
            logger.error("Runtime exception creating container.", e);
            throw e;
         }
         catch (Exception e)
         {
            logger.error("Exception creating container.", e);
         }
         logger.debug("Created container.");
         container.setText(result.getPayload());
         logger.debug("Serializing to JSON.");
         result.setPayload(container.toJson());
      }
      logger.debug("Returning {}", DiscriminatorRegistry.get(result.getDiscriminator()));
      return result;
   }

}
