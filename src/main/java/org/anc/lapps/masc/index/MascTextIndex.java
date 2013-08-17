package org.anc.lapps.masc.index;

import org.anc.index.api.Index;
import org.anc.index.core.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Keith Suderman
 */
public class MascTextIndex implements Index
{
   protected Index index;

   public MascTextIndex() throws IOException
   {
      index = new IndexImpl("masc3.index");
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
      return new ArrayList<String>(index.keys());
   }
}
