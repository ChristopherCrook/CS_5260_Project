import java.util.*;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

//! Class to test Hermes classes
public class TestRunner {

  //! Test the Resource class
  public static boolean TestResource()
  {
    System.out.println("/--- Begin Resource Test ---/");
    Resource r1 = new Resource();
    r1.SetName(new String("population"));
    r1.SetAmount(200000);
    
    if (!(r1.GetName().equals("population")))
      return false;
    if(!(r1.GetAmount() == 200000))
      return false;
      
    Resource r2 = new Resource(r1.GetName(), r1.GetAmount());
    
    if (!(r2.GetName().equals("population")))
      return false;
    if(!(r2.GetAmount() == 200000))
      return false;
    
    System.out.println("/---- End Resource Test ----/");
    return true;
  }
  
  //! Test the Alloy Transform class
  public static boolean TestAlloyTransform()
  {
    System.out.println("/--- Begin Alloy Transform Test ---/");
    Resource p = new Resource(new String("population"), 1);
    Resource e = new Resource(new String("metallic elements"), 2);
    Resource ma = new Resource(new String("metallic alloys"), 0);
    Resource maw = new Resource(new String("metallic alloys waste"), 0);
    
    AlloyTransform transform = new AlloyTransform();
    
    boolean test = transform.Transform(p, e, ma, maw);
    
    if (!(test))
      return false;
      
    if (ma.GetAmount() != 1)
      return false;
      
    if (maw.GetAmount() != 1)
      return false;
      
    test = transform.Transform(p, e, ma, maw);
    
    if (test)
      return false;
      
    System.out.println("/---- End Alloy Transform Test ----/");
    
    return true;
  }
  
  //! Test the Country class
  public static boolean TestCountry()
  {
    System.out.println("/--- Begin Country Test ---/");
    Country narnia = new Country();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String name = new String("Picon");
    String output = new String(name).concat(new String("_output.txt"));
    
    narnia.schedule(
      name,
      file,
      test,
      output,
      1,
      1,
      1
    );
    
    //narnia.printDetails();
    //boolean check = narnia.CalculateStatus();
    //narnia.printStatus();
    
    System.out.println("/---- End Country Test ----/");
    
    return true;
  }
  
  //! Test the Trade Entry class
  public static boolean TestEntry()
  {
    System.out.println("/--- Begin Entry Test ---/");
    Country narnia = new Country();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String name = new String("Picon");
    String output = new String(name).concat(new String("_output.txt"));
    
    narnia.schedule(
      name,
      file,
      test,
      output,
      1,
      1,
      1
    );
    
    Resource need = new Resource(new String("substance1"), 1000);
    Resource offer = new Resource(new String("substance2"), 2000);
    Entry entry = new Entry(narnia, offer, need, false);
    
    if (!(entry.GetName().equals("Picon")))
      return false;
      
    if (!(entry.GetCountry().get().equals(narnia)))
      return false;
      
    if (!(entry.GetNeed().GetName().equals("substance1")))
      return false;
      
    if (!(entry.GetOffer().GetName().equals("substance2")))
      return false;
      
    if (entry.GetNeed().GetAmount() != 1000)
      return false;
      
    if (entry.GetOffer().GetAmount() != 2000)
      return false;
      
    if (entry.GetSuccess() != false)
      return false;
      
    if (entry.GetNoMatch() != false)
      return false;
      
    boolean setResult = entry.SetSuccessToTrue();
    
    if (setResult != true)
      return false;
      
    if(entry.GetSuccess() != true)
      return false;
      
    if(entry.IsSurplus() != false)
      return false;
    
    System.out.println("/---- End Entry Test ----/");
    return true;
  }
  
  //! Test the Country class ability to calculate a Status
  public static boolean TestStatus()
  {
    System.out.println("/--- Begin Status Test ---/");
    Country narnia = new Country();
    
    Status stat = new Status();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String name = new String("Picon");
    String output = new String(name).concat(new String("_output.txt"));
    
    narnia.schedule(
      name,
      file,
      test,
      output,
      1,
      1,
      1
    );
    
    //narnia.printDetails();
    boolean check = narnia.CalculateStatus(stat);
    stat.Print();
    
    System.out.println("/---- End Status Test ----/");
    
    return true;
  }
  
