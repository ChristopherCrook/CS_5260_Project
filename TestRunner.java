import java.util.*;
import Hermes.Resources.Resource;
import Hermes.Transforms.*;
import Hermes.Country;
import Hermes.Trade.*;

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
  
  //! Test the Alloy Transform class
  public static boolean TestAlloyTransform()
  {
    System.out.println("/--- Begin Alloy Transform Test ---/");
    Resource p = new Resource(new String("population"), 1);
    Resource e = new Resource(new String("metallic elements"), 2);
    Resource ma = new Resource(new String("metallic alloys"), 0);
    Resource maw = new Resource(new String("metallic alloys waste"), 0);
    
    AlloyTransform transform = new AlloyTransform();
    
    boolean test = transform.Transform(p, e, ma, maw);
    
    if (!(test))
      return false;
      
    if (ma.GetAmount() != 1)
      return false;
      
    if (maw.GetAmount() != 1)
      return false;
      
    test = transform.Transform(p, e, ma, maw);
    
    if (test)
      return false;
      
    System.out.println("/---- End Alloy Transform Test ----/");
    
    return true;
  }
  
  //! Test the Country class
  public static boolean TestCountry()
  {
    System.out.println("/--- Begin Country Test ---/");
    Country narnia = new Country();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String name = new String("Picon");
    
    narnia.schedule(
      name,
      file,
      test,
      test,
      1,
      1,
      1
    );
    
    //narnia.printDetails();
    //boolean check = narnia.CalculateStatus();
    //narnia.printStatus();
    
    System.out.println("/---- End Country Test ----/");
    
    return true;
  }
  
  //! Test the Trade Entry class
  public static boolean TestEntry()
  {
    System.out.println("/--- Begin Entry Test ---/");
    Country narnia = new Country();
    
    String test = new String("test");
    String file = new String("countries.csv");
    String name = new String("Picon");
    
    narnia.schedule(
      name,
      file,
      test,
      test,
      1,
      1,
      1
    );
    
    Resource rsrc = new Resource(new String("substance"), 1000);
    Entry entry = new Entry(narnia, rsrc);
    
    if (!(entry.GetName().equals("Picon")))
      return false;
      
    if (!(entry.GetCountry().get().equals(narnia)))
      return false;
      
    if (!(entry.GetResource().GetName().equals("substance")))
      return false;
      
    if (entry.GetResource().GetAmount() != 1000)
      return false;
      
    if (entry.GetResult() != false)
      return false;
      
    boolean setResult = entry.SetResultToTrue();
    
    if (setResult != true)
      return false;
      
    if(entry.GetResult() != true)
      return false;
    
    System.out.println("/---- End Entry Test ----/");
    return true;
  }

  //! Program Execution point
  public static void main(String[] args)
  {
    if (!(TestResource()))
      System.out.println("Resource Test Failed");
      
    if (!(TestAlloyTransform()))
      System.out.println("Alloy Transform Test Failed");
      
    if (!(TestCountry()))
      System.out.println("Country Test Failed");
      
    if(!(TestEntry()))
      System.out.println("Entry Test Failed");
  }
}
