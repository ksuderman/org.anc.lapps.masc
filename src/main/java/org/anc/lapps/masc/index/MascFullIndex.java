package org.anc.lapps.masc.index;

import org.anc.index.api.Index;
import org.anc.index.core.IndexImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Keith Suderman
 */
public class MascFullIndex implements Index
{
   protected Index index;

   public MascFullIndex() throws IOException
   {
      index = new IndexImpl("full-masc3.index");
   }

   @Override
   public int size() { return index.size(); }
   @Override
   public File get(String key)
   {
      return index.get(key);
   }

   @Override
   public List<String> keys()
   {
      return index.keys();
   }

}
