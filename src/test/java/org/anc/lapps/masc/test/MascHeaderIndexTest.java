package org.anc.lapps.masc.test;

import org.anc.index.api.Index;
import org.anc.lapps.masc.index.MascHeaderIndex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Keith Suderman
 */
public class MascHeaderIndexTest
{
   protected Index index;

   @Before
   public void before() throws IOException
   {
      index = new MascHeaderIndex();
   }

   @After
   public void after()
   {
      index = null;
   }

   @Test
   public void testSize()
   {
      List<String> keys = index.keys();
      assertTrue("Keys size: " + keys.size(), keys.size() == 392);
   }

   @Test
   public void testUnique()
   {
      List<String> list1 = index.keys();
      List<String> list2 = index.keys();
      assertTrue("index.keys() returns the same object.", list1 != list2);
   }

   @Test
   public void testFiles()
   {
      List<String> keys = index.keys();
      for (String key : keys)
      {
         File file = index.get(key);
         assertTrue("Invalid file name : " + file.getName(), file.getName().endsWith(".hdr"));
         assertTrue("File not found : " + file.getPath(), file.exists());
      }
   }
}
