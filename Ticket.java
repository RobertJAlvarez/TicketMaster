import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Ticket class save information about multiple transactions from a single customer for
 * multiple seats for different events, if wanted.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Ticket {
  private int purchaseID;
  private Event event;
  private Venue venue;
  private ArrayList<Seat> seatsPurchased;
  private Customer customer;
  private String purchaseTime;
  private float totalCost;
  private static final int MAXNUMBEROFSEATS = 6;

  /**
   * Constructor that initialize seatsPurchased, save customer, and generate purchase id for the ticket.
   * 
   * @param event - 
   * @param venue - 
   * @param customer - 
   */
  public Ticket(Event event, Venue venue, Customer customer) {
    this.event = event;
    this.venue = venue;
    this.seatsPurchased = new ArrayList<>();
    this.customer = customer;
    this.totalCost = (float) 0.0;
  }

  /**
   * Constructor with no parameters. Call Constructor with Customer parameter of null.
   */
  public Ticket() {
    this(null,null,null);
  }

  //Getters
  public int getPurchaseID() {
    return purchaseID;
  }

  public Event getEvent() {
    return this.event;
  }

  public Venue getVenue() {
    return this.venue;
  }

  public List<Seat> getSeatsPurchased() {
    return seatsPurchased;
  }

  public Customer getCustomer() {
    return customer;
  }

  public String getPurchaseTime() {
    return purchaseTime;
  }

  public float getTotalCost() {
    return totalCost;
  }

  public static int getMaxNumberOfSeats() {
    return MAXNUMBEROFSEATS;
  }

  //Setters
  public void setPurchaseID() {
    this.purchaseID = generatePurchaseID();
  }

  public void setPurchaseID(int purchaseID) {
    this.purchaseID = purchaseID;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public void setVenue(Venue venue) {
    this.venue = venue;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public void setPurchaseTime(String purchaseTime) {
    this.purchaseTime = purchaseTime;
  }

  public void setPurchaseTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
    purchaseTime = dtf.format(LocalDateTime.now());
  }

  public void setTotalCost(float totalCost) {
    this.totalCost = totalCost;
  }

  //Methods
  public void addPurchase(Seat purchase) {
    seatsPurchased.add(purchase);
  }

  public int getNumberOfSeatsPurchases() {
    return seatsPurchased.size();
  }

  private int generatePurchaseID() {
    return this.hashCode();
  }

  /**
   * Use the ticket information to save a string in records with the information of the purchases.
   */
  public String getRecord() {
    StringBuilder bld = new StringBuilder();

    bld.append("Event: " + event.getName() + " | Venue: " + venue.getName() + " | Customer: " + getCustomer().getUsername() +
      " | Ticket ID:" + getPurchaseID() + " | Total cost: " + getTotalCost() + " | Purchase time: " + getPurchaseTime() + "\n");

    for (Seat seat : getSeatsPurchased()) {
      bld.append("\t" + "Seat type: " + seat.getSeatType() + " | Seat price: " + seat.getPrice() + "\n");
    }

    return bld.toString();
  }

  /**
   * @return String with the information summary of the ticket.
   */
  public String getSummary() {
    StringBuilder bld = new StringBuilder();

    bld.append("Confirmation Number: " + getPurchaseID() + " | Event type: " + event.getClass().getName() + " | Event Name: " + event.getName() +
      " | Event Date: " + event.getDate() + " | Number of seats: " + getNumberOfSeatsPurchases() + " | Total price: " + getTotalCost() + "\n");

    return bld.toString();
  }

  /**
   * Write the header for all the necessary information to replicate the Ticket only by reading the file.
   * Last character appended to file is a new line character.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public static void writeCSVHeader(FileWriter writer) {
    try {
      writer.append("Purchase ID,Event ID,Customer ID,Purchase Time,Total Cost,");
      String[] seatsHeader = {"Seat Type","Price"};
      for (int i = 1; i <= getMaxNumberOfSeats(); i++) {
        for (int j = 0; j < seatsHeader.length; j++) {
          writer.append(seatsHeader[j] + " " + i + (((i == getMaxNumberOfSeats()) && (j == seatsHeader.length-1)) ? "\n" : ",") );
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fill all the columns made by Ticket.writeCSVHeader() with the event information.
   * Last character added to file is an new line character.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public void writeContentToCSV(FileWriter writer) {
    try {
      writer.append(getPurchaseID() + ",");
      writer.append(getEvent().getEventID() + ",");
      writer.append(getCustomer().getCustomerID() + ",");
      writer.append(getPurchaseTime() + ",");
      writer.append(getTotalCost() + ",");
      int i = 0;
      for (Seat seat : getSeatsPurchased()) {
        writer.append(seat.getSeatType() + ",");
        writer.append(seat.getPrice() + ((i++ == getMaxNumberOfSeats()-1) ? "\n" : ","));
      }
      while (i < getMaxNumberOfSeats()-1) {
        writer.append(",,,");
        i++;
      }
      if (i < getMaxNumberOfSeats()) {
        writer.append(",,\n");
      }
    } catch (IOException e) {
      e.printStackTrace();  //Writing the file was unsuccessful
    }
  }
}
