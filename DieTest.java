import java.util.*;
import java.util.concurrent.atomic.*;

import Dice.TwentySidedDie;

//! Class to test the TwentySideDie Class
public class DieTest {

  //! Program Execution point
  public static void main(String[] args)
  {
    TwentySidedDie t = new TwentySidedDie();
    
    int roll = -1;
    
    AtomicInteger a = new AtomicInteger(0);
    AtomicInteger d = new AtomicInteger(0);
    
    //t.GetAttackScale(20, -15, a, d);
    
    for (int i = -27; i < 29; i++)
    {
      roll = t.Roll();
      
      System.out.println("Roll is " + roll + "; mod is " + i);
      
      t.GetAttackScale(roll, i, a, d);
      
      System.out.println("Attacker percentage is " + a.get() + "%");
      System.out.println("Defender percentage is " + d.get() + "%");
    }
  }

} //! End class
