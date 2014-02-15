package org.anc.lapps.masc;

import org.lappsgrid.api.*;

import org.anc.lapps.masc.index.MascFullIndex;
import org.anc.lapps.core.AbstractDataSource;

import java.io.IOException;

/**
 * A @{link org.lappsgrid.api.DataSource DataSource} for accessing all files
 * in MASC.
 *
 * @author Keith Suderman
 */
//@WebService
public class MascFileSource extends AbstractDataSource
{
   public MascFileSource() throws IOException
   {
      super(new MascFullIndex());
   }

}