  //! Test the Trade Manager given a perfect match
  public static boolean TestManager1()
  {
    System.out.println("/--- Begin Manager Test 1 ---/");
    Country Picon = new Country();
    Country Caprica = new Country();
    Country Gemenon = new Country();
    Country Aquaria = new Country();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String picon_name = new String("Picon");
    String caprica_name = new String("Caprica");
    String gemenon_name = new String("Gemenon");
    String aquaria_name = new String("Aquaria");
    String p_output = new String(picon_name).concat(new String("_output.txt"));
    String c_output = new String(caprica_name).concat(new String("_output.txt"));
    String g_output = new String(gemenon_name).concat(new String("_output.txt"));
    String a_output = new String(aquaria_name).concat(new String("_output.txt"));
    
    Picon.schedule(picon_name, file, test, p_output, 1, 1, 1);
    Caprica.schedule(caprica_name, file, test, c_output, 1, 1, 1);
    Gemenon.schedule(gemenon_name, file, test, g_output, 1, 1, 1);
    Aquaria.schedule(aquaria_name, file, test, a_output, 1, 1, 1);
    
    ArrayBlockingQueue<Entry> queue = new ArrayBlockingQueue<>(10);
    Manager manager = new Manager();
    Manager.SetQueue(queue);
    
    Resource s1 = new Resource(new String("substance1"), 1000);
    Resource s2 = new Resource(new String("substance2"), 2000);
    
    Resource s3 = new Resource(new String("substance1"), 900);
    Resource s4 = new Resource(new String("substance2"), 3000);
    
    Resource s5 = new Resource(new String("substance1"), 2000);
    Resource s6 = new Resource(new String("substance2"), 400);
    
    Entry eTest = new Entry(Picon, s2, s1, false);
    Entry e2    = new Entry(Caprica, s1, s2, false);
    Entry e3    = new Entry(Gemenon, s3, s4, false);
    Entry e4    = new Entry(Aquaria, s5, s6, true);
    
    Thread newThread = new Thread(manager);
    
    boolean checker = false;
    queue.add(eTest);
    queue.add(e2);
    queue.add(e3);
    queue.add(e4);
    
    newThread.start();
    
    while (eTest.GetNoMatch() == false && eTest.GetSuccess() == false)
    {
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException ie) {
        System.out.println("Thread interrupted");
      } // end catch
    } // end while
    
    try {
      manager.Shutdown();
      newThread.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
    
    if (eTest.GetNoMatch() != false)
      return false;
      
    if (eTest.GetSuccess() != true)
      return false;
      
    if (e2.GetNoMatch() != false)
      return false;
      
    if (e2.GetSuccess() != true)
      return false;
      
    if (e3.GetSuccess() != false && e3.GetNoMatch() != false)
      return false;
      
    if (e4.GetSuccess() != false && e4.GetNoMatch() != false)
      return false;
      
    if (queue.contains(eTest))
      return false;
      
    if (queue.contains(e2))
      return false;
      
    if (!(queue.contains(e3)))
      return false;
      
    if (queue.contains(e4) != true)
      return false;
      
    if (eTest.GetTaken() != 2000)
      return false;
      
    if (eTest.GetGiven() != 1000)
      return false;
      
    if (e2.GetTaken() != 1000)
      return false;
      
    if (e2.GetGiven() != 2000)
      return false;
      
    //System.out.println("taken is " + eTest.GetTaken());
    //System.out.println("given is " + eTest.GetGiven());
    
    System.out.println("/---- End Manager Test 1 ----/");
    
    return true;
  }
  
    //! Test the Trade Manager given an offer and need that are both less
  public static boolean TestManager2()
  {
    System.out.println("/--- Begin Manager Test 2 ---/");
    Country Picon = new Country();
    Country Gemenon = new Country();
    Country Aquaria = new Country();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String picon_name = new String("Picon");
    String gemenon_name = new String("Gemenon");
    String aquaria_name = new String("Aquaria");
    String p_output = new String(picon_name).concat(new String("_output.txt"));
    String g_output = new String(gemenon_name).concat(new String("_output.txt"));
    String a_output = new String(aquaria_name).concat(new String("_output.txt"));
    
    Picon.schedule(picon_name, file, test, p_output, 1, 1, 1);
    Gemenon.schedule(gemenon_name, file, test, g_output, 1, 1, 1);
    Aquaria.schedule(aquaria_name, file, test, a_output, 1, 1, 1);
    
    ArrayBlockingQueue<Entry> queue = new ArrayBlockingQueue<>(10);
    Manager manager = new Manager();
    Manager.SetQueue(queue);
    
    Resource s1 = new Resource(new String("substance1"), 1000);
    Resource s2 = new Resource(new String("substance2"), 2000);
    
    Resource s3 = new Resource(new String("substance1"), 500);
    Resource s4 = new Resource(new String("substance2"), 700);
    
    Resource s5 = new Resource(new String("substance1"), 900);
    Resource s6 = new Resource(new String("substance2"), 950);
    
    Entry eTest = new Entry(Picon, s2, s1, false);
    Entry e2    = new Entry(Gemenon, s3, s4, false);
    
    // This is our success entry
    Entry e3    = new Entry(Aquaria, s5, s6, true);
    
    Thread newThread = new Thread(manager);
    
    boolean checker = false;
    queue.add(eTest);
    queue.add(e2);
    queue.add(e3);
    
    newThread.start();
    
    while (eTest.GetNoMatch() == false && eTest.GetSuccess() == false)
    {
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException ie) {
        System.out.println("Thread interrupted");
      } // end catch
    } // end while
    
