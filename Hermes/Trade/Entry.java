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
  private AtomicReference<Resource> resource_m;
  private AtomicBoolean success_m;
  
  //! Constructor
  public Entry(Country country, Resource resource)
  {
    country_m = new AtomicReference<>(country);
    name_m = new String(country.GetName().toCharArray());
    resource_m = new AtomicReference<>(resource);
    success_m = new AtomicBoolean(false);
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
  
  //! Method to get the Resource
  public Resource GetResource()
  {
    return resource_m.get();
  }
  
  //! Method to set the result of the trade entry
  public boolean SetResultToTrue()
  {
    return success_m.compareAndSet(false, true); 
  }
  
  //! Method to get the result of the trade entry
  public boolean GetResult()
  {
    return success_m.get();
  }
}
