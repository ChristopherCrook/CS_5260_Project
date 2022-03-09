package Hermes;

import Hermes.Resources.Resource;
import Hermes.Resources.Status;
import Hermes.Transforms.*;
import Hermes.Trade.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*; 
import java.io.*;
import java.io.BufferedWriter;

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
  
  //! Variable for the Queue
  private ArrayBlockingQueue<Entry> queue_m;

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
  private volatile int schedules_m;
  private volatile int depth_m;
  private volatile int frontier_m;
  private String output_m;
  private BufferedWriter writer_m;

  //! Constructor // ----------------------------------------------------------
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
  
  //! Method to set the blocking queue // ------------------------------------
  public void SetQueue(ArrayBlockingQueue<Entry> queue)
  {
    queue_m = queue;
  }
  
  //! Method to get country name // ------------------------------------------
  public String GetName()
  {
    return name_m;
  }
  
  //! Inherited schedule method from Scheduler // ----------------------------
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
        
    // Assign name of the country    
    name_m = name;
    
    // These variables are used to read the CSV contents
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
    
    // Set the output file name and create the BufferedReader
    output_m = new String(output_schedule_filename.toCharArray());
    
    try {
      writer_m = new BufferedWriter(new FileWriter(output_m));
      writer_m.write(new String(name_m).concat(new String(" file started")));
      writer_m.flush();
    }
    catch (IOException e)
    {
      System.out.println("File error!");
    }
    
    // Set the scheduler variables
    schedules_m = num_output_schedules;
    depth_m = depth_bound;
    frontier_m = frontier_max_size;
    
    // Set the configured flag to true. Class is now ready.
    configured_m = true;
  }
  
  //!--------------------------------------------------------------------------
  
  //! Overloaded run() method inherited from Runnable // ----------------------
  public void run()
  {
    // This is the HashMap that will hold our search results
    HashMap<String, Status> mMap = new HashMap<>();
    
    // Calculate Initial Status
    Status initial = new Status();
    
    boolean check = CalculateStatus(initial);
    if (!(check))
    {
      System.out.println("Error setting initial state");
      return;
    }
    
    // Set initial state
    status_m.set(initial);
    mMap.put(new String("Initial"), initial);
    
    // Set counter values
    AtomicInteger count = new AtomicInteger(0);
    
    // Begin Node search
    GenerateNode(mMap, initial, count);
    
  }
  
  //! Recursive method to iterate over nodes // -------------------------------
  public void GenerateNode(
    HashMap<String, Status> map,
    Status state,
    AtomicInteger i
  )
  {
    // Increment the count
    i.set(i.get() + 1);
    
     // Test the frontier and bound limit
    if (frontier_m < i.get() || bound_m < i.get())
      return;
    
    // Placeholder variables
    Resource need = null;
    Resource offer = null;
    
    // Set a flag for finding a needed resource
    boolean current_need_found = false;
    
    // Determine the immediate need based on our hierarchy
    //
    //
    // Do we need population?
    if (state.Get_Status()[0] == true) // Do we need population
    {
      // Yes
      need = CreateResource(
        state.values_m.get(0),
        state.Get_Deficits().get(0).longValue()
      );
      
      current_need_found = true;
    } // end if
    //
    // Do we need houses?
    else if (state.Get_Status()[6] == true && current_need_found == false)
    {
      // Yes
      // Do we have enough materials to make them? If we do, we call a 
      // transform. If not we get what we need first.
      
      // Do we have enough metallic elements?
      if (state.Get_Status()[1] == true)
      {
        need = CreateResource(
          state.values_m.get(1),
          state.Get_Deficits().get(1).longValue()
        );
      
        current_need_found = true;
      }
      // Do we have enough alloys
      else if (state.Get_Status()[3] == true && current_need_found == false)
      {
        // Yes. If we get here, we assume we can make them
        // Initiate transform
        boolean r;
        
        for (long i = 0; i < state.Get_Deficits().get(3).longValue(); i++)
        {
          r = a_trans_m.Transform(
            population_m,
            metallicElems_m,
            metallicAlloys_m,
            metallicAlloyWaste_m
          );
          
          if (!(r))
              System.out.print(name_m + ": Error performing Alloy Transform");
        } // end for
        
        // Create next iteration Status and add to map
        String a_t = new String("Performed Alloy Transform: ");
        a_t.concat(state.Get_Deficits().get(3).toString());

        map.put(new String(a_t), state);
          
        result = this.CalculateStatus(state);
        this.GenerateNode(map, state, i);
          
        return;
      }
      // Do we need Timber?
      else if (state.Get_Status()[2] == true && current_need_found == false)
      {
        // Yes
        need = CreateResource(
          state.values_m.get(2),
          state.Get_Deficits().get(2).longValue()
        );
      
        current_need_found = true;
      }
      // We have everything we need to make a house
      else
      {
        // This should never be true, but just in case
        if (!(current_need_found))
        {
          // Build houses
          // create a return variable
          boolean result;
          
          // Loop until we have the amount needed
          for (long i = 0; i < state.Get_Deficits().get(6).longValue(); i++)
          {
            result = h_trans_m.Transform(
              population_m,
              metallicElems_m,
              timber_m,
              metallicAlloys_m,
              housing_m,
              housingWaste_m
            );
            next iteration Status and add to map
            if (!(result))
              System.out.print(name_m + ": Error performing Housing Transform");
          } // end for
          // Create next iteration Status and add to map
          String h_t = new String("Performed Housing Transform: ");
          h_t.concat(state.Get_Deficits().get(6).toString());

          map.put(new String(h_t), state);
          
          result = this.CalculateStatus(state);
          this.GenerateNode(map, state, i);
          
          return;
        } // end if !current_need_found
      } // end else
    } // else if (housing needed)
    // Do we need electronics?
    else if (state.Get_Status()[5] == true && current_need_found == false)
    {
      // Yes
      // Do we have enough materials to make them? If we do, we call a 
      // transform. If not we get what we need first.
      if (state.Get_Status()[1] == true && current_need_found == false)
      {
        need = CreateResource(
          state.values_m.get(1),
          state.Get_Deficits().get(1).longValue()
        );
      
        current_need_found = true;
      }
      // Do we need alloys?
      else if (state.Get_Status()[3] == true && current_need_found == false)
      {
        // Yes. If we get here, we assume we can make them       
        // Initiate transform
        boolean r;
        
        for (long i = 0; i < state.Get_Deficits().get(3).longValue(); i++)
        {
          r = a_trans_m.Transform(
            population_m,
            metallicElems_m,
            metallicAlloys_m,
            metallicAlloyWaste_m
          );
          
          if (!(r))
              System.out.print(name_m + ": Error performing Alloy Transform");
        } // end for
        
        // Create next iteration Status and add to map
        String a_t = new String("Performed Alloy Transform: ");
        a_t.concat(state.Get_Deficits().get(3).toString());

        map.put(new String(a_t), state);
          
        result = this.CalculateStatus(state);
        this.GenerateNode(map, state, i);
          
        return;
      } // end else if
      else
      {
        if (!(current_need_found))
        {
          // We have everything we need to make electronics
          // Initiate Transform
          boolean t;
        
          for (long i = 0; i < state.Get_Deficits().get(3).longValue(); i++)
          {
              t = e_trans_m.Transform(
              population_m,
              metallicElems_m,
              metallicAlloys_m,
              electronics_m,
              metallicAlloyWaste_m
            );
          
            if (!(t))
                System.out.print(name_m + ": Error performing Electronics Transform");
          } // end for
        
          // Create next iteration Status and add to map
          String e_t = new String("Performed Electronics Transform: ");
          e_t.concat(state.Get_Deficits().get(5).toString());

          map.put(new String(e_t), state);
          
          result = this.CalculateStatus(state);
          this.GenerateNode(map, state, i);
          
          return;
        } // end if !current_need_found
      } // end else
    } // end else if (electronics need)
    // 
    else // We don't need People, Houses or Electronics
    {      
      // Check and see if we have a deficit anywhere
      /*for (long i = 0; i < state.Get_Deficits().size(); i++)
      {
        if (state.Get_Deficits().get(i).longValue() > 0)
        {
          need = CreateResource(
          state.values_m.get(i),
          state.Get_Deficits().get(i).longValue()
        );
      
        current_need_found = true;
        }
      } */ //XXX This block may not be needed. If we don't need
      // People, houses or electronics, do we care?
      // If so, update the Update methods
      
      // if we get here, we don't need anything or have anything to offer
      // we are balanced
    } // else
    
    // Get the Trade process started if we have a need
    if (current_need_found)
    {
      // Create the offer
      int s = this.GetHighestSurplusPosition(state);
      
      long offer_amount 0;
      if (need.GetAmount() > state.Get_Surlus().get().longValue())
      {
        // Try to trade what we have
        offer_amount = state.Get_Surlus().get().longValue();
      }
      else
      {
        offer_amount = need.GetAmount();
      }
      
      offer = CreateResource(
        state.values_m.get(s),
        offer_amount
      );
      
      // Now we create the Entry
      Entry trade = new Entry(
        this.GetName(),
        offer,
        need,
        false
      );
      
      // Add it to the queue
      queue_m.add(trade);
      while (trade.GetNoMatch() == false && trade.GetSuccess() == false)
      {
        try {
          Thread.sleep(1000);
        }
        catch (InterruptedException ie) {
          System.out.println("Thread interrupted");
        } // end catch
      } // end while
      
      // We have a result
      // Check and see if it failed
      if (trade.GetNoMatch())
      {
        // We are dead in the water
        String f = new String("Trade Failed for ");
        f.concat(need.GetName());
        
        map.put(new String(f), state);
        
        return;
      }
      
      // Extract the result
      this.AssignNewValues(trade);
      
      // Generate the new node
      String tr = new String("Traded for ");
      tr.concat(need.GetName());
        
      map.put(new String(tr), state);
      
      result = this.CalculateStatus(state);
      this.GenerateNode(map, state, i);
      
      return;
    } // end if current_need_found
    // If we get here, we check if we have a surplus because we don't need anything
    if (GetHighestSurplusPosition(state) > 0)
    {
      // Create new trade
      need = new Resource(
        this.GetLowestResource(state).GetName(),
        state.Get_Surplus().get(GetHighestSurplusPosition(state)).longValue()
      );
      
      offer = CreateResource(
        state.values_m.get(GetHighestSurplusPosition(state)),
        state.Get_Surplus().get(GetHighestSurplusPosition(state)).longValue()
      );
      
      // Now we create the Entry
      Entry trade = new Entry(
        this.GetName(),
        offer,
        need,
        true
      );
      
      // Add it to the queue
      queue_m.add(trade);
      while (trade.GetSuccess() == false)
      {
        try {
          Thread.sleep(1000);
        }
        catch (InterruptedException ie) {
          System.out.println("Thread interrupted");
        } // end catch
      } // end while
      
      // Extract the result
      this.AssignNewValues(trade);
      
      // Generate new node
      String ts = new String("Traded Surplus for ");
      ts.concat(need.GetName());
        
      map.put(new String(ts), state);
      
      result = this.CalculateStatus(state);
      this.GenerateNode(map, state, i);
      
      return;
    } // end if (GetHighestSurplusPosition(state) > 0)
    
    // We have the perfect system, Flynn!!!
    System.out.println(this.GetName() + "State is optimal");
    String o = new String("State is optimal")
        
    map.put(new String(tr), state);
    return;
  }
  
  //! Method to set the results of a trade
  protected void AssignNewValues(Entry entry)
  {
    String need  = new String(entry.GetNeed().GetName());
    String offer = new String(entry.GetOffer().GetName());
    
    // Start with what was needed
    if (need.equals(POPULATION))
    {
      population_m.get().SetAmount() = 
        population_m.get().SetAmount() + entry.GetGiven();
    }
    else if (need.equals(METALLICELEMS))
    {
      metallicElems_m.get().SetAmount() = 
        metallicElems_m.get().SetAmount() + entry.GetGiven();
    }
    else if (need.equals(TIMBER))
    {
      timber_m.get().SetAmount() = 
        timber_m.get().SetAmount() + entry.GetGiven();
    }
    else
    {
      // Invalid case
      System.out.println("Error trying to assign the results of an unexpected type");
      return;
    }
    
    // Now get the offer
    if (offer.equals(POPULATION))
    {
      population_m.get().SetAmount() = 
        population_m.get().SetAmount() - entry.GetTaken();
    }
    else if (offer.equals(METALLICELEMS))
    {
      metallicElems_m.get().SetAmount() = 
        metallicElems_m.get().SetAmount() - entry.GetTaken();
    }
    else if (offer.equals(TIMBER))
    {
      timber_m.get().SetAmount() = 
        timber_m.get().SetAmount() - entry.GetTaken();
    }
    else if (offer.equals(METALLICALLOY))
    {
      metallicAlloys_m.get().SetAmount() = 
        metallicAlloys_m.get().SetAmount() - entry.GetTaken();
    }
    else if (offer.equals(ELECTRONICS))
    {
      electronics_m.get().SetAmount() = 
        electronics_m.get().SetAmount() - entry.GetTaken();
    }
    else if (offer.equals(HOUSING))
    {
      housing_m.get().SetAmount() = 
        housing_m.get().SetAmount() - entry.GetTaken();
    }
    else
    {
      // Invalid case
      System.out.println("Error trying to assign the results of an unexpected type");
      return;
    } // end else
  }
  
  //! -------------------------------------------------------------------------
  
  //! Method to get the lowest resource amount we have //----------------------
  public Resource GetLowestResource(Status status)
  {
    int pos = -1;
    long lowest = -1;
    int count = 0;
    
    ArrayList<Resource> list = new ArrayList<>();
    list.add(population_m.get());
    list.add(metallicElems_m.get());
    list.add(timber_m.get());
    list.add(metallicAlloys_m.get());
    list.add(electronics_m.get());
    
    for (int i = 0; i < list.size(); i++)
    {
      if (lowest < 0)
        lowest = list.get(i).GetAmount();
      else if (list.get(i).GetAmount() < lowest)
        lowest = list.get(i).GetAmount();
    }    
      
    return list.stream().filter(l -> l.GetAmount() == lowest).findFirst();
  }
  
  //! Method to get the highest surplus // ------------------------------------
  public int GetHighestSurplusPosition(Status status)
  {
    int pos = -1;
    long surplus = 0;
    int count = 0;
    
    for (Long l : status.Get_Surplus())
    {
      if (l.longValue() > surplus)
      {
        surplus = l.longValue();
        pos = count;
      } // end if
      count++;
    } // end for
      
    return pos;
  }
  
  //! Method to get a Resource from a Status
  public Resource CreateResource(Status status, int pos)
  {
    if (pos < 0)
      return null;
      
    Resource returned = new Resource(
      status.values_m.get(pos),
      status.Get_Surplus().get(pos).longValue()
    );
    
    return returned;
  }
  
  //! Method to calculate the current state, which is very complicated
  //! XXX TO-DO make this simpler somehow
  public boolean CalculateStatus(Status status) // ----------------------------
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
    check = status.Set_Status(temp_status);
    
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
    
    check = status.Set_Deficits(temp_deficits);
    
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
    
    check = status.Set_Surplus(temp_surplus);
    
    if (!(check))
    {
      System.out.println("Failed to set Surplus");
      return false;
    }
    
    return true;
  }
  
  //! Test Method to print country name and current Resource values //---------
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
  
  //! Passthrough call to print the overall Country status // -----------------
  public void printStatus()
  {
    status_m.get().Print();
  }
  
  //! Logging method to print a status to the configured log file  // ---------
  public void exportStatus(Status status) // ----------------------------------
  {
    String state = new String();
    
    for (Boolean b : status.Get_Status())
    {
      if (b.booleanValue() == true)
        state.concat(new String("true; "));
      else
        state.concat(new String("false; "));
    }
    
    try {
      writer_m.write(state);
      writer_m.flush();
    }
    catch (IOException e)
    {
      System.out.println("File write error!");
    }
  }

}
