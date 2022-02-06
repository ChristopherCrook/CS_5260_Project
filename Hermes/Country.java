package Hermes;

import Hermes.Resources.Resource;
import Hermes.Transforms.*;

import java.util.concurrent.atomic.*;

//! Class declaration for a Country
public class Country implements Runnable, Scheduler {

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
    schedules_m = num_output_schedules;
    depth_m = depth_bound;
    frontier_m = frontier_max_size;
    configured_m = true;
  }
  
  public void run()
  {
  
  }

}
