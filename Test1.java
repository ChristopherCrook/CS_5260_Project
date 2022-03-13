import java.util.*;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

//! Class to test Hermes classes
public class Test1 {

  //! Program Execution point
  public static void main(String[] args)
  {
    System.out.println("/--- Begin Test 1 ---/");
    Country Picon = new Country();
    Country Caprica = new Country();

    String test = new String("test");

    String file = new String("test1.csv");
    String picon_name = new String("Picon");
    String caprica_name = new String("Caprica");

    String p_output = new String(picon_name).concat(new String("_test1_output.txt"));
    String c_output = new String(caprica_name).concat(new String("_test1_output.txt"));

    Picon.schedule(picon_name, file, test, p_output, 1, 5, 5);
    Caprica.schedule(caprica_name, file, test, c_output, 1, 5, 5);

    System.out.println("/--- Picon Status ---/");
    Picon.printStatus();
    System.out.println("/--- Caprica Status ---/");
    Caprica.printStatus();

    ArrayBlockingQueue<Entry> queue = new ArrayBlockingQueue<>(10);
    Manager manager = new Manager();

    Manager.SetQueue(queue);
    Picon.SetQueue(queue);
    Caprica.SetQueue(queue);

    Thread mt = new Thread(manager);
    Thread pt = new Thread(Picon);
    Thread ct = new Thread(Caprica);

    mt.start();
    pt.start();
    ct.start();

    Country.BEGIN = true;

    try {
      pt.join();
      ct.join();
      manager.Shutdown();
      mt.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
    
    System.out.println("/---- End Test 1 ----/");
  }
}
