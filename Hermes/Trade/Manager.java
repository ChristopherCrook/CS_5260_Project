package Hermes.Trade;

import Hermes.Country;
import Hermes.Resources.Resource;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.Math.*;

//! Class declaration for the Trade Manager
public class Manager implements Runnable {

  //! Private Variables
  ArrayBlockingQueue<Entry> queue_m;
  
  //! Constructor
  public Manager()
  {
  }
  
  //! Method to set the blocking queue
  public void SetQueue(ArrayBlockingQueue<Entry> queue)
  {
    queue_m = queue;
  }
  
  //! Overloaded run() method inherited from Runnable
  public void run()
  {
    // Begin run loop
    while (!Thread.currentThread().isInterrupted())
    {
      // Check that the queue isn't empty
      if (queue_m.isEmpty())
      {
        try { // try to sleep the thread
          Thread.sleep(100);
        }
        catch (InterruptedException ie) {
         // do nothing
        }
        finally
        {
          continue;
        }
      } // end if isEmpty()
        
      Entry current;
      
      // get the head
      try {
        current = queue_m.take();
      }
      catch (InterruptedException ei)
      {
        continue;
      }
      
      // make sure this one hasn't been handled
      if (current.GetNoMatch() == true || current.GetSuccess() == true)
        continue;
        
      // Check and make sure that this isn't a surplus
      if (current.IsSurplus())
        continue;
      
      // Create a placeholder for the best offer
      Entry bestOffer = null;
      
      AtomicReference<Country> req_country;
      AtomicReference<Country> off_country;
      
      boolean success = false;
      
      // Go through the list in the queue and find matches
      for (Entry entry : queue_m)
      {
        if (entry.GetOffer().GetName().equals(current.GetNeed().GetName()) &&
            entry.GetNeed().GetName().equals(current.GetOffer().GetName()))
        {
          if (entry.GetOffer().GetAmount() >= current.GetNeed().GetAmount())
          {
            bestOffer = entry;
            
            if (entry.GetNeed().GetAmount() <= current.GetOffer().GetAmount())
            { // This is perfect case scenario
              entry.SetTaken(current.GetOffer().GetAmount());
              current.SetGiven(current.GetOffer().GetAmount());
              
              entry.SetGiven(entry.GetNeed().GetAmount());
              current.SetTaken(entry.GetNeed().GetAmount());
              
              success = true;
              break;
            }
          } // end if
          else
          {
            if (bestOffer == null) // check and see if we have a best offer so far
              bestOffer = entry;
            else if (entry.GetOffer().GetAmount() > bestOffer.GetOffer().GetAmount())
              bestOffer = entry;
          } // end else
        }  // if
      } // end for
      
      if (success == true && bestOffer != null)
      {
        boolean check = current.SetSuccessToTrue();
        check = bestOffer.SetSuccessToTrue();
        queue_m.remove(current);
        queue_m.remove(bestOffer);
        
        continue;
      }
      
      // If we get here, we don't have a perfect match. We need to 
      // See if a trade is still possible.
      long current_need = current.GetNeed().GetAmount();
      long current_offer = current.GetOffer().GetAmount();
      
      long bestOffer_need = bestOffer.GetNeed().GetAmount();
      long bestOffer_offer = bestOffer.GetOffer().GetAmount();
      
      // We know that the offer is less than the need
      if (bestOffer.GetNeed().GetAmount() < current.GetOffer().GetAmount())
      {
        bestOffer.SetTaken(bestOffer.GetOffer().GetAmount());
        current.SetGiven(bestOffer.GetOffer().GetAmount());
              
        bestOffer.SetGiven(current.GetOffer().GetAmount());
        current.SetTaken(current.GetOffer().GetAmount());
        
        boolean ifcheck = current.SetSuccessToTrue();
        ifcheck = bestOffer.SetSuccessToTrue();
        queue_m.remove(current);
        queue_m.remove(bestOffer);
      }
      else
      {
        double multiplier = (double) bestOffer.GetNeed().GetAmount()/
                            (double) bestOffer.GetOffer().GetAmount();
                            
        long final_offer = (long)Math.floor(
          (double)current.GetOffer().GetAmount() / multiplier
        );
        
        bestOffer.SetTaken(current.GetOffer().GetAmount());
        current.SetGiven(current.GetOffer().GetAmount());
        
        bestOffer.SetGiven(final_offer);
        current.SetTaken(final_offer);
        
        boolean elsecheck = current.SetSuccessToTrue();
        elsecheck = bestOffer.SetSuccessToTrue();
        queue_m.remove(current);
        queue_m.remove(bestOffer);
      } // else          
    } // end while
  } // end run()
}
