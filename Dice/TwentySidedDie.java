package Dice;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.lang.Math;

//! Class to simulate a 20-Sided Die
//!
public class TwentySidedDie {

  private Random random_m;

  // Constructor
  public TwentySidedDie()
  {
    random_m = new Random();
  }
  
  //! Method to get the results of a 20-sided die roll
  public int Roll()
  {
    return random_m.nextInt(20) + 1;
  }
  
  //! Method to Calculate attack scale based on a 20-Sided Die
  //!
  //! args:
  //! int roll - the result of a 20-sided die roll
  //! int mod  - the calculated modifier for an attack between -27 and 27
  //!
  //! Integer att - a percentage reference for the amount of damage done to
  //! the attacker
  //! Integer def - a percentage reference for the amount of damage done to 
  //! the defending country
  public void GetAttackScale(int roll, int mod, AtomicInteger att, AtomicInteger def, boolean outnumber)
  {    
    if (roll == 1) // Attack went terrible potentially had losses
    {
      if (mod >= 15)
      {
        att.set(0); // No loss
        def.set(0);
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(2); // 2% loss
        def.set(0);
      }
      else if (mod > -12)
      {
        if (outnumber)
          att.set(1);
        else
         att.set(3); // should be a negative number
        def.set(0);
      }
      else if (mod > -16)
      {
        if (outnumber)
          att.set(2);
        else
         att.set(5);
        def.set(0);
      }
      else
      {
        if (outnumber)
          att.set(5);
        else
         att.set(2 * Math.abs(mod));
        def.set(0);
      }
      
      return;
    }
    else if (roll == 2) //  Attack went very bad and may have had losses
    {
      if (mod >= 10)
      {
        att.set(0); // No loss
        def.set(0);
      }
      else if (mod < 10 && mod >= -12)
      {
        if (outnumber)
          att.set(0);
        else
         att.set(1); // 1% loss
        def.set(0);
      }
      else if (mod > -16)
      {
        if (outnumber)
          att.set(1);
        else
         att.set(2);
        def.set(0);
      }
      else
      {
        if (outnumber)
          att.set(2);
        else
         att.set(Math.abs(mod));
        def.set(0);
      }
      
      return;
    }
    else if (roll == 3) // Attack went bad and was not effective
    {
      if (mod > -1)
      {
        att.set(0); // No loss
        def.set(0);
      }
      else if (mod <= -1 && mod > -10)
      {
        att.set(1); // 1% damage
        def.set(0);
      }
      
      else if (mod > -12)
      {
        att.set(0);
        def.set(0);
      }
      else if (mod > -16)
      {
        if (outnumber)
          att.set(0);
        else
         att.set(1);
        def.set(0);
      }
      else
      {
        if (outnumber)
          att.set(1);
        else
         att.set(2);
        def.set(0);
      }
      
      return;
    }
    else if (roll == 4) // Attach did not go well, but was slightly effective
    {
      if (mod >= 15)
      {
        att.set(0); 
        def.set(4); // 5% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0); 
        def.set(2); // 2% effective
      }
      else if (mod < 0 && mod > -10)
      {
        att.set(0); // no loss
        def.set(0);
      }
      else if (mod > -16)
      {
        att.set(0);
        def.set(0);
      }
      else
      {
        if (outnumber)
          att.set(0);
        else
         att.set(1);
        def.set(0);
      }
      
      return;
    }
    else if (roll == 5) // Attack did not go as planned, but was slighly effective
    {
      if (mod >= 10)
      {
        att.set(0);
        def.set(5); // 5% effective
      }
      else if (mod < 10 && mod >= 0)
      {
        att.set(0);
        def.set(3); // 2% effective
      }
      else
      {
        att.set(0);
        def.set(0);
      }
      
      return;
    }
    else if (roll == 6) // Attack could have been better and was only moderately effective
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(6); // 6% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(4); // 3% effective
      }
      else
      {
        att.set(0); // // 3% damage
        def.set(0); // 1% damage
      }
      
