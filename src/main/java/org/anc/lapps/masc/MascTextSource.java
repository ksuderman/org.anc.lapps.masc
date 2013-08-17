package org.anc.lapps.masc;

import org.anc.index.Index;
import org.anc.lapps.masc.index.MascTextIndex;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A {@link org.lappsgrid.api.DataSource DataSource} for accessing the text
 * files from the MASC.
 *
 * @author Keith Suderman
 */
public class MascTextSource extends AbstractDataSource
{
   public MascTextSource() throws IOException
   {
      super(new MascTextIndex());
   }
}
