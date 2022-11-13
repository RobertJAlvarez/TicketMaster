package ticketmaster;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Ticket class save information about multiple transactions from a single customer for
 * multiple seats for different events, if wanted.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Ticket {
  private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

  private int purchaseID;
  private Event event;
  private Venue venue;
  private ArrayList<Seat> seatsPurchased;
  private Customer customer;
  private GregorianCalendar purchaseTime;
  private float[] salesTotals = new float[] {(float) 0.0, (float) 0.0, (float) -1.0, (float) 0.0}; //0: taxes, 1: service, 2: convenience, 3: charity
  private float subtotal;
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
    this.subtotal = (float) 0.0;
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
    return event;
  }

  public Venue getVenue() {
    return venue;
  }

  public List<Seat> getSeatsPurchased() {
    return seatsPurchased;
  }

  public Customer getCustomer() {
    return customer;
  }

  public String getPurchaseTime() {
    return sdf.format(purchaseTime.getTime());
  }

  public float getTaxesPay() {
    return salesTotals[0];
  }

  public float getServiceFeePay() {
    return salesTotals[1];
  }

  public float getConvenienceFeePay() {
    return salesTotals[2];
  }

  public float getCharityFeePay() {
    return salesTotals[3];
  }

  public float getSubtotal() {
    return subtotal;
  }

  public static int getMaxNumberOfSeats() {
    return MAXNUMBEROFSEATS;
  }

  public float getTotalCost() {
    float fees = (float) 0.0;
    for (float cost : salesTotals) {
      fees += cost;
    }
    return fees + subtotal;
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

  public void setPurchaseTime(GregorianCalendar purchaseTime) {
    this.purchaseTime = purchaseTime;
  }

  public void setPurchaseTime() {
    purchaseTime = new GregorianCalendar();
  }

  public void setTaxesPay(float taxes) {
    salesTotals[0] = taxes;
    event.addTaxesCollected(taxes);
    Database.addTaxesCollected(taxes);
  }

  public void setServiceFeePay(float serviceFee) {
    salesTotals[1] = serviceFee;
    event.addServiceFee(serviceFee);
    Database.addServiceFee(serviceFee);
  }

  public void setConvenienceFee(float convenienceFee) {
    salesTotals[2] = convenienceFee;
    event.addConvenienceFee(convenienceFee);
    Database.addConvenienceFee(convenienceFee);
  }

  public void setCharityFeePay(float charityFee) {
    salesTotals[3] = charityFee;
    event.addCharityFee(charityFee);
    Database.addCharityFee(charityFee);
  }

  public void setSubtotal(float subtotal) {
    this.subtotal = subtotal;
  }

  //Methods
  public void addTaxesCollected(float taxes) {
    salesTotals[0] += taxes;
    event.addTaxesCollected(taxes);
    Database.addTaxesCollected(taxes);
  }

  public void addServiceFee(float serviceFee) {
    salesTotals[1] += serviceFee;
    event.addServiceFee(serviceFee);
    Database.addServiceFee(serviceFee);
  }

  public void addCharityFee(float charityFee) {
    salesTotals[3] += charityFee;
    event.addCharityFee(charityFee);
    Database.addCharityFee(charityFee);
  }

  public void addToSubtotal(float cost) {
    subtotal += cost;
  }

  public void returnTaxes(float taxes) {
    salesTotals[0] -= taxes;
    event.returnTaxes(taxes);
    Database.returnTaxes(taxes);
  }

  public void returnServiceFee(float serviceFee) {
    salesTotals[1] += serviceFee;
    event.returnServiceFee(serviceFee);
    Database.returnServiceFee(serviceFee);
  }

  public void returnConvenienceFee() {
    event.returnConvenienceFee(salesTotals[2]);
    Database.returnConvenienceFee(salesTotals[2]);
    salesTotals[2] = ((float) -1.0);
  }

  public void returnCharityFee(float charityFee) {
    salesTotals[3] += charityFee;
    event.returnCharityFee(charityFee);
    Database.returnCharityFee(charityFee);
  }

  public void addPurchase(Seat purchase) {
    seatsPurchased.add(purchase);
  }

  public void removeSeat(Seat seat) {
    seatsPurchased.remove(seat);
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

    bld.append("Confirmation Number: " + getPurchaseID() + " | Event type: " + event.getClass().getSimpleName() + " | Event Name: " + event.getName() +
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
      writer.append("Purchase ID,Event ID,Customer ID,Purchase Time,Taxes pay,Service Fee,Convenience Fee,Charity Fee,Subtotal,Total,");
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
      writer.append(getTaxesPay() + ",");
      writer.append(getServiceFeePay() + ",");
      writer.append(getConvenienceFeePay() + ",");
      writer.append(getCharityFeePay() + ",");
      writer.append(getSubtotal() + ",");
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
