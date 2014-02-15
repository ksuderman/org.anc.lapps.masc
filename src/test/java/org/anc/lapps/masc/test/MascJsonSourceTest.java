package org.anc.lapps.masc.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.anc.lapps.masc.MascDataSource;
import org.anc.lapps.masc.MascJsonSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.DataSource;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;

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
      Data data = source.query(DataFactory.list());
      assertTrue(data.getPayload(), data.getDiscriminator() != Types.ERROR);
      String type = DiscriminatorRegistry.get(data.getDiscriminator());
      System.out.println("Type is " + type);
      System.out.println("Payload: " + data.getPayload());
      String[] parts = data.getPayload().split("\\s+");
      assertTrue("Invalid payload", parts.length > 0);
//      System.out.println("Array size: " + parts.length );
   }

   @Test
   public void testGet()
   {
      Data data = source.query(DataFactory.get("MASC3-0290"));
      assertTrue(data.getPayload(), data.getDiscriminator() != Types.ERROR);
      String name = DiscriminatorRegistry.get(data.getDiscriminator());
      System.out.println("Type is " + name);
      String payload = data.getPayload();
      assertTrue("Null payload.", payload != null);
      assertTrue("Empty payload", payload.length() != 0);
//      System.out.println("Payload " + data.getPayload());
   }
}
