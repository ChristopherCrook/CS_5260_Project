import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*; 
import java.io.*;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;

import Dice.TwentySidedDie;

//! Class to simulate an alien invasion
//!
public class AlienInvasion {

  //! Private class variables
  private static LinkedList<Country> countries_m;
  private static ArrayBlockingQueue<Entry> queue_m;
  private static Manager trade_manager_m;
  private static BufferedWriter writer_m;
  
  private static Random random_m;
  private static AtomicLong aliens_m;
  
  private static TwentySidedDie die_m;
  
  //! Private constants
  static final int SCHEDULES = 2;
  static final int FRONTIER = 10;
  static final int BOUND = 10;

  //! Initialize method to set up countries used for invasion as well
  //! as private variables
  public static void Initialize(
    String input_file,
    String output_suffix,
    String log_name,
    ArrayList<String> countries
  )
  {
    // Initialize our variables that are considered constant
    random_m        = new Random();
    aliens_m        = new AtomicLong(0);
    queue_m         = new ArrayBlockingQueue<>(100);
    trade_manager_m = new Manager();
    countries_m     = new LinkedList<>();
    die_m           = new TwentySidedDie();
    
    // Open the log
    try {
      writer_m = new BufferedWriter(new FileWriter(log_name));
    }
    catch (IOException e)
    {
      System.out.println("File error!");
    }
    
    // Create our countries
    for (String s : countries)
    {
      // Build a country given a name
      Country c = new Country();
      c.SetQueue(queue_m);
      c.schedule(
        s,
        input_file,
        new String("not important"),
        new String(s).concat(output_suffix),
        SCHEDULES,
        BOUND,
        FRONTIER
      );
      
      // Add to the list
      countries_m.add(c);
    }
    
    // Set the size of the invading force
    aliens_m.set(GetInvasionSize());
    
    // Set the queue for the manager
    trade_manager_m.SetQueue(queue_m);
  }
  
  public static void BeginInvasion()
  {
    // Notify beginning
    System.out.println("Invasion beginning with " + aliens_m.get() + " aliens");
    
    // Log the Invasion specifics
    try {
      writer_m.write(new String("Alien invasion beginning with "));
      writer_m.flush();
      writer_m.write(new String(String.valueOf(aliens_m.get())));
      writer_m.flush();
      writer_m.write(new String(" aliens and "));
      writer_m.flush();
      writer_m.write(new String(String.valueOf(countries_m.size())));
      writer_m.flush();
      writer_m.write(new String(" countries."));
      writer_m.flush();
      writer_m.newLine();
      writer_m.flush();
    }
    catch (IOException e)
    {
      System.out.println("File error!");
    }
    
    // Create Manager thread
    Thread mt = new Thread(trade_manager_m);
    
    // Start the thread
    mt.start();
    
    // Get the first country using a random seed
    Country next = countries_m.get(random_m.nextInt(countries_m.size()));
    
    // Set a print count
    int count = 0;
    
    // Start a loop
    while (next != null)
    {
      //! Increment count
      count++;
      
      //! Sanitize the queue_m
      queue_m.clear();
      
      // Run the Sequence
      Run_Sequence(next, count);
      
      //XXX
      System.out.println("Aliens now have " + aliens_m);
      
      //! Check and see if the aliens were beaten
      if (aliens_m.get() < 1)
        break;
      
      //! If populate is 0, then get next Country
      if (GetPopulationSize(next) < 1)
      {
        // log it
        try {
          writer_m.write(next.GetName().concat(new String(" has been destroyed. ")));
          writer_m.flush();
          writer_m.newLine();
        }
        catch (IOException e)
        {
          System.out.println("File error!");
        }
        finally
        {
          next = GetNextCountry();
        } // end finally
      } // end if
    } // end while
    
    // shutdown the manager and the associated thread
    trade_manager_m.Shutdown();
    
    try {
      mt.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    }
      
    // Let the user know we're done
    System.out.println("Invasion is over.");
    if (aliens_m.get() < 1)
      System.out.println("Aliens have been defeated.");
    else
      System.out.println("Aliens are victorious.");
  }
  
