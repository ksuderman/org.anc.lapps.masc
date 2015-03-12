package org.anc.lapps.masc.test;

import org.anc.lapps.masc.MascAbstractDataSource;
import org.anc.lapps.masc.MascTextSource;
import org.lappsgrid.api.DataSource;

import org.junit.*;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.serialization.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.lappsgrid.discriminator.Discriminators.Uri;

/**
 * @author Keith Suderman
 */
public class MascTextSourceTest
{
   private DataSource source;

   public MascTextSourceTest()
   {

   }

   @Before
   public void setup() throws IOException
   {
		MascAbstractDataSource.testing = true;
      source = new MascTextSource();
   }

   @After
   public void teardown()
   {
      source = null;
   }

   @Test
   public void testList()
   {
      System.out.println("MascTextSourceTest.testList");
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
   public void testParameterizedList()
   {
      System.out.println("MascTextSourceTest.testList");
      String listCommand = DataFactory.list(0, 10);
      String response = source.execute(listCommand);
      Map<String,Object> data = Serializer.parse(response, HashMap.class);
      Object discriminator = data.get("discriminator");
      assertNotNull("No discriminator returned.", discriminator);
      assertEquals("Wrong discriminator returned", Uri.STRING_LIST, discriminator);
      java.util.List<String> payload = (java.util.List<String>) data.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload.size() == 10);
   }

   @Test
   public void testGet()
   {
      System.out.println("MascTextSourceTest.testGet");
      String getCommand = DataFactory.get("MASC3-0290");
      String response = source.execute(getCommand);
      Map<String,Object> map = Serializer.parse(response, HashMap.class);
      Object discriminator = map.get("discriminator");
      assertNotNull("No discriminator returned.");
      assertEquals("Wrong discriminator type returned.", Uri.TEXT, discriminator);
      Object payload = map.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload instanceof String);
      System.out.println(payload);
   }
}
