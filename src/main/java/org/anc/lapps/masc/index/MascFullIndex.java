package org.anc.lapps.masc.index;

import org.anc.index.api.Index;
import org.anc.index.core.IndexImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Keith Suderman
 */
public class MascFullIndex extends MascIndex
{
   public MascFullIndex() throws IOException
   {
      super("full-masc3.index");
   }
}
