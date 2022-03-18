import java.util.*;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

//! Class to test Hermes classes using the following conditions:
//!
//! There are three countries: Aerelon, Aquaria and Caprica
//! Aerelon needs people
//! Aquaria needs houses and the items to make them
//! Caprica is doing great except for surpluses
//!
public class Test2 {

  //! Program Execution point
  public static void main(String[] args)
  {
    System.out.println("/--- Begin Test 2 ---/");
    Country Aerelon = new Country();
    Country Aquaria = new Country();
    Country Caprica = new Country();
    Country Gemenon = new Country();

    String test = new String("test");

    String file = new String("test2.csv");
    String aerelon_name = new String("Aerelon");
    String caprica_name = new String("Caprica");
    String aquaria_name = new String("Aquaria");
    String gemenon_name = new String("Gemenon");

    String ae_output = new String(aerelon_name).concat(new String("_test2_output.txt"));
    String aq_output = new String(aquaria_name).concat(new String("_test2_output.txt"));
    String c_output = new String(caprica_name).concat(new String("_test2_output.txt"));
    String g_output = new String(gemenon_name).concat(new String("_test2_output.txt"));

    Aerelon.schedule(aerelon_name, file, test, ae_output, 1, 5, 5);
    Aquaria.schedule(aquaria_name, file, test, aq_output, 1, 5, 5);
    Caprica.schedule(caprica_name, file, test, c_output, 1, 5, 5);
    Gemenon.schedule(gemenon_name, file, test, g_output, 1, 5, 5);

    System.out.println("/--- Aerelon Status ---/");
    Aerelon.printStatus();
    System.out.println("/--- Aquaria Status ---/");
    Aquaria.printStatus();
    System.out.println("/--- Caprica Status ---/");
    Caprica.printStatus();
    System.out.println("/--- Gemenon Status ---/");
    Gemenon.printStatus();

    ArrayBlockingQueue<Entry> queue = new ArrayBlockingQueue<>(100);
    Manager manager = new Manager();

    Manager.SetQueue(queue);
    Aerelon.SetQueue(queue);
    Aquaria.SetQueue(queue);
    Caprica.SetQueue(queue);
    Gemenon.SetQueue(queue);

    Thread mt = new Thread(manager);
    Thread aet = new Thread(Aerelon);
    Thread aqt = new Thread(Aquaria);
    Thread ct = new Thread(Caprica);
    Thread gt = new Thread(Gemenon);

    mt.start();
    aet.start();
    aqt.start();
    ct.start();
    gt.start();

    Country.BEGIN = true;

    try {
      aet.join();
      aqt.join();
      ct.join();
      gt.join();
      manager.Shutdown();
      mt.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
    
    System.out.println("/---- End Test 2 ----/");
  }
}
