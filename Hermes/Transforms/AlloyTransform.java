package Hermes.Transforms;

import Hermes.Resources.Resource;

//! Class definition for a Alloy Transform
public class AlloyTransform {

  public AlloyTransform()
  {
  }
  
  //! Method to perform a transformation
  public boolean Tranform(
    Resource population,          // inout 1
    Resource metallicElems,       // in    2
    Resource metallicAlloys,      // out   1
    Resource metallicAlloysWaste  // out   1
  )
  {
    // Check pre-conditions
    if (population == null ||
        metallicElems == null ||
        metallicAlloys == null ||
        metallicAlloysWaste == null)
        return false; 
    
    if (population.GetAmount() < 1)
      return false;
      
    if (metallicElems.GetAmount() < 2)
      return false;
  
    // Perform Transforms
    metallicElems.SetAmount(metallicElems.GetAmount() - 2);
    
    metallicAlloys.SetAmount(metallicAlloys.GetAmount() + 1);
    metallicAlloysWaste.SetAmount(metallicAlloysWaste.GetAmount() + 1);
    
    return true;
  }

}
