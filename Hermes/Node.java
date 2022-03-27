package Hermes;

import Hermes.Resources.Status;

//! Class declaration for a Node, which serves as a container
//! for a Status associated with an action stored as text
public class Node {

  private Status status_m;
  private String text_m;
  
  public Node(Status s, String t)
  {
    status_m = s;
    text_m = t;
  }
  
  public String GetText()
  {
    return text_m;
  }
  
  public Status GetStatus()
  {
    return status_m;
  }

} // class
