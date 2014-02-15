package org.anc.lapps.masc.test;

import org.anc.lapps.masc.MascTextSource;
import org.lappsgrid.api.Data;
import org.lappsgrid.api.DataSource;

import org.junit.*;
import org.lappsgrid.discriminator.Types;

import java.io.IOException;

import static org.junit.Assert.*;

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
      Data query = new Data(Types.LIST);
      Data result = source.query(query);
      assertTrue("Result is null", result != null);
      assertTrue(result.getPayload(), result.getDiscriminator() != Types.ERROR);
//      System.out.println(result.getPayload());
   }

   @Test
   public void testGet()
   {
      Data query = new Data(Types.GET, "MASC3-0202");
      Data result = source.query(query);
      assertTrue("Result is null", result != null);
      assertTrue(result.getPayload(), result.getDiscriminator() != Types.ERROR);
      assertTrue("Datasource did not return a text document.", result.getDiscriminator() == Types.TEXT);
      System.out.println(result.getPayload());
   }
}
