package org.anc.lapps.masc;

import org.anc.lapps.masc.index.MascTextIndex;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.metadata.DataSourceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.lif.Container;

import java.io.IOException;

/** Returns the MASC document in a LIF Container object.
 *
 * @author Keith Suderman
 */
//@DataSourceMetadata(
//		  description = "Text files from the ANC's Manually Annotated SubCorpus in a JSON container",
//		  vendor = "http://www.anc.org",
//		  license = "cc-by",
//		  allow = "any",
//		  format = "lif",
//		  encoding = "utf-8",
//		  language = "en-US"
//)
public class MascLifSource extends MascAbstractDataSource
{
	public MascLifSource() throws IOException
	{
		super(new MascTextIndex(), MascLifSource.class, Discriminators.Uri.LAPPS);
	}

	@Override
	protected String packageContent(String content)
	{
		Container container = new Container();
		container.setLanguage("en-US");
		container.setText(content);
		return new DataContainer(container).asJson();
	}

	@Override
	public String getMetadata()
	{
		if (metadata == null)
		{
			DataSourceMetadata md = getCommonMetadata();
			md.setName(this.getClass().getName());
			md.setDescription("MASC text is a LIF container");
			md.addFormat(Discriminators.Uri.LAPPS);
			metadata = new Data<DataSourceMetadata>(Discriminators.Uri.META, md).asJson();
		}
		return metadata;
	}


}
