package ticketmaster;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Venue class model a place that can be use to host an event. Every venue have attributes like
 * name, number of seats unavailable, capacity, and cost.
 * 
 * @author Robert J Alvarez
 * @date September 13th, 2022
 */
public abstract class Venue {
  private String name;
  private int numSeatsUnavailable;
  private int capacity;
  private int cost;

  /**
   * Constructor with all parameters. Initialize all attributes. 
   * 
   * @param name - String for name attribute
   * @param numSeatsUnavailable - int for ???
   * @param capacity - int for capacity attribute
   * @param cost - int for cost attribute
   */
  protected Venue(String name, int numSeatsUnavailable, int capacity, int cost) {
    this.name = name;
    this.numSeatsUnavailable = numSeatsUnavailable;
    this.capacity = capacity;
    this.cost = cost;
  }

  /**
    * Constructor with no parameters. Do nothing.
    */
  protected Venue() {}

  //Getters
  public String getName() {
    return name;
  }

  public int getNumSeatsUnavailable() {
    return numSeatsUnavailable;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getCost() {
    return cost;
  }

  //Setters
  public void setName(String name) {
    this.name = name;
  }

  public void setNumSeatsUnavailable(int numSeatsUnavailable) {
    this.numSeatsUnavailable = numSeatsUnavailable;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  //methods
  /**
   * Print all the information of the venue. Name, # seats unavailable, capacity, and cost.
   */
  public void printInfo() {
    System.out.println("Venue information:");
    System.out.printf("Name: %s | Number of seat unavailable: %d  | Capacity: %d | cost: $%d.%n", name, numSeatsUnavailable, capacity, cost);
  }

  /**
   * Write the header for all the necessary information to replicate the venue only by reading the file.
   * Last character appended to file was not an special character.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public static void writeCSVHeader(FileWriter writer) {
    try {
      writer.append("Venue Name,Num Seats Unavailable,Venue Type,Capacity,Cost");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fill all the columns made by Venue.writeCSVHeader() with the venue information.
   * Last character appended to file was not an special character.
   * 
   * @param writer
   */
  public void writeContentToCSV(FileWriter writer) {
    try {
      writer.append(getName() + ",");
      writer.append(getNumSeatsUnavailable() + ",");
      writer.append(getClass().getSimpleName() + ",");
      writer.append(getCapacity() + ",");
      writer.append(getCost() + "");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
