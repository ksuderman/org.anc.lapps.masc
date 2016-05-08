package org.anc.lapps.masc.test;

import org.anc.lapps.masc.MascAbstractDataSource;
import org.anc.lapps.masc.MascJsonSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.lappsgrid.discriminator.Discriminators.Uri;

@Ignore
public class MascJsonSourceTest
{

   protected DataSource source;

   @Before
   public void before() throws IOException
   {
		MascAbstractDataSource.testing = true;
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
      Map<String,Object> data = Serializer.parse(response, Map.class);
      Object discriminator = data.get("discriminator");
      assertNotNull("No discriminator returned.", discriminator);
      assertEquals("Wrong discriminator returned", Uri.STRING_LIST, discriminator);
      List<String> payload = (List<String>) data.get("payload");
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
      assertEquals("Wrong discriminator type returned.", Uri.LAPPS, discriminator);
      Object payload = map.get("payload");
      assertNotNull("No payload returned.", payload);
      assertTrue(payload instanceof Map);
      //System.out.println(payload);
   }
}
