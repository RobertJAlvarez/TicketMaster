/**
 * Special is a class that extends the Event class containing, event id, name, date, time,
 * prices, seats, availability, venue and tickets sold.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Special extends Event {
  /**
   * Constructor to create an Event with all parameters. It invokes the super constructor.
   * 
   * @param eventID - int with event id
   * @param name - String for event name
   * @param date - String for event date
   * @param time - String for event time
   */
  public Special(int eventID, String name, String date, String time, boolean fireworksPlanned, int fireworksCost) {
    super(eventID, name, date, time, fireworksPlanned, fireworksCost);
  }

  /**
   * Constructor to create an Event with no parameters. It invokes the super constructor.
   */
  public Special() {
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
