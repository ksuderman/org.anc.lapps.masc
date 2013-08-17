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
 * A {@link org.lappsgrid.api.DataSource DataSource} that returns
 * header files from the MASC.
 *
 * @author Keith Suderman
 *
 */
public class MascHeaderSource extends AbstractDataSource
{
   public MascHeaderSource() throws IOException
   {
      super(new MascHeaderIndex());
   }
}
