package Hermes.Resources;

import java.util.*;

//! Class declaration for a Status
public class Status {

  //! Variable for the overall status in terms of the following fields:
  //! 
  //! Population            needed(true) / not needed(false)
  //! Metallic Elements     needed(true) / not needed(false)
  //! Timber                needed(true) / not needed(false)
  //! Metallic Alloys       needed(true) / not needed(false)
  //! Metallic Alloys Waste surplus(true) / no surplus(false)
  //! Electronics           needed(true) / not needed(false)
  //! Housing               needed(true) / not needed(false)
  //! Housing Waste         surplus(true) / no surplus(false)
  private ArrayList<Boolean> status_m;
  
  //! Variable for list of deficits in same order as status_m
  private ArrayList<Long> deficits_m;
  
  //! Variable for list of surpluses in same order as status_m
  private ArrayList<Long> surplus_m;
  
  private static ArrayList<String> values_m = new ArrayList<String>(
            Arrays.asList("Population",
                          "Metallic Elements",
                          "Timber",
                          "Metallic Alloys",
                          "Metallic Alloys Waste",
                          "Electronics",
                          "Housing",
                          "Housing Waste"
                          ));
  
  //! Constructor
  public Status()
  {
    status_m = new ArrayList<>(Arrays.asList(
      Boolean.valueOf(false),
      Boolean.valueOf(false), 
      Boolean.valueOf(false), 
      Boolean.valueOf(false), 
      Boolean.valueOf(false),
      Boolean.valueOf(false), 
      Boolean.valueOf(false),
      Boolean.valueOf(false)
    ));
    deficits_m = new ArrayList<>(Arrays.asList(
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0)
    ));
    surplus_m = new ArrayList<>(Arrays.asList(
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0),
      Long.valueOf(0)
    ));
  }
  
  //! Setter method for Status
  public boolean Set_Status(ArrayList<Boolean> status)
  {
    if (status == null || status.size() != 8)
    {
      return false;
    }
    
    for (int i = 0; i < status_m.size(); i++)
    {
      status_m.set(i, status.get(i));
    }
    
    return true;
  }
  
  //! Getter method for Status
  public ArrayList<Boolean> Get_Status()
  {
    return status_m;
  }
  
  //! Setter method for Surplus
  public boolean Set_Surplus(ArrayList<Long> surplus)
  {
    if (surplus == null || surplus.size() != 8)
    {
      return false;
    }
    
    for (int i = 0; i < surplus_m.size(); i++)
    {
      surplus_m.set(i, surplus.get(i));
    }
    
    return true;
  }
  
  //! Getter method for Surplus
  public ArrayList<Long> Get_Surplus()
  {
    return surplus_m;
  }
  
  //! Setter method for Deficits
  public boolean Set_Deficits(ArrayList<Long> deficits)
  {
    if (deficits == null || deficits.size() != 8)
    {
      return false;
    }
    
    for (int i = 0; i < deficits_m.size(); i++)
    {
      deficits_m.set(i, deficits.get(i));
    }
    
    return true;
  }
  
  //! Getter method for Deficits
  public ArrayList<Long> Get_Deficits()
  {
    return deficits_m;
  }
  
  public void Print()
  {
    for (int i = 0; i < status_m.size(); i++)
    {
      System.out.println(
        values_m.get(i) +
        ": " +
        status_m.get(i) +
        "; Deficit: " +
        deficits_m.get(i) +
        "; Surplus: " +
        surplus_m.get(i)
      );
    } // end for
  }
}