      return;
    }
    else if (roll == 7) // Attack was moderately effective
    {
      if (mod >= 10)
      {
        att.set(0);
        def.set(7); // 7% effective
      }
      else if (mod < 10 && mod >= 0)
      {
        att.set(0);
        def.set(5); // 5% effective
      }
      else if (mod < 0 && mod > -15)
      {
        att.set(0); // no damage
        def.set(0);
      }
      else
      {
        
        if (outnumber)
          att.set(0);
        else
         att.set(2); // // 2% damage
        def.set(1); // 1% damage
      }
      
      return;
    }
    else if (roll == 8) // Attack could have been better, but damage was done
    {
      if (mod >= 10)
      {
        att.set(0);
        def.set(10); // 10% effective
      }
      else if (mod < 10 && mod >= 0)
      {
        att.set(0);
        def.set(7); // 7% effective
      }
      else if (mod < 0 && mod > -10)
      {
        att.set(0); // no damage
        def.set(0);
      }
      else
      {
        if (outnumber)
          att.set(0);
        else
         att.set(2); // // 2% damage
        def.set(1); // 1% damage
      }
      
      return;
    }
    else if (roll == 9) // Attack had some setbacks, but was successful
    {
      if (mod >= 10)
      {
        att.set(0);
        def.set(12); // 12% effective
      }
      else if (mod < 10 && mod >= 0)
      {
        att.set(0);
        def.set(8); // 7% effective
      }
      else if (mod < 0 && mod > -10)
      {
        att.set(0); // no damage
        def.set(0);
      }
      else
      {
        if (outnumber)
          att.set(0);
        else
         att.set(2); // // 2% damage
        def.set(1); // 1% damage
      }
      
      return;
    }
    else if (roll == 10) // Attack went as expected
    {
      if (mod >= 10)
      {
        att.set(0);
        def.set(15); // 15% effective
      }
      else if (mod < 10 && mod >= 0)
      {
        att.set(0);
        def.set(10); // 10% effective
      }
      else if (mod < 0 && mod > -10)
      {
        att.set(0); // no damage
        def.set(0);
      }
      else
      {
        if (outnumber)
          att.set(0);
        else
         att.set(1); // // 1% damage
        def.set(1); // 1% damage
      }
      
      return;
    }
    else if (roll == 11) // Attack went slightly better than planned
    {
      if (mod >= 10)
      {
        att.set(0);
        def.set(15); // 15% effective
      }
      else if (mod < 10 && mod >= 0)
      {
        att.set(0);
        def.set(10); // 10% effective
      }
      else
      {
        att.set(0); // no damage
        def.set(0);
      }
      
      return;
    }
    else if (roll == 12)
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(17); // 17% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(12); // 12% effective
      }
      else
      {
        att.set(0); // no damage
        def.set(0);
      }
      
      return;
    }
    else if (roll == 13) // Attack went a little better than planned
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(18); // 18% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(15); // 15% effective
      }
      else
      {
        att.set(0); // no damage
        def.set(0); 
      }
      
      return;
    }
    else if (roll == 14)
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(19); // 19% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(15); // 15% effective
      }
      else
      {
        att.set(0); // no damage
        def.set(0);
      }
      
      return;
    }
    else if (roll == 15) // Attack went better than planned
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(20); // 20% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(15); // 15% effective
      }
      else
      {
        att.set(0); // no damage
        def.set(0);
      }
      
      return;
    }
    else if (roll == 16) //
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(22); // 22% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(17); // 17% effective
      }
      else if (mod < 0 && mod > -15)
      {
        att.set(0); 
        def.set(1); // 2% damage
      }
      else
      {
        att.set(0); 
        def.set(0); // No damage
      }
      
      return;
    }
    else if (roll == 17) //
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(25); // 25% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(20); // 20% effective
      }
      else if (mod < 0 && mod > -15)
      {
        att.set(0); 
        def.set(2); // 2% damage
      }
      else
      {
        att.set(0); 
        def.set(1); // No damage
      }
      
      return;
    }
    else if (roll == 18) // Attack did much more significant damage than planned
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(25); // 25% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(20); // 20% effective
      }
      else if (mod < 0 && mod > -15)
      {
        att.set(0); 
        def.set(6); // 2% damage
      }
      else
      {
        att.set(0); 
        def.set(3); // No damage
      }
      
      return;
    }
    else if (roll == 19) //
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(30); // 30% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(25); // 25% effective
      }
      else if (mod < 0 && mod > -15)
      {
        att.set(0); 
        def.set(8); // 3% damage
      }
      else
      {
        att.set(0); 
        def.set(4); // No damage
      }
      
      return;
    }
    else if (roll == 20) // Attack was superior
    {
      if (mod >= 15)
      {
        att.set(0);
        def.set(35); // 35% effective
      }
      else if (mod < 15 && mod >= 0)
      {
        att.set(0);
        def.set(28); // 25% effective
      }
      else if (mod < 0 && mod > -15)
      {
        att.set(0); 
        def.set(7); // 3% damage
      }
      else
      {
        att.set(0); 
        def.set(3); // No damage
      }
      
      return;
    }
    else
      System.out.println("Unknown roll amount");
  }
}
