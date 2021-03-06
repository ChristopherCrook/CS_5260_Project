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
  private static ArrayBlockingQueue<Entry> queue_m;
  private static volatile boolean shutdown_m;

  private volatile int count_m;
  
  //! Constructor
  public Manager()
  {
    shutdown_m = false;
    count_m = 0;
  }
  
  //! Method to set the blocking queue
  public static void SetQueue(ArrayBlockingQueue<Entry> queue)
  {
    queue_m = queue;
  }
  
  public static void Shutdown()
  {
    shutdown_m = true;
  }
  
  //! Overloaded run() method inherited from Runnable
  public void run() 
  {
      // Begin run loop
      while (!(shutdown_m))
      {
        // Check that the queue isn't empty
        if (queue_m.isEmpty())
        {
          try { // try to sleep the thread if it is empty
            Thread.sleep(100);
            count_m++;
          }
          catch (InterruptedException ie) {
           // do nothing
          }
          finally
          {
            if (count_m == 100)
            {
              System.out.println("Trade Manager timing out");
              return;
            }

            continue;
          }
        } // end if isEmpty()
        
        Entry current;

        // reset the count
        count_m = 0;
      
        // Try to get the head
        try {
          current = queue_m.take();
        }
        catch (InterruptedException ei)
        {
          continue;
        } // end catch

        // make sure this one hasn't been handled, which shouldn't happen
        if (current.GetNoMatch() == true || current.GetSuccess() == true)
          continue;
        
        // Check and make sure that this isn't a surplus
        // If it is a surplus, it will be handled when a need arises for it
        if (current.IsSurplus())
        {
          try { // Place it at the tail
            queue_m.put(current);
          }
          catch (InterruptedException ie)
          { }
          finally {
            continue;
          }
        } // end if
        
        // Create a placeholder for the best offer encountered
        Entry bestOffer = null;
      
        AtomicReference<Country> req_country;
        AtomicReference<Country> off_country;
      
        boolean success = false;
      
        // Go through the list in the queue and find matches
        for (Entry entry : queue_m)
        {
          if (entry.GetCountry().get().GetName().equals(current.GetCountry().get().GetName()))
            continue; // don't look at entries from the same country
            
          if (entry.GetOffer().GetName().equals(current.GetNeed().GetName()) &&
              entry.GetNeed().GetName().equals(current.GetOffer().GetName()))
          {
            if (entry.GetOffer().GetAmount() >= current.GetNeed().GetAmount())
            {
              bestOffer = entry;
              
              if (entry.GetNeed().GetAmount() <= current.GetOffer().GetAmount())
              { // This is perfect case scenario
                entry.SetTaken(entry.GetOffer().GetAmount());
                current.SetGiven(entry.GetOffer().GetAmount());
              
                entry.SetGiven(current.GetOffer().GetAmount());
                current.SetTaken(current.GetOffer().GetAmount());
                
                success = true;
                break;
              }
            } // end if
            else
            {
              if (bestOffer == null) // check and see if we have a best offer so far
              {
                bestOffer = entry;
              }
              else if (entry.GetOffer().GetAmount() > bestOffer.GetOffer().GetAmount())
              {
                bestOffer = entry;
              }
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
      
        if (bestOffer == null)
        {
          // Added this for troubleshooting. If no match, we attempt
          // to wait for a better condition
          try { // Place it at the tail 
            queue_m.put(current);
          }
          catch (InterruptedException ie)
          { }
          finally {
            continue;
          }
        }
      
        // If we get here, we don't have a perfect match. We need to 
        // See if a trade is still possible.
        // 
        // If this statement is true, we know that the current offer is 
        // less than the need of the current trade country.
        // We just take what the country is offering and give what it needs
        if (bestOffer.GetNeed().GetAmount() < current.GetOffer().GetAmount())
        {
          bestOffer.SetTaken(bestOffer.GetOffer().GetAmount());
          current.SetGiven(bestOffer.GetOffer().GetAmount());
              
          bestOffer.SetGiven(bestOffer.GetNeed().GetAmount());
          current.SetTaken(bestOffer.GetNeed().GetAmount());
          
          boolean ifcheck = current.SetSuccessToTrue();
          ifcheck = bestOffer.SetSuccessToTrue();
          queue_m.remove(current);
          queue_m.remove(bestOffer);
        }
        else
        {
          // If we get here, the need of the trading country is more than
          // we have to give, and they have less than what we need.
          // We need to find out how much each offered resource costs.
          double multiplier = (double) bestOffer.GetOffer().GetAmount()/
                              (double) bestOffer.GetNeed().GetAmount();
                            
          long final_offer = (long)Math.floor(
            (double)current.GetOffer().GetAmount() * multiplier
          );
          
          // Error check
          if (final_offer < 1)
          {
            try { // Place it at the tail 
            queue_m.put(current);
            }
            catch (InterruptedException ie)
            { }
            finally {
              continue;
            }
          } // end if

          bestOffer.SetTaken(final_offer);
          current.SetGiven(final_offer);
          
          bestOffer.SetGiven(current.GetOffer().GetAmount());
          current.SetTaken(current.GetOffer().GetAmount());
          
          boolean elsecheck = current.SetSuccessToTrue();
          elsecheck = bestOffer.SetSuccessToTrue();
          queue_m.remove(current);
          queue_m.remove(bestOffer);
        } // else
        
        // Troubleshooting print statements
        //System.out.println(current.GetName() + " needed " + current.GetNeed().GetAmount() + " " + current.GetNeed().GetName());
        //System.out.println("Traded " + current.GetTaken() + " " + current.GetOffer().GetName());
        //System.out.println("for " + current.GetGiven() + " " + current.GetNeed().GetName());
      } // end while

  } // end run()
}
