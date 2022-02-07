import java.util.*;
import Hermes.Resources.Resource;

//! Class to test Hermes classes
public class TestRunner {

  //! Test the Resource class
  public static boolean TestResource()
  {
    System.out.println("/--- Begin Resource Test ---/");
    Resource r1 = new Resource();
    r1.SetName(new String("population"));
    r1.SetAmount(200000);
    
    if (!(r1.GetName().equals("population")))
      return false;
    if(!(r1.GetAmount() == 200000))
      return false;
      
    Resource r2 = new Resource(r1.GetName(), r1.GetAmount());
    
    if (!(r2.GetName().equals("population")))
      return false;
    if(!(r2.GetAmount() == 200000))
      return false;
    
    System.out.println("/---- End Resource Test ----/");
    return true;
  }

  public static void main(String[] args)
  {
    if (!(TestResource()))
      System.out.println("Resource Test Failed");
  }
}
