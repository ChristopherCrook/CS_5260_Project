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
  private static String log_name_m;
  private static AtomicInteger destroyed_m;
  
  private static String first_line_m = new String("Aliens population: ");
  private static String second_line_m = new String("Human population: ");
  private static String third_line_m = new String("Number of Human countries: ");
  private static String fourth_line_m = new String("Human countries left: ");
  private static String currentCountry_m = new String("Current country under attack: ");
  private static String currentPop_m = new String("Current country population: ");
  private static String fifth_line_m = new String("Last attack: ");
  private static String resAttack_m = new String("Resource attack");
  private static String popAttack_m = new String("Population attack");
  private static String alienDamage_m = new String("Alien Damage: ");
  private static String humanDamage_m = new String("Human Damage: ");
  private static String roll_m = new String("Roll: ");
  private static String modifier_m = new String("Modifier: ");
  private static String none_m = new String("None");
  private static String remaining_m = new String("Human countries destroyed: ");
  private static String comma = new String(", ");
  private static String percent_m = new String("%");
  private static String aWins_m = new String("Aliens Win!");
  private static String hWins_m = new String("Humans Win!");
  
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
    destroyed_m     = new AtomicInteger(countries.size());
    die_m           = new TwentySidedDie();
    
    log_name_m = new String(log_name);
    
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
      
      //! Check and see if the aliens were beaten
      if (aliens_m.get() < 1)
        break;
      
      //! If populate is 0, then get next Country
      if (GetPopulationSize(next) < 1)
      {
        int previous = destroyed_m.getAndDecrement();
        next = GetNextCountry();
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
    
    long size = 0;
    if (countries_m.isEmpty() == false)
    {
      for (Country c : countries_m)
        size = size + GetPopulationSize(c);
    }
    BufferedWriter writer;
    
    try {
      writer = new BufferedWriter(new FileWriter(log_name_m));
      writer.write(first_line_m);
      writer.write(String.valueOf(aliens_m.get()));
      writer.write(new String(","));
      writer.write(second_line_m);
      writer.write(String.valueOf(size));
      writer.write(new String(","));
      writer.write(third_line_m);
      writer.write(String.valueOf(countries_m.size()));
      writer.write(new String(","));
      writer.write(fourth_line_m);
      writer.write(destroyed_m.toString());
      writer.write(new String(","));
      if (aliens_m.get() > 0)
        writer.write(aWins_m);
      else
        writer.write(hWins_m);
      writer.flush();
      writer.newLine();
      
      writer.close();
    }
    catch (IOException e)
    {
      System.out.println("File error!");
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
    
    // Check what attack took place and wha' happened?
    if (type == 0) // This is a resource attack
    {
      // No damage to aliens since this was a resource attack
      // Check the Defender damage
      if (Defender.intValue() > 0)
      {
        country.Reduce_Supplies(defender_percentage);
        country.Reduce_Urban((float)0.01);
      } // end if
    
      long size = 0;
      if (countries_m.isEmpty() == false)
      {
        for (Country c : countries_m)
          size = size + GetPopulationSize(c);
      }
      
      int left = 0;
      
      for (Country c : countries_m)
      {
        if (GetPopulationSize(c) > 0)
          left++;
      }
      
      BufferedWriter writer;
      // log it
      try {
        writer = new BufferedWriter(new FileWriter(log_name_m));
        writer.write(first_line_m);
        writer.write(String.valueOf(aliens_m.get()));
        writer.write(new String(","));
        writer.write(second_line_m);
        writer.write(String.valueOf(size));
        writer.write(new String(","));
        writer.write(third_line_m);
        writer.write(String.valueOf(countries_m.size()));
        writer.write(new String(","));
        writer.write(fourth_line_m.concat(String.valueOf(countries_m.size() - left)));
        writer.write(new String(","));
        writer.write(currentCountry_m.concat(country.GetName()));
        writer.write(new String(","));
        writer.write(currentPop_m.concat(String.valueOf(GetPopulationSize(country))));
        writer.write(new String(","));
        writer.write(fifth_line_m.concat(resAttack_m));
        writer.write(new String(","));
        writer.write(humanDamage_m);
        writer.write(String.valueOf(Defender.intValue()).concat(percent_m));
        writer.write(new String(","));
        writer.write(roll_m.concat(String.valueOf(roll)));
        writer.write(new String(","));
        writer.write(modifier_m.concat(String.valueOf(mod)));
        writer.write(new String(","));
        writer.write(remaining_m.concat(String.valueOf(left)));
        
        writer.flush();
        writer.newLine();
        writer.close();
      }
      catch (IOException e)
      {
        System.out.println("File error!");
      }
    } // end if resource
    else // This is a population attack
    {
      // First check the Defender damage
      if (Defender.intValue() > 0)
      {
        country.Reduce_Urban(defender_percentage);
      }
      
      // Now apply alien damage
      if (Attacker.intValue() > 0)
      {        
        aliens_m.set(aliens_m.get() - (long)(aliens_m.get() * attacker_percentage));
      } // end if
      
      long size = 0;
      if (countries_m.isEmpty() == false)
      {
        for (Country c : countries_m)
          size = size + GetPopulationSize(c);
      }
      
      int left = 0;
      
      for (Country c : countries_m)
      {
        if (GetPopulationSize(c) > 0)
          left++;
      }
      
      BufferedWriter writer;
      // log it
      try {
        writer = new BufferedWriter(new FileWriter(log_name_m));
        writer.write(first_line_m);
        writer.write(String.valueOf(aliens_m.get()));
        writer.write(new String(","));
        writer.write(second_line_m);
        writer.write(String.valueOf(size));
        writer.write(new String(","));
        writer.write(third_line_m);
        writer.write(String.valueOf(countries_m.size()));
        writer.write(new String(","));
        writer.write(fourth_line_m.concat(String.valueOf(countries_m.size() - left)));
        writer.write(new String(","));
        writer.write(currentCountry_m.concat(country.GetName()));
        writer.write(new String(","));
        writer.write(currentPop_m.concat(String.valueOf(GetPopulationSize(country))));
        writer.write(new String(","));
        writer.write(fifth_line_m.concat(popAttack_m));
        writer.write(new String(","));
        writer.write(humanDamage_m);
        writer.write(String.valueOf(Defender.intValue()).concat(percent_m));
        writer.write(alienDamage_m);
        writer.write(new String(" -- "));
        writer.write(String.valueOf(Attacker.intValue()).concat(percent_m));
        writer.write(new String(","));
        writer.write(roll_m.concat(String.valueOf(roll)));
        writer.write(new String(","));
        writer.write(modifier_m.concat(String.valueOf(mod)));
        writer.write(new String(","));
        writer.write(remaining_m.concat(String.valueOf(left)));
        writer.flush();
        writer.newLine();
        
        writer.close();
      }
      catch (IOException e)
      {
        System.out.println("File error!");
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
