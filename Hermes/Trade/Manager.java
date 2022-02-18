package Hermes.Trade;

import Hermes.Country;
import Hermes.Resources.Resource;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

//! Class declaration for the Trade Manager
public class Manager {

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
}
