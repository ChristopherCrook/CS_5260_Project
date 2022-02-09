import java.util.*;
import Hermes.Resources.Resource;
import Hermes.Transforms.*;
import Hermes.Country;

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
    
    narnia.printDetails();
    
    System.out.println("/---- End Country Test ----/");
    
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
  }
}
