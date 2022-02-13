package Hermes;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;

import java.util.concurrent.atomic.*;
import java.util.*; 
import java.io.*; 

//! Class declaration for a Country
public class Country implements Runnable, Scheduler {

  //! These are constant values used in populating the names of the 
  //! various atomic references for Resources
  static final String POPULATION = new String("population");
  static final String METALLICELEMS = new String("metallic elems");
  static final String TIMBER = new String("timber");
  static final String METALLICALLOY = new String("metallic alloys");
  static final String METALLICWASTE = new String("metallic alloy waste");
  static final String ELECTRONICS = new String("electronics");
  static final String HOUSING = new String("housing");
  static final String HOUSINGWASTE = new String("housing waste");

  //! Variable for the name of the country
  private String name_m = new String();
  
  //! Variable to hold the status of the country
  private AtomicReference<Status> status_m;

  //! Resource variables implemented as Atomic Reference
  private AtomicReference<Resource> population_m;
  private AtomicReference<Resource> metallicElems_m;
  private AtomicReference<Resource> timber_m;
  private AtomicReference<Resource> metallicAlloys_m;
  private AtomicReference<Resource> metallicAlloysWaste_m;
  private AtomicReference<Resource> electronics_m;
  private AtomicReference<Resource> housing_m;
  private AtomicReference<Resource> housingWaste_m;
  
  //! private references for transform classes
  private HousingTransform     h_trans_m;
  private AlloyTransform       a_trans_m;
  private ElectronicsTransform e_trans_m;
  
  //! Variable for whether the country class instance
  //! has been confgured
  private volatile boolean configured_m;
  
  //! Variables that are set by the user via the schedule() method
  private int schedules_m;
  private int depth_m;
  private int frontier_m;
  private String output_m;

  //! Constructor
  public Country()
  {
    // Initialize private variables
    status_m = new AtomicReference<>(new Status());
    
    population_m = new AtomicReference<>();
    metallicElems_m = new AtomicReference<>();
    timber_m = new AtomicReference<>();
    metallicAlloys_m = new AtomicReference<>();
    metallicAlloysWaste_m = new AtomicReference<>();
    electronics_m = new AtomicReference<>();
    housing_m = new AtomicReference<>();
    housingWaste_m = new AtomicReference<>();
    
    h_trans_m = new HousingTransform();
    a_trans_m = new AlloyTransform();
    e_trans_m = new ElectronicsTransform();
    
    // Set configured to false
    configured_m = false;
  }
  
  //! Inherited schedule method from Scheduler
  public void schedule(
    String name,
    String resource_filename,
    String initial_state_filename,
    String output_schedule_filename,
    int num_output_schedules,
    int depth_bound,
    int frontier_max_size 
  )
  {
    // Check dependencies
    if (name == null ||
        resource_filename == null ||
        initial_state_filename == null ||
        output_schedule_filename == null ||
        num_output_schedules < 1 ||
        depth_bound < 1 ||
        frontier_max_size < 1)
        return;
        
    name_m = name;
    
    String line = new String();
    String splitBy = new String(",");
    
    // Get the country info from the resource_filename
    try
    {
      BufferedReader resource_reader = new BufferedReader(new FileReader(resource_filename));
      boolean found = false;
    
      while ((line = resource_reader.readLine()) != null)
      {
        String[] newLine = line.split(splitBy);
      
        if (newLine[0].equals(name_m))
        {
          population_m.set(new Resource(POPULATION, Long.parseLong(newLine[1])));
          metallicElems_m.set(new Resource(METALLICELEMS, Long.parseLong(newLine[2])));
          timber_m.set(new Resource(TIMBER, Long.parseLong(newLine[3])));
          metallicAlloys_m.set(new Resource(METALLICALLOY, Long.parseLong(newLine[4])));
          metallicAlloysWaste_m.set(new Resource(METALLICWASTE, Long.parseLong(newLine[5])));
          electronics_m.set(new Resource(ELECTRONICS, Long.parseLong(newLine[6])));
          housing_m.set(new Resource(HOUSING, Long.parseLong(newLine[7])));
          housingWaste_m.set(new Resource(HOUSINGWASTE, Long.parseLong(newLine[8])));

          found = true;
          break;
        } // end if
      } // end while
    }
    catch (IOException e)
    {
      System.out.println("File error!");
    }
    
    // Placeholder to get resource weights from initial_state_filename XXX
    
    // Set the output file name
    output_m = new String(output_schedule_filename.toCharArray());
    
    // Set the scheduler variables
    schedules_m = num_output_schedules;
    depth_m = depth_bound;
    frontier_m = frontier_max_size;
    
    // Set the configured flag to true. Class is now ready.
    configured_m = true;
  }
  
  //! Overloaded run() method inherited from Runnable
  public void run()
  {
  
  }
  
