package org.anc.lapps.masc.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.anc.lapps.masc.MascJsonSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.api.WebService;
//import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.Constants;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.datasource.Get;
import org.lappsgrid.serialization.datasource.List;

public class MascJsonSourceTest
{

   protected WebService source;

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
      String listCommand = Serializer.toJson(new List());
      String response = source.execute(listCommand);
      Map<String,Object> data = Serializer.parse(response, HashMap.class);
      Object discriminator = data.get("discriminator");
      assertNotNull("No discriminator returned.", discriminator);
      assertEquals("Wrong discriminator returned", Constants.Uri.OK, discriminator);
      java.util.List<String> payload = (java.util.List<String>) data.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload.size() > 0);
//      System.out.println(payload.toString());
//      assertTrue(data.getPayload(), data.getDiscriminator() != Types.ERROR);
//      String type = DiscriminatorRegistry.get(data.getDiscriminator());
//      System.out.println("Type is " + type);
//      System.out.println("Payload: " + data.getPayload());
//      String[] parts = data.getPayload().split("\\s+");
//      assertTrue("Invalid payload", parts.length > 0);
   }

   @Test
   public void testGet()
   {
      System.out.println("MascJsonSourceTest.testGet");
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
//      Data data = source.query(DataFactory.get("MASC3-0290"));
//      assertTrue(data.getPayload(), data.getDiscriminator() != Types.ERROR);
//      String name = DiscriminatorRegistry.get(data.getDiscriminator());
//      System.out.println("Type is " + name);
//      String payload = data.getPayload();
//      assertTrue("Null payload.", payload != null);
//      assertTrue("Empty payload", payload.length() != 0);
   }
}
