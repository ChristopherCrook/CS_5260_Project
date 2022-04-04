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

  private static LinkedList<Country> countries_m;
  private static ArrayBlockingQueue<Entry> queue_m;
  private static Manager trade_manager_m;
  
  private static Random random_m;
  private static AtomicLong aliens_m;
  
  private static TwentySidedDie die_m;
  
  static volatile int SCHEDULES = 25;
  static volatile int FRONTIER = 10;
  static volatile int BOUND = 10;

  //! Initialize method to set up countries used for invasion as well
  //! as private variables
  public static void Initialize(
    String input_file,
    String output_suffix,
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
  }
  
  public static void BeginInvasion()
  {
    System.out.println("Invasion beginning with " + aliens_m.get() + " aliens");
    
    
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
    
    // Get a random long between half the world size and 1.5 times its size
    long[] longs = random_m.longs(1, (long)(size / 2), (long)(size * 1.5)).toArray();
    
    return longs[0];
  }
  
  //! Method to carry out an attack on a country given two types of attacks:
  //! Resource attack and Urban attack
  public static void ExecuteAttack(Country country, int roll, int mod)
  {
    int type = random_m.nextInt(2);
    
    AtomicInteger Attacker = new AtomicInteger(0);
    AtomicInteger Defender = new AtomicInteger(0);
    
    die_m.GetAttackScale(roll, mod, Attacker, Defender);
    
    float attacker_percentage = (float)(.01 * Attacker.get());
    float defender_percentage = (float)(.01 * Defender.get());
    
    if (type == 0) // This is a resource attack
    {
      // First check the Defender damage
      if (Defender.intValue() > 0)
      {
        country.Reduce_Supplies(defender_percentage);
      }
      
      if (Attacker.intValue() > 0)
      {
        aliens_m.set(aliens_m.get() - (long)(aliens_m.get() * attacker_percentage));
      }
    } // end if resource
    else // This is a population attack
    {
      // First check the Defender damage
      if (Defender.intValue() > 0)
      {
        country.Reduce_Urban(defender_percentage);
      }
      
      if (Attacker.intValue() > 0)
      {
        aliens_m.set(aliens_m.get() - (long)(aliens_m.get() * attacker_percentage));
      }
    } // end else
  }

  //! Program Execution point
  public static void main(String[] args)
  {
    ArrayList<String> names = new ArrayList<>();
    
    String output = new String("_AlienInvasion_output.txt");
    String file = new String("test4.csv");
    
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
    
    Initialize(file, output, names);
    
    BeginInvasion();
  }

} // end class
