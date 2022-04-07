import java.util.*;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

//! Class to test Hermes classes
//!
public class Test4 {

  //! Program Execution point
  public static void main(String[] args)
  {
    System.out.println("/--- Begin Test 4 ---/");

    ArrayList<String> names = new ArrayList<>();
    
    String test = new String("test");
    String output = new String("_test4_output.txt");
    String file = new String("test4.csv");
    
    ArrayBlockingQueue<Entry> queue = new ArrayBlockingQueue<>(100);
    Manager manager = new Manager();
    manager.SetQueue(queue);
    
    Thread mt = new Thread(manager);
    
    String Aerelon = new String("Aerelon");
    String Aquaria = new String("Aquaria");
    String Caprica = new String("Caprica");
    String Tauron = new String("Tauron");
    String Gemenon = new String("Gemenon");
    String Leonis = new String("Leonis");
    String Libris = new String("Libris");
    String Canceron = new String("Canceron");
    String Picon = new String("Picon");
    String Sagittaron = new String("Sagittaron");
    String Scorpia = new String("Scorpia");
    String Virgon = new String("Virgon");
    
    names.add(Aerelon);
    names.add(Aquaria);
    names.add(Caprica);
    names.add(Gemenon);
    names.add(Libris);
    names.add(Tauron);
    names.add(Leonis);
    names.add(Canceron);
    names.add(Picon);
    names.add(Sagittaron);
    names.add(Scorpia);
    names.add(Virgon);
    
    LinkedList<Country> countries = new LinkedList<>();
    LinkedList<Thread> threads = new LinkedList<>();
    
    for (String s : names)
    {
      Country p = new Country();
      p.SetQueue(queue);
      p.schedule(
        s,
        file,
        test,
        new String(s).concat(output),
        3,
        20,
        20
      );
      //p.printStatus();
      countries.add(p);
    }
    
    System.out.println("Number of countries: " + countries.size());

    for (Country c : countries)
    {
      Thread t = new Thread(c);
      threads.add(t);
    }
    
    for (Thread tr : threads)
      tr.start();
      
    mt.start();

    Country.BEGIN = true;
    
    while (queue.size() > 0)
    {
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException ie)
      {
        System.out.println("Thread interrupted");
      }
    }

    try {
      for (Thread tr : threads)
        tr.join();
        
      manager.Shutdown();
      mt.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
    
    System.out.println("/---- End Test 4 ----/");
  }
}
