package Hermes.Transforms;

import Hermes.Resources.Resource;

//! Class definition for a Electronics Transform
public class ElectronicsTransform {

  public ElectronicsTransform()
  {
  }
  
  //! Method to perform a transformation
  public boolean Transform(
    Resource population,          // inout 1
    Resource metallicElems,       // in    3
    Resource metallicAlloys,      // in    2
    Resource electronics,         // out   2
    Resource electronicsWaste     // out   1
  )
  {
    // Check pre-conditions
    if (population == null ||
        metallicElems == null ||
        metallicAlloys == null ||
        electronics == null ||
        electronicsWaste == null)
        return false; 
    
    if (population.GetAmount() < 1)
      return false;
      
    if (metallicElems.GetAmount() < 3)
      return false;
      
    if (metallicAlloys.GetAmount() < 2)
      return false;
  
    // Perform transforms
    metallicElems.SetAmount(metallicElems.GetAmount() - 3);
    metallicAlloys.SetAmount(metallicAlloys.GetAmount() - 2);
    
    electronics.SetAmount(electronics.GetAmount() + 2);
    electronicsWaste.SetAmount(electronicsWaste.GetAmount() + 1);
    
    return true;
  }

}
