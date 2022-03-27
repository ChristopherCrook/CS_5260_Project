import java.util.*;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

//! Class to test Hermes classes as the generate new nodes
//!
public class GenerateTest {

  //! Program Execution point
  public static void main(String[] args)
  {
    System.out.println("/--- Begin GenerateTest ---/");
    Country Picon = new Country();

    String test = new String("test");

    String file = new String("generatetest.csv");
    String picon_name = new String("Picon");

    String p_output = new String("GenerateTest_output.txt");

    Picon.schedule(picon_name, file, test, p_output, 1, 10, 10);


    ArrayBlockingQueue<Entry> queue = new ArrayBlockingQueue<>(10);
    Manager manager = new Manager();

    Manager.SetQueue(queue);
    Picon.SetQueue(queue);

    Thread mt = new Thread(manager);
    Thread pt = new Thread(Picon);

    mt.start();
    pt.start();

    Country.BEGIN = true;

    try {
      pt.join();
      manager.Shutdown();
      mt.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
    
    System.out.println("/---- End GenerateTest ----/");
  }
}
