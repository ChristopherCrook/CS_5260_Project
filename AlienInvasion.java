import java.util.*;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;

//! Class to simulate an alien invasion
//!
public class AlienInvasion {

  private LinkedList<Country> countries_m;
  private ArrayBlockingQueue<Entry> queue_m;
  private Manager trade_manager_m;
  
  private Random random_m;
  private AtomicLong aliens_m;
  
  static volatile int SCHEDULES = 25;
  static volatile int FRONTIER = 10;
  static volatile int BOUND = 10;

  //! Initialize method to set up countries used for invasion as well
  //! as private variables
  public void Initialize(
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
  }
  
  //! Method to get the results of a 20-sided die roll
  public int Roll_20()
  {
    return random_m.nextInt(20) + 1;
  }
  
  //! Method to calculate the attack modifier
  public int CalculateModifier(Country country)
  {
  
  }
  
  //! Method to get the size of invading force
  public long GetInvastionSize()
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
    System.out.println("Size of world is " + size);
    
    // Get a random long between half the world size and 1.5 times its size
    long[] longs = random_m.longs(1, size / 2, size * 1.5).toArray();
    
    return longs[0];
  }

  //! Program Execution point
  public static void main(String[] args)
  {
  }

} // end class