    try {
      manager.Shutdown();
      newThread.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
    
    if (eTest.GetNoMatch() != false)
      return false;
      
    if (eTest.GetSuccess() != true)
      return false;
      
    if (e3.GetNoMatch() != false)
      return false;
      
    if (e3.GetSuccess() != true)
      return false;
      
    if (e2.GetSuccess() != false)
      return false;
      
    if (queue.contains(eTest))
      return false;
      
    if (queue.contains(e3))
      return false;
      
    // This should pass because it is not a surplus. Therefore, the manager
    // should try to find a match and fail.  
    if (!(queue.contains(e2)))
      return false;
      
    if (eTest.GetTaken() != 950)
      return false;
      
    if (eTest.GetGiven() != 900)
      return false;
      
    if (e3.GetTaken() != 900)
      return false;
      
    if (e3.GetGiven() != 950)
      return false;
    
    System.out.println("/---- End Manager Test 2 ----/");
    
    return true;
  }
  
  //! Test the trade Manager given trade country need that's higher than what
  //! we can give
    public static boolean TestManager3()
  {
    System.out.println("/--- Begin Manager Test 3 ---/");
    Country Picon = new Country();
    Country Gemenon = new Country();
    Country Aquaria = new Country();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String picon_name = new String("Picon");
    String gemenon_name = new String("Gemenon");
    String aquaria_name = new String("Aquaria");
    String p_output = new String(picon_name).concat(new String("_output.txt"));
    String g_output = new String(gemenon_name).concat(new String("_output.txt"));
    String a_output = new String(aquaria_name).concat(new String("_output.txt"));
    
    Picon.schedule(picon_name, file, test, p_output, 1, 1, 1);
    Gemenon.schedule(gemenon_name, file, test, g_output, 1, 1, 1);
    Aquaria.schedule(aquaria_name, file, test, a_output, 1, 1, 1);
    
    ArrayBlockingQueue<Entry> queue = new ArrayBlockingQueue<>(10);
    Manager manager = new Manager();
    Manager.SetQueue(queue);
    
    Resource s1 = new Resource(new String("substance1"), 1000);
    Resource s2 = new Resource(new String("substance2"), 2000);
    
    Resource s3 = new Resource(new String("substance1"), 900);
    Resource s4 = new Resource(new String("substance2"), 3000);
    
    Resource s5 = new Resource(new String("substance1"), 500);
    Resource s6 = new Resource(new String("substance2"), 400);
    
    Entry eTest = new Entry(Picon, s2, s1, false);
    
    // This is our success entry
    Entry e2    = new Entry(Gemenon, s3, s4, false);
    
    Entry e3    = new Entry(Aquaria, s5, s6, true);
    
    Thread newThread = new Thread(manager);
    
    boolean checker = false;
    queue.add(eTest);
    queue.add(e2);
    queue.add(e3);
    
    newThread.start();
    
    while (eTest.GetNoMatch() == false && eTest.GetSuccess() == false)
    {
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException ie) {
        System.out.println("Thread interrupted");
      } // end catch
    } // end while
    
    try {
      manager.Shutdown();
      newThread.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
    
    if (eTest.GetNoMatch() != false)
      return false;
      
    if (eTest.GetSuccess() != true)
      return false;
      
    if (e2.GetNoMatch() != false)
      return false;
      
    if (e2.GetSuccess() != true)
      return false;
      
    if (e3.GetSuccess() != false && e3.GetNoMatch() != false)
      return false;
      
    if (queue.contains(eTest))
      return false;
      
    if (queue.contains(e2))
      return false;
      
    if (queue.contains(e3) != true)
      return false;
      
    if (eTest.GetTaken() != 2000)
      return false;
      
    if (eTest.GetGiven() != 600)
      return false;
      
    if (e2.GetTaken() != 600)
      return false;
      
    if (e2.GetGiven() != 2000)
      return false;
    
    System.out.println("/---- End Manager Test 3 ----/");
    
    return true;
  }

  //! Program Execution point
  public static void main(String[] args)
  {
    if (!(TestResource()))
      System.out.println("Resource Test Failed");
      
    if (!(TestAlloyTransform()))
      System.out.println("Alloy Transform Test Failed");
      
    if (!(TestCountry()))
      System.out.println("Country Test Failed");
      
    if (!(TestStatus()))
      System.out.println("Status Test Failed");
      
    if (!(TestEntry()))
      System.out.println("Entry Test Failed");
      
    if (!(TestManager1()))
      System.out.println("Manager Test 1 Failed");
      
    if (!(TestManager2()))
      System.out.println("Manager Test 2 Failed");
      
    if (!(TestManager3()))
      System.out.println("Manager Test 3 Failed");
  }
}
