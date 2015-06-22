package org.anc.lapps.masc;

import org.anc.lapps.masc.index.MascJsonIndex;
import static org.lappsgrid.discriminator.Discriminators.Uri;
//import org.lappsgrid.experimental.annotations.DataSourceMetadata;
import org.lappsgrid.metadata.DataSourceMetadata;
import org.lappsgrid.serialization.Data;

import java.io.IOException;


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

}
