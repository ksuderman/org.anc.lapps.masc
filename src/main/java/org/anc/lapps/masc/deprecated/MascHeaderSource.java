package org.anc.lapps.masc.deprecated;

import org.anc.lapps.core.AbstractDataSource;
import org.anc.lapps.masc.index.MascHeaderIndex;

import java.io.IOException;

/**
 * A {@link org.lappsgrid.api.DataSource DataSource} that returns
 * header files from MASC.
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
