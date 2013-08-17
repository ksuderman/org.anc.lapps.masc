package org.anc.lapps.masc.test;

import org.anc.lapps.masc.index.MascTextIndex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.anc.index.api.Index;

import java.io.*;
import java.util.List;

/**
 * @author Keith Suderman
 */
public class MascTextIndexTest
{
   protected Index index;

   @Before
   public void before() throws IOException
   {
      index = new MascTextIndex();
   }

   @After
   public void after()
   {
      index = null;
   }

   @Test
   public void testKeys()
   {
      List<String> keys1 = index.keys();
      assertTrue("index.keys() returned null.", keys1 != null);
      List<String> keys2 = index.keys();
      assertTrue("index.keys() returned null.", keys2 != null);
      assertTrue("index.keys() returned the same list object.", keys1 != keys2);
      assertTrue("Keys size is " + keys1.size(), keys1.size() == 392);
   }

   @Test
   public void testFiles()
   {
      List<String> keys = index.keys();
      for (String key : keys)
      {
         File file = index.get(key);
         assertTrue("Wrong file type: " + file.getPath(), file.getName().endsWith(".txt"));
         assertTrue("No file for key: " + key, file != null);
         assertTrue("File not found: " + file.getPath(), file.exists());
      }
   }
}
