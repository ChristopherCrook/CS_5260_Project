package Hermes.Resources;

import java.util.*;

// Class declaration for a Resource
public class Resource {

  private String name_m;
  private long amount_m;

  public Resource()
  {
    name_m = new String();
    amount_m = 0;
  }
  
  public Resource(String name, long amount)
  {
    name_m = new String(String.copyValueOf(name.toCharArray()));
    amount_m = amount;
  }
  
  public void SetName(String name)
  {
    name_m = String.copyValueOf(name.toCharArray());
  }
  
  public void SetAmount(long amount)
  {
    amount_m = amount;
  }
  
  public String GetName()
  { return name_m; }
  
  public long GetAmount()
  { return amount_m; }
}
