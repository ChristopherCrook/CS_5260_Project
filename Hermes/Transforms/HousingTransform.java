package Hermes.Transforms;

import Hermes.Resources.Resource;

//! Class definition for a Housing Transform
public class HousingTransform {

  public HousingTransform()
  {
  }
  
  //! Method to perform a transformation
  public boolean Transform(
    Resource population,     // inout 5
    Resource metallicElems,  // in    1
    Resource timber,         // in    5
    Resource metallicAlloys, // in    3
    Resource housing,        // out   1
    Resource housingWaste   // out   1
  )
  {
    // Check pre-conditions
    if (population == null ||
        metallicElems == null ||
        timber == null ||
        metallicAlloys == null ||
        housing == null ||
        housingWaste == null )
        return false;
    
    if (population.GetAmount() < 5)
      return false;
      
    if (metallicElems.GetAmount() < 1)
      return false;
      
    if (timber.GetAmount() < 5)
      return false;
      
    if (metallicAlloys.GetAmount() < 3)
      return false;
      
    // Perform Transform    
    metallicElems.SetAmount(metallicElems.GetAmount() - 1);
    timber.SetAmount(timber.GetAmount() - 5);
    metallicAlloys.SetAmount(metallicAlloys.GetAmount() - 3);
    
    housing.SetAmount(housing.GetAmount() + 1);
    housingWaste.SetAmount(housingWaste.GetAmount() + 1);
    
    return true;
  }

}
