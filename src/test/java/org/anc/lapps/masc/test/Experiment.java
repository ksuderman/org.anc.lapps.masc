package org.anc.lapps.masc.test;

import org.lappsgrid.discriminator.Types;

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

   public void run()
   {
      System.out.println("Get: " + Types.GET);
      System.out.println("List: " + Types.LIST);
   }

   public static void main(String[] args)
   {
      new Experiment().run();
   }
}
