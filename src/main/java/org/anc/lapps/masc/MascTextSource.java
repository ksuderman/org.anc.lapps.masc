package org.anc.lapps.masc;

import org.anc.lapps.masc.index.MascTextIndex;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.experimental.annotations.DataSourceMetadata;

import java.io.IOException;

/**
 * A {@link org.lappsgrid.api.DataSource DataSource} for accessing the text
 * files from the MASC.
 *
 * @author Keith Suderman
 */
@DataSourceMetadata(
        description = "Text files from the ANC's Manually Annotated SubCorpus in a JSON container",
        vendor = "http://www.anc.org",
        license = "cc-by",
        allow = "any",
        format = "text",
        encoding = "utf-8",
        language = "en-US"
)
public class MascTextSource extends MascAbstractDataSource
{
   public MascTextSource() throws IOException
   {
   	super(new MascTextIndex(), MascTextSource.class, Discriminators.Uri.TEXT);
   }

}
