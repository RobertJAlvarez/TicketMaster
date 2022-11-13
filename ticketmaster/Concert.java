package ticketmaster;

import java.util.GregorianCalendar;

/**
 * Concert is a class that extends the Event class containing, event id, name, date, time,
 * prices, seats, availability, venue and tickets sold.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Concert extends Event {
  /**
   * Constructor to create an Event by invoking the super constructor
   * 
   * @param eventID
   * @param name - String for event name
   * @param date - GregorianCalendar for event date and time
   */
  public Concert(int eventID, String name, GregorianCalendar date, boolean fireworksPlanned, int fireworksCost) {
    super(eventID, name, date, fireworksPlanned, fireworksCost);
  }

  /**
   * Constructor without parameters. Do nothing.
   */
  public Concert() {}

  //Methods
  /**
   * Print vital event information and its type.
   * 
   * @see Event#printEventInfo()
   */
  @Override
  public void printEventInfo() {
    super.printEventInfo();
    System.out.println(" - Event Type: " + getClass().getSimpleName());
  }
}