  //! Run Sequence of Optimization and Attack given a certain country
  public static void Run_Sequence(Country current, int count)
  {
    //! Create Threads
    LinkedList<Thread> threads = new LinkedList<>();
      
    //! Add Countries to threads
    for (Country c : countries_m)
    {
      Thread t = new Thread(c);
      threads.add(t);
    }
      
    //! Start threads
    for (Thread tr : threads)
      tr.start();
      
    //! Signal the Schedule start
    Country.BEGIN = true;
      
    //! Alert the user
    System.out.println("Starting optimization");
      
    //! Wait for threads to complete using a timer
    int timer = 0;
    while (queue_m.size() > 0)
    {
      try {
        if (timer > 49) // Wait up to 5000 milliseconds/ 5 seconds
          break;
        Thread.sleep(100);
        
        timer++;
      }
      catch (InterruptedException ie)
      {
        System.out.println("Thread interrupted");
      }
    } // end catch
      
    //! Shutdown threads
    try {
      for (Thread tr : threads)
        tr.join();
    }
    catch (InterruptedException ie) {
      System.out.println("Thread interrupted");
    } // end catch
      
    //! Stop the Countries from starting again
    Country.BEGIN = false;
      
    //! Alert the user the attack is starting
    System.out.println("Performing attack number " + count + " on " + current.GetName());
   
    //! Roll the Dice
    int roll = die_m.Roll();
      
    //! Calculate Modifier
    int mod = CalculateModifier(current);

    //! Log the attack
    try {
      writer_m.newLine();
      writer_m.write(new String("Attacking "));
      writer_m.flush();
      writer_m.write(new String(current.GetName()));
      writer_m.flush();
      writer_m.write(new String(" with a population size of "));
      writer_m.flush();
      writer_m.write(new String(String.valueOf(GetPopulationSize(current))));
      writer_m.flush();
      writer_m.write(new String("."));
      writer_m.flush();
      writer_m.newLine();
      writer_m.write(new String("Roll: "));
      writer_m.flush();
      writer_m.write(String.valueOf(roll));
      writer_m.flush();
      writer_m.newLine();
      writer_m.write(new String("Mod: "));
      writer_m.flush();
      writer_m.write(String.valueOf(mod));
      writer_m.flush();
      writer_m.newLine();
      writer_m.flush();
    }
    catch (IOException e)
    {
      System.out.println("File error!");
    }
      
    //! Execute the attack
    ExecuteAttack(current, roll, mod);   
  }
  
  //! Get Country population size
  public static long GetPopulationSize(Country country)
  {
    for (Resource r : country.GetResources())
    {
      if (r.GetName().equals(new String("Population")))
      {
        return r.GetAmount();
      } // end if
    } // end for
    
    return -1;
  }
  
  //! Get next Country to attack. If no countries can be attacked, then 
  //! return null
  public static Country GetNextCountry()
  {
    if (countries_m.isEmpty())
      return null;
  
    // Loop through the countries
    for (Country c : countries_m)
    {
      // Test the population
      if (GetPopulationSize(c) > 0)
        return c;
    } // end for
    
    // if we get here, nothing was found. Everyone is assumed dead
    return null;
  }
  
  //! Method to calculate the attack modifier between -26 and 26
  //! Ways that modifier is calculated:
  //!
  //! In terms of population, how fair is the fight?
  //! > add or remove 6 points
  //! Does the country have resources to defend itself?
  //! > add or remove 4 points
  //! Is the country doing bad in terms of housing?
  //! > add or remove 12 points
  //! Is the country advanced?
  //! > add or remove 5 points
  public static int CalculateModifier(Country country)
  {
    // Get the status of the country
    Status c_stat = new Status();
    boolean check = country.CalculateStatus(c_stat);
    
    // Declare variables we need
    Resource c_pop = new Resource();
    
    // Do a check on progress
    if (!(check))
    {
      System.out.println("Error getting status from " +  country.GetName());
      return 0;
    }
    
    // Declare our modifier
    int mod = 0;
    
    // Set our population variable
    for (Resource r : country.GetResources())
    {
      if (r.GetName().equals(new String("Population")))
        c_pop.SetName(r.GetName());
        c_pop.SetAmount(r.GetAmount());
    }
    
    // Adjust for population
    if (c_pop.GetAmount() > aliens_m.get())
      mod = mod - 6;
    else if (c_pop.GetAmount() < aliens_m.get())
      mod = mod + 6;
    else
      // Do nothing
      
    // Adjust for resources
    if (c_stat.Get_Status().get(1) || 
        c_stat.Get_Status().get(2) || 
        c_stat.Get_Status().get(3)
    )
      mod = mod + 4;
    else
      mod = mod - 4;
    
    // Adjust for housing
    if (c_stat.Get_Status().get(6))
      mod = mod + 12;
    else
      mod = mod - 12;
      
    // Adjust for electronics
    if (c_stat.Get_Status().get(5))
      mod = mod + 5;
    else
      mod = mod - 5;
      
    // Check our result
    if (mod < -27 || mod > 27)
    {
      System.out.println("Error generating modifier. Number is " + mod);
      return 0;
    }
      
    return mod;
  }
  
  //! Method to get the size of invading force
  public static long GetInvasionSize()
  {
    // Declare our variable
    long size = 0;
    
    // Iterate through the countries and get the overall world population
    // as a starting point
    for (Country c : countries_m)
    {
      for (Resource r : c.GetResources())
      {
        if (r.GetName().equals(new String("Population")))
        {
          size += r.GetAmount();
        } // end if
      } // end for
    } // end for
    
    // Print size
    System.out.println("Size of the target world has " + size + " people");
    
    // Get a random long between half the average country size and 1.5 times its size
    long[] longs = random_m.longs(
      1,
      (long)(size / 2),
      (long)(size * 1.5)
    ).toArray();
    
    return longs[0];
  }
  
