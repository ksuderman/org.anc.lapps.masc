package org.anc.lapps.masc;

import org.anc.lapps.core.AbstractDataSource;
import org.anc.lapps.masc.index.MascJsonIndex;
import org.lappsgrid.api.Data;
import org.lappsgrid.discriminator.Types;

import java.io.IOException;


/**
 * @author Jesse Stuart
 */
public class MascJsonSource extends AbstractDataSource
{

   public MascJsonSource() throws IOException
   {
      super(new MascJsonIndex());
   }

   @Override
   /**
    * The text data source either returns an error or the actual text.
    */
   protected Data get(String key)
   {
      Data result = super.get(key);
      if (result.getDiscriminator() != Types.ERROR)
      {
         result.setDiscriminator(Types.JSON);
      }
      return result;
   }
}
