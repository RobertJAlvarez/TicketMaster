package ticketmaster;

import java.util.GregorianCalendar;

/**
 * Sport is a class that extends the Event class containing, event id, name, date, time,
 * prices, seats, availability, venue and tickets sold.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Sport extends Event {
  /**
   * Constructor to create an Event with all parameters. It invokes the super constructor.
   * 
   * @param eventID - int with event id
   * @param name - String for event name
   * @param date - GregorianCalendar for event date and time
   */
  public Sport(int eventID, String name, GregorianCalendar date, boolean fireworksPlanned, int fireworksCost) {
    super(eventID, name, date, fireworksPlanned, fireworksCost);
  }

  /**
   * Constructor to create an Event with no parameters. It invokes the super constructor.
   */
  public Sport() {
    super();
  }

  //Methods
  /**
   * Print vital event information and its type.
   * 
   * @see Event#printEventInfo()
   */
  @Override
  public void printEventInfo() {
    super.printEventInfo();
    System.out.println(" - Event Type: " + getClass().getName());
  }
}