  //! Method to carry out an attack on a country given two types of attacks:
  //! Resource attack and Urban attack
  public static void ExecuteAttack(Country country, int roll, int mod)
  {
    int type = random_m.nextInt(2);
    
    Long thou = Long.valueOf(1000);
    Long tenThou = Long.valueOf(10000);
    
    AtomicInteger Attacker = new AtomicInteger(0);
    AtomicInteger Defender = new AtomicInteger(0);
    
    // Check and see if we are beyond hope
    if (GetPopulationSize(country) < 10 && thou.longValue() < aliens_m.get())
    {
      // No matter what we roll, they're going to die
      country.Reduce_Urban(1);
      return;
    }
    
    // Check and see if the country is at their last stand
    if (GetPopulationSize(country) < 100 && thou.longValue() < aliens_m.get())
    {
      if (roll > 2)
      {
        country.Reduce_Urban(1);
        return;
      }
    }
    
    if (GetPopulationSize(country) <= 1000 && tenThou.longValue() < aliens_m.get())
    {
      if (roll > 15)
      {
        Attacker.set(0);
        Defender.set(35);
      }
      else if (roll > 10)
      {
        Attacker.set(0);
        Defender.set(25);
      }
      else if (roll > 5)
      {
        Attacker.set(1);
        Defender.set(10);
      }
      else if (roll > 1)
      {
        Attacker.set(2);
        Defender.set(5);
      }
      else
      {
        Attacker.set(2);
        Defender.set(0);
      }
    }
    else
    {
      boolean check = false;
      
      if ((long)(GetPopulationSize(country) * 10) < aliens_m.get())
      {
        check = true;
      }
      
      die_m.GetAttackScale(roll, mod, Attacker, Defender, check);
    }
    
    
    float attacker_percentage = (float)(.01 * Attacker.get());
    float defender_percentage = (float)(.01 * Defender.get());
    
    if (type == 0) // This is a resource attack
    {
      // No damage to aliens since this was a resource attack
      // Check the Defender damage
      if (Defender.intValue() > 0)
      {
        country.Reduce_Supplies(defender_percentage);
        country.Reduce_Urban((float)0.01);
        
        // log it
        try {
          writer_m.write(country.GetName().concat(new String(" lost ")));
          writer_m.flush();
          writer_m.write(String.valueOf(Defender.get()).concat(new String("% of resoures")));
          writer_m.flush();
          writer_m.newLine();
        }
        catch (IOException e)
        {
          System.out.println("File error!");
        }
      } // end if
      else
      {
        try {
          writer_m.write(new String("No damage to resources"));
          writer_m.flush();
          writer_m.newLine();
        }
        catch (IOException e)
        {
          System.out.println("File error!");
        }
      } // end if
    } // end if resource
    else // This is a population attack
    {
      // First check the Defender damage
      if (Defender.intValue() > 0)
      {
        country.Reduce_Urban(defender_percentage);
        
        // log it
        try {
          writer_m.write(country.GetName().concat(new String(" lost ")));
          writer_m.flush();
          writer_m.write(String.valueOf(Defender.get())
            .concat(new String("% of people and houses")));
          writer_m.flush();
          writer_m.newLine();
        }
        catch (IOException e)
        {
          System.out.println("File error!");
        }
      }
      
      // Now apply alien damage
      if (Attacker.intValue() > 0)
      {
        // log it
        try {
          if (attacker_percentage > 0)
          {
            writer_m.write(new String("Aliens were reduced by ")
              .concat(String.valueOf((long)(aliens_m.get() * attacker_percentage))));
          }
          else
            writer_m.write(new String("Aliens were not reduced"));
            
          writer_m.flush();
          
          writer_m.newLine();
        
          aliens_m.set(aliens_m.get() - (long)(aliens_m.get() * attacker_percentage));
        } // end try
        catch (IOException e)
        {
          System.out.println("File error!");
        }
      } // end if
      
      // Check and see if there was no damage at all
      if (Attacker.intValue() == 0 && Defender.intValue() == 0)
      {
        try {
          writer_m.write(new String("No damage to either side occurred"));
          writer_m.flush();
          writer_m.newLine();
        }
        catch (IOException e)
        {
          System.out.println("File error!");
        }
      }
    } // end else
  }

  //! Program Execution point
  public static void main(String[] args)
  {
    ArrayList<String> names = new ArrayList<>();
    
    String output = new String("_AlienInvasion_output.txt");
    String file = new String("aliens.csv");
    String log = new String("alien_log.txt");
    
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
    
    Initialize(file, output, log, names);
    
    BeginInvasion();
  }

} // end class
