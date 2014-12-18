package org.anc.lapps.masc;

import org.anc.lapps.core.AbstractDataSource;
import org.anc.lapps.masc.index.MascTextIndex;

import java.io.IOException;

/**
 * A {@link org.lappsgrid.api.DataSource DataSource} for accessing the text
 * files from the MASC.
 *
 * @author Keith Suderman
 */
public class MascTextSource extends MascAbstractDataSource
{
   public MascTextSource() throws IOException
   {
   	super(new MascTextIndex(), MascTextSource.class);
   }

}
