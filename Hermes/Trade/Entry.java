package Hermes.Trade;

import Hermes.Country;
import Hermes.Resources.Resource;

import java.util.concurrent.atomic.*;
import java.util.*;

//! Class declaration for a trade entry
public class Entry {

  //! Private variables
  private AtomicReference<Country> country_m;
  
  private String name_m;
  
  private AtomicReference<Resource> need_m;
  private AtomicReference<Resource> offer_m;
  
  private AtomicBoolean success_m;
  private AtomicBoolean no_match_m;
  
  private AtomicBoolean is_surplus_m;
  
  //! Constructor
  public Entry(Country country, Resource offer, Resource need, boolean is_surplus)
  {
    country_m = new AtomicReference<>(country);
    
    name_m = new String(country.GetName().toCharArray());
    need_m = new AtomicReference<>(need);
    offer_m = new AtomicReference<>(offer);
    
    is_surplus_m = new AtomicBoolean(is_surplus);
    
    success_m = new AtomicBoolean(false);
    no_match_m = new AtomicBoolean(false);
  }

  //! Method to get the country name
  public String GetName()
  {
    return name_m;
  }
  
  //! Method to get the Country instance
  public AtomicReference<Country> GetCountry()
  {
    return country_m;
  }
  
  //! Method to return whether or not this offer is due to surplus
  public boolean IsSurplus()
  {
    return is_surplus_m.get();
  }
  
  //! Method to get the Offer
  public Resource GetOffer()
  {
    return offer_m.get();
  }
  
  //! Method to get the Need
  public Resource GetNeed()
  {
    return need_m.get();
  }
  
  //! Method to set the result of the trade entry
  public boolean SetSuccessToTrue()
  {
    return success_m.compareAndSet(false, true); 
  }
  
  //! Method to get the result of the trade entry
  public boolean GetSuccess()
  {
    return success_m.get();
  }
  
  //! Method to set that a match is not possible
  public boolean SetNoMatchToTrue()
  {
    return no_match_m.compareAndSet(false, true); 
  }
  
  //! Method to get whether no match is true
  public boolean GetNoMatch()
  {
    return no_match_m.get();
  }
}
