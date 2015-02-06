package org.anc.lapps.masc.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.anc.lapps.masc.MascJsonSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.serialization.Serializer;

import static org.lappsgrid.discriminator.Discriminators.Uri;

public class MascJsonSourceTest
{

   protected DataSource source;

   @Before
   public void before() throws IOException
   {
      source = new MascJsonSource();
   }

   @After
   public void after()
   {
      source = null;
   }

   @Test
   public void testList()
   {
      System.out.println("MascJsonSourceTest.testList");
      String listCommand = DataFactory.list();
      String response = source.execute(listCommand);
      Map<String,Object> data = Serializer.parse(response, HashMap.class);
      Object discriminator = data.get("discriminator");
      assertNotNull("No discriminator returned.", discriminator);
      assertEquals("Wrong discriminator returned", Uri.STRING_LIST, discriminator);
      java.util.List<String> payload = (java.util.List<String>) data.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload.size() > 0);
   }

   @Test
   public void testGet()
   {
      System.out.println("MascJsonSourceTest.testGet");
      String getCommand = DataFactory.get("MASC3-0290");
      String response = source.execute(getCommand);
      Map<String,Object> map = Serializer.parse(response, HashMap.class);
      Object discriminator = map.get("discriminator");
      assertNotNull("No discriminator returned.", discriminator);
      assertEquals("Wrong discriminator type returned.", Uri.JSON_LD, discriminator);
      Object payload = map.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload instanceof String);
      System.out.println(payload);
   }
}
