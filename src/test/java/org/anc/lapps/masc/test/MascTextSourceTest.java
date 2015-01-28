package org.anc.lapps.masc.test;

import org.anc.lapps.masc.MascTextSource;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.DataSource;

import org.junit.*;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Constants;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.datasource.Get;
import org.lappsgrid.serialization.datasource.List;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Keith Suderman
 */
public class MascTextSourceTest
{
   private WebService source;

   public MascTextSourceTest()
   {

   }

   @Before
   public void setup() throws IOException
   {
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
      String listCommand = Serializer.toJson(new List());
      String response = source.execute(listCommand);
      Map<String,Object> data = Serializer.parse(response, HashMap.class);
      Object discriminator = data.get("discriminator");
      assertNotNull("No discriminator returned.", discriminator);
      assertEquals("Wrong discriminator returned", Constants.Uri.OK, discriminator);
      java.util.List<String> payload = (java.util.List<String>) data.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload.size() > 0);
   }

   @Test
   public void testGet()
   {
      System.out.println("MascTextSourceTest.testGet");
      String getCommand = Serializer.toJson(new Get(null, "MASC3-0290"));
      String response = source.execute(getCommand);
      Map<String,Object> map = Serializer.parse(response, HashMap.class);
      Object discriminator = map.get("discriminator");
      assertNotNull("No discriminator returned.");
      assertEquals("Wrong discriminator type returned.", Constants.Uri.OK, discriminator);
      Object payload = map.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload instanceof String);
      System.out.println(payload);
   }
}