  //! Method to calculate the current state, which is very complicated
  //! XXX TO-DO make this simpler somehow
  public boolean CalculateStatus()
  {
    // List of statuses
    ArrayList<Boolean> temp_status = new ArrayList<>(Arrays.asList(
      Boolean.valueOf(false),
      Boolean.valueOf(false), 
      Boolean.valueOf(false), 
      Boolean.valueOf(false), 
      Boolean.valueOf(false),
      Boolean.valueOf(false), 
      Boolean.valueOf(false),
      Boolean.valueOf(false)
     ));
    ArrayList<Long> temp_deficits = new ArrayList<>(Arrays.asList(
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0)
    ));
    ArrayList<Long> temp_surplus = new ArrayList<>(Arrays.asList(
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0)
    ));
    
    // longs for deficits
    long pop_deficit = 0;
    long metallic_deficit = 0;
    long timber_deficit = 0;
    long alloys_deficit = 0;
    long electronics_deficit = 0;
    long housing_deficit = 0;
    long alloy_waste_under = 0;
    long housing_waste_under = 0;
    
    // longs for surplus
    long alloy_waste_over = 0;
    long housing_waste_over = 0;
    long pop_surplus = 0;
    long metallic_surplus = 0;
    long timber_surplus = 0;
    long alloys_surplus = 0;
    long electronics_surplus = 0;
    long housing_surplus = 0;
   
    // Calculate need for population
    // Either we don't have enought or we don't have enough for the amount of
    // houses
    if (population_m.get().GetAmount() < 5 ||
        (population_m.get().GetAmount() < housing_m.get().GetAmount()))
        {
          if (population_m.get().GetAmount() < 5)
          {
            pop_deficit = 5 - population_m.get().GetAmount();
          } // end if
          else
          {
            pop_deficit = housing_m.get().GetAmount() - population_m.get().GetAmount();
          } // end else
          temp_status.set(0, Boolean.valueOf(true)); // We need more people
        } // end if
    else
    {
      pop_surplus = population_m.get().GetAmount() - housing_m.get().GetAmount();
    }
        
    // Calculate need for houses based on an assumption of a 4-person household
    if ((population_m.get().GetAmount() / housing_m.get().GetAmount()) > 4)
    {
      housing_deficit = population_m.get().GetAmount() / 4;
      housing_deficit = housing_deficit - housing_m.get().GetAmount();
      
      temp_status.set(6, Boolean.valueOf(true)); // We need houses
    }
    else
    {
      if (housing_m.get().GetAmount() > population_m.get().GetAmount())
        housing_surplus = housing_m.get().GetAmount() - population_m.get().GetAmount();
    }
    
    // Calculate the need for electronics based on the assumption of 2
    // electronics per person
    if ((electronics_m.get().GetAmount() / population_m.get().GetAmount()) < 2)
    {
      electronics_deficit = population_m.get().GetAmount() * 2;
      electronics_deficit = electronics_deficit - electronics_m.get().GetAmount();
      temp_status.set(5, Boolean.valueOf(true)); // need more electronics
    }
    else
    {
      if ((population_m.get().GetAmount() * 4) > electronics_m.get().GetAmount())
        electronics_surplus = electronics_m.get().GetAmount() - (population_m.get().GetAmount() * 4);
    }
    
    // Calculate the need for alloys based on need for housing and need 
    // for electronics
    if (temp_status.get(6).booleanValue() == true)  // did we need houses?
    {                                               // houses take priority
      if ((housing_deficit * 3) < metallicAlloys_m.get().GetAmount()) // do we have enough
      {                                                            // to build them?
        alloys_deficit = housing_deficit * 3;
        alloys_deficit = alloys_deficit - metallicAlloys_m.get().GetAmount();
        temp_status.set(3, Boolean.valueOf(true)); // need alloys
      }
    } // end if
    else if (temp_status.get(5).booleanValue() == true && alloys_deficit == 0)
    {
      if ((electronics_deficit * 2) > metallicAlloys_m.get().GetAmount())
      {
        alloys_deficit = electronics_deficit * 2;
        alloys_deficit = alloys_deficit - metallicAlloys_m.get().GetAmount();
          
        temp_status.set(3, Boolean.valueOf(true)); // need alloys
      } // end if
    } // end else if
    else
    {
      alloys_surplus = metallicAlloys_m.get().GetAmount() / 2;
    } // end else
        
    // Calculate need for Metallic Elements
    if (temp_status.get(3).booleanValue() == true)
    {                                             // do we need alloys?
      if ((alloys_deficit * 2) > metallicElems_m.get().GetAmount())
      {
        metallic_deficit = (alloys_deficit * 2) - metallicElems_m.get().GetAmount();
        temp_status.set(1, Boolean.valueOf(true)); // need metallic elements
      } // end if
    } // end if
    else if (temp_status.get(6).booleanValue() == true && metallic_deficit == 0)
    {                                          // do we need houses?
      if (housing_deficit > metallicElems_m.get().GetAmount())
      {
        metallic_deficit = metallicElems_m.get().GetAmount() - housing_deficit;
        temp_status.set(1, Boolean.valueOf(true)); // need metallic elements
      }
    }
    else if(temp_status.get(5).booleanValue() == true && metallic_deficit == 0)
    {                                          // do we need electronics?
      if ((electronics_deficit * 3) > metallicElems_m.get().GetAmount())
      {
        metallic_deficit = (electronics_deficit * 3) - metallicElems_m.get().GetAmount();
        temp_status.set(1, Boolean.valueOf(true)); // need metallic elements
      }
    }
    else
    {
      metallic_surplus = metallicElems_m.get().GetAmount() / 2;
    }
    
    // Calculate Need for Timber
    if (temp_status.get(6).booleanValue() == true)
    {
      if((housing_deficit * 5) < timber_m.get().GetAmount())
      {
        timber_deficit = (housing_deficit * 5) - timber_m.get().GetAmount();
        temp_status.set(2, Boolean.valueOf(true)); // need timber
      } // end if
      else
      {
        timber_surplus = timber_m.get().GetAmount() / 2;
      }
    } // end if
    else
    {
      timber_surplus = timber_m.get().GetAmount() / 2;
    }
    
    // See if we have a surplus of Wastes
    // Check for surplus of HousingWaste
    if ((housingWaste_m.get().GetAmount() - housing_m.get().GetAmount()) > 1)
    {
      temp_status.set(7, Boolean.valueOf(true)); // too much waste
      housing_waste_over = housingWaste_m.get().GetAmount() - housing_m.get().GetAmount();
    }
    else
    {
      housing_waste_under = housing_m.get().GetAmount() - housingWaste_m.get().GetAmount();
    }
    
    // Check for surplus of Alloy Waste
    if ((metallicAlloysWaste_m.get().GetAmount() - metallicAlloys_m.get().GetAmount()) > 1)
    {
      temp_status.set(4, Boolean.valueOf(true)); // too much waste
      alloy_waste_over = metallicAlloysWaste_m.get().GetAmount() - metallicAlloys_m.get().GetAmount();
    }
    else
    {
      alloy_waste_under = metallicAlloys_m.get().GetAmount() - metallicAlloysWaste_m.get().GetAmount();
    }
    
    // Set Status class instance with values set by above statements
    boolean check = false;
    check = status_m.get().Set_Status(temp_status);
    
    if (!(check))
    {
      System.out.println("Failed to set Status");
      return false;
    }
      
    temp_deficits.set(0, Long.valueOf(pop_deficit));
    temp_deficits.set(1, Long.valueOf(metallic_deficit));
    temp_deficits.set(2, Long.valueOf(timber_deficit));
    temp_deficits.set(3, Long.valueOf(alloys_deficit));
    temp_deficits.set(4, Long.valueOf(alloy_waste_under));
    temp_deficits.set(5, Long.valueOf(electronics_deficit));
    temp_deficits.set(6, Long.valueOf(housing_deficit));
    temp_deficits.set(7, Long.valueOf(housing_waste_under));
    
    check = status_m.get().Set_Deficits(temp_deficits);
    
    if (!(check))
    {
      System.out.println("Failed to set Deficits");
      return false;
    }
      
    temp_surplus.set(0, Long.valueOf(pop_surplus));
    temp_surplus.set(1, Long.valueOf(metallic_surplus));
    temp_surplus.set(2, Long.valueOf(timber_surplus));
    temp_surplus.set(3, Long.valueOf(alloys_surplus));
    temp_surplus.set(4, Long.valueOf(alloy_waste_over));
    temp_surplus.set(5, Long.valueOf(electronics_surplus));
    temp_surplus.set(6, Long.valueOf(housing_surplus));
    temp_surplus.set(7, Long.valueOf(housing_waste_over));
    
    check = status_m.get().Set_Surplus(temp_surplus);
    
    if (!(check))
    {
      System.out.println("Failed to set Surplus");
      return false;
    }
      
    return true;
  }
  
  //! Troubleshooting method to print country name and current Resource values
  public void printDetails()
  {
    System.out.println(name_m);
    System.out.println(population_m.get().GetName() + ": " + population_m.get().GetAmount());
    System.out.println(metallicElems_m.get().GetName() + ": " + metallicElems_m.get().GetAmount());
    System.out.println(timber_m.get().GetName() + ": " + timber_m.get().GetAmount());
    System.out.println(metallicAlloys_m.get().GetName() + ": " + metallicAlloys_m.get().GetAmount());
    System.out.println(metallicAlloysWaste_m.get().GetName() + ": " + metallicAlloysWaste_m.get().GetAmount());
    System.out.println(electronics_m.get().GetName() + ": " + electronics_m.get().GetAmount());
    System.out.println(housing_m.get().GetName() + ": " + housing_m.get().GetAmount());
    System.out.println(housingWaste_m.get().GetName() + ": " + housingWaste_m.get().GetAmount());    
  }
  
  public void printStatus()
  {
    status_m.get().Print();
  }

}
