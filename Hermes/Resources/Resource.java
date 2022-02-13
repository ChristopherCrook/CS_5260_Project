package Hermes.Resources;

import java.util.*;

//! Class declaration for a Resource
public class Resource {

  private String name_m;
  private long amount_m;

  //! Constructor
  public Resource()
  {
    name_m = new String();
    amount_m = 0;
  }
  
  //! Constructor with args
  public Resource(String name, long amount)
  {
    name_m = new String(String.copyValueOf(name.toCharArray()));
    amount_m = amount;
  }
  
  //! Method to set the name of the resource
  public void SetName(String name)
  {
    name_m = String.copyValueOf(name.toCharArray());
  }
  
  //! Method to set the amount of the resource
  public void SetAmount(long amount)
  {
    amount_m = amount;
  }
  
  //! Method to return the name
  public String GetName()
  { return name_m; }
  
  //! Method to get the amount
  public long GetAmount()
  { return amount_m; }
}
