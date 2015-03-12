package org.anc.lapps.masc;

import org.anc.lapps.masc.index.MascJsonIndex;
import org.lappsgrid.discriminator.Discriminators;
//import org.lappsgrid.experimental.annotations.DataSourceMetadata;

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
      super(new MascJsonIndex(), MascJsonSource.class, Discriminators.Uri.JSON_LD);
   }
}
