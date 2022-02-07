package Hermes;

import Hermes.Resources.Resource;
import Hermes.Transforms.*;

import java.util.concurrent.atomic.*;
import java.util.*; 
import java.io.*; 

//! Class declaration for a Country
public class Country implements Runnable, Scheduler {

  static final String POPULATION = new String("population");
  static final String METALLICELEMS = new String("metallic elems");
  static final String TIMBER = new String("timber");
  static final String METALLICALLOY = new String("metallic alloys");
  static final String METALLICWASTE = new String("metallic alloy waste");
  static final String ELECTRONICS = new String("electronics");
  static final String HOUSING = new String("housing");
  static final String HOUSINGWASTE = new String("housing waste");

  private String name_m = new String();

  private AtomicReference<Resource> population_m;
  private AtomicReference<Resource> metallicElems_m;
  private AtomicReference<Resource> timber_m;
  private AtomicReference<Resource> metallicAlloys_m;
  private AtomicReference<Resource> metallicAlloysWaste_m;
  private AtomicReference<Resource> electronics_m;
  private AtomicReference<Resource> housing_m;
  private AtomicReference<Resource> housingWaste_m;
  
  private HousingTransform     h_trans_m;
  private AlloyTransform       a_trans_m;
  private ElectronicsTransform e_trans_m;
  
  private volatile int statusVal_m;
  private volatile boolean configured_m;
  
  private int schedules_m;
  private int depth_m;
  private int frontier_m;

  //! Constructor
  public Country()
  {
    // Initialize private variables
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
    
    statusVal_m = 0;
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
        
    String line = new String();
    String splitBy = new String(",");
    
    // Get the country info
    try
    {
      BufferedReader resource_reader = new BufferedReader(new FileReader(resource_filename));
    
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
        
          break;
        }
      }
    }
    catch (IOException e)
    {
      System.out.println("File error!");
    }
    
    schedules_m = num_output_schedules;
    depth_m = depth_bound;
    frontier_m = frontier_max_size;
    configured_m = true;
  }
  
  public void run()
  {
  
  }
  
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

}
