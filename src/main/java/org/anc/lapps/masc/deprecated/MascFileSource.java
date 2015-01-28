package org.anc.lapps.masc.deprecated;

import org.lappsgrid.api.*;

import org.anc.lapps.masc.index.MascFullIndex;
//import org.anc.lapps.core.AbstractDataSource;

import java.io.IOException;

/**
 * A @{link org.lappsgrid.api.DataSource DataSource} for accessing all files
 * in MASC.
 * <p>
 * The @{link org.and.lapps.masc.index.MascFullIndex MascFullIndex} uses the
 * document id with the annotation type appended as the key for the index.
 * For example
 *    MASC3-0202-txt
 *    MASC3-0202-hdr
 *    MASC3-0202-nc
 *
 * @author Keith Suderman
 */
public class MascFileSource //extends AbstractDataSource
{
   public MascFileSource() throws IOException
   {
//      super(new MascFullIndex());
   }

}
