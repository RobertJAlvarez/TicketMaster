/**
 * Stadium is a class that extends the Venue class to be use in the Event class 'to have a place to hold an event.'
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Stadium extends Venue {
  /**
   * Constructor to create a venue by invoking the super constructor with all parameters.
   * 
   * @param name - String name
   * @param numSeatsUnavailable - int with the number of seat unavailable
   * @param capacity - int with the maximum capacity
   * @param cost - int with for the cost to rent the venue
   */
  public Stadium(String name, int numSeatsUnavailable, int capacity, int cost) {
    super(name, numSeatsUnavailable, capacity, cost);
  }

  /**
   * Constructor without parameters. Do nothing.
   */
  public Stadium() {}
}
