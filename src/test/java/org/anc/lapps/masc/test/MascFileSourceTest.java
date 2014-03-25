package org.anc.lapps.masc.test;

import org.anc.lapps.masc.MascFileSource;
import org.junit.*;
import org.lappsgrid.api.*;
import org.lappsgrid.core.DataFactory;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.discriminator.Types;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Keith Suderman
 */
public class MascFileSourceTest
{
   protected DataSource source;

   @Before
   public void before() throws IOException
   {
      source = new MascFileSource();
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
      Data data = source.query(DataFactory.get("MASC3-0290-logical"));
      assertTrue(data.getPayload(), data.getDiscriminator() != Types.ERROR);
      String name = DiscriminatorRegistry.get(data.getDiscriminator());
      System.out.println("Type is " + name);
      String payload = data.getPayload();
      assertTrue("Null payload.", payload != null);
      assertTrue("Empty payload", payload.length() != 0);
//      System.out.println("Payload " + data.getPayload());
   }

   @Test
   public void testQuerySpoken() throws IOException
   {
      System.out.println("MascFileSourceTest.testQuerySpoken");
      String[] parts = get("spoken");
      assertTrue(parts != null);
      assertTrue(parts.length > 0);
      int i = 0;
      for (String part : parts)
      {
         System.out.println(++i + ": " + part);
      }
   }

   @Test
   public void testQueryAll() throws IOException
   {
      String[] parts = get("/data/");
      assertTrue("Null parts", parts != null);
      assertTrue("Empty parts list.", parts.length > 0);
      String[] spoken = get("/spoken/");
      String[] written = get("/written/");
      System.out.println("Parts: " + parts.length);
      System.out.println("Spoken: " + spoken.length);
      System.out.println("Written: " + written.length);
      assertTrue(parts.length == (spoken.length + written.length));
   }

   protected String[] get(String query) throws IOException
   {
      Data data = source.query(DataFactory.query(query));
      if (data.getDiscriminator() == Types.ERROR)
      {
         throw new IOException(data.getPayload());
      }
      return data.getPayload().split("\\s+");
   }
}
