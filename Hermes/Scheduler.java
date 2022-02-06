package Hermes;

import java.util.*;

//! Interface declaration for a Scheduler
public interface Scheduler {

  //! Method that defines a schedulde given various inputs
  public void schedule(
    String name,
    String resource_filename,
    String initial_state_filename,
    String output_schedule_filename,
    int num_output_schedules,
    int depth_bound,
    int frontier_max_size 
  );
}
