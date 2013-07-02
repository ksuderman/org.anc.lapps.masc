package org.anc.lapps.masc.index;

import org.anc.index.api.Index;
import org.anc.index.core.*;

import java.io.File;
import java.util.List;

/**
 * @author Keith Suderman
 */
public class MascTextIndex implements Index
{
   protected Index index = new IndexImpl();

   public MascTextIndex()
   {

   }

   public File get(String id)
   {
      File headerFile = index.get(id);
      if (headerFile == null)
      {
         return null;
      }

      File parent = headerFile.getParentFile();
      String name = headerFile.getName().replace(".hdr", ".txt");
      return new File(parent, name);
   }

   public List<String> keys()
   {
      return index.keys();
   }
}
