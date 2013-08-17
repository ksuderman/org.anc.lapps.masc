package org.anc.lapps.masc.test;

import org.lappsgrid.discriminator.Types;

import java.io.UnsupportedEncodingException;
import javax.xml.bind.DatatypeConverter;

/**
 * This class is intended for one-off testing and/or experimentation.
 *
 * @author Keith Suderman
 */
public class Experiment
{
   public Experiment()
   {

   }

   public void run() throws UnsupportedEncodingException
   {
      String string = "Hello world.";
      byte[] bytes = string.getBytes("UTF8");
      String encoded = DatatypeConverter.printBase64Binary(bytes);
      byte[] decoded = DatatypeConverter.parseBase64Binary(encoded);
      System.out.println("before: " + string);
      System.out.println("base64: " + encoded);
      System.out.println("after : " + new String(decoded));
      System.out.println("Get: " + Types.GET);
      System.out.println("List: " + Types.LIST);
   }

   public static void main(String[] args)
   {
      try
      {
         new Experiment().run();
      }
      catch (UnsupportedEncodingException e)
      {
         e.printStackTrace();
      }
   }
}
