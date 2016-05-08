package org.anc.lapps.masc.index;

import java.io.*;
import java.util.List;

import org.anc.index.api.Index;
import org.anc.index.core.IndexImpl;

/**
 * @author Keith Suderman
 */
public class MascHeaderIndex extends MascIndex
{
   public MascHeaderIndex() throws IOException
   {
//      index = new IndexImpl();
//      index.loadMasc3Index();
      super("masc3.index");
   }
}
