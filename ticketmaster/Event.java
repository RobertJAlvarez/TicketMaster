package ticketmaster;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Sport is a type of event with attributes of name, date, time, and prices. This class also have setters and getters for all attributes
 * and a printInfo similar to toString.
 * It also have an special option to hold fireworks during the event.
 * 
 * @author Robert J Alvarez
 * @date September 16th, 2022
 */
public abstract class Event {
  private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

  private int eventID;
  private String name;
  private GregorianCalendar date;
  private String state;
  private int reservedSeats;
  private boolean fireworksPlanned;
  private int fireworksCost;
  private LinkedHashMap<String,String[]> seatsInfo; //key = seatType | value[0] = num of seats available, value[1] = price for each seat
  private HashMap<Integer,Ticket> ticketsSold;      //key = ticketID | value = Ticket that match the id
  private Venue venue;
  private float totalDiscounted;
  private float[] salesTotals = new float[] {(float) 0.0, (float) 0.0, (float) 0.0, (float) 0.0}; //0: taxes, 1: service, 2: convenience, 3: charity

  /**
  * Constructor with all parameters.
  * 
  * @param eventID - int with ID number identifier of the event
  * @param name - String for name attribute
  * @param date - GregorianCalendar for date attribute which contains date and time information of the event
  * @param fireworksPlanned - boolean to see if fireworks are planned during the event
  * @param fireworksCost - int with cost of fireworks if they are planned, price should be 0.0 otherwise.
  */
  protected Event(int eventID, String name, GregorianCalendar date, boolean fireworksPlanned, int  fireworksCost) {
    this.eventID = eventID;
    this.name = name;
    this.date = date;
    //TODO: Currently all the event are in Texas but we shouldn't assume that. Unfortunately, this wont be change in future PAs
    this.state = "Texas";
    this.fireworksPlanned = fireworksPlanned;
    this.fireworksCost = fireworksCost;
    this.seatsInfo = new LinkedHashMap<>();
    this.ticketsSold = new HashMap<>();
    this.totalDiscounted = (float) 0.0;
  }

  /**
  * Constructor with no parameters. Initialize seatsInfo and ticketsSold.
  */
  protected Event() {
    this.seatsInfo = new LinkedHashMap<>();
    this.ticketsSold = new HashMap<>();
  }

  //Getters
  public int getEventID() {
    return eventID;
  }

  public String getName() {
    return name;
  }

  public String getDate() {
    return sdf.format(date.getTime());
  }

  public String getState() {
    return state;
  }

  public int getHowManyReservedSeats() {
    return reservedSeats;
  }

  public boolean areFireworksPlanned() {
    return fireworksPlanned;
  }

  public int getFireworksCost() {
    return fireworksCost;
  }

  private String[] getSeatTypeInfo(String type) {
    if (seatsInfo.containsKey(type))
      return seatsInfo.get(type);
    return new String[0];
  }

  public float getSeatPrice(String type) {
    String[] temp = getSeatTypeInfo(type);
    return temp.length >= 2 ? Float.parseFloat(temp[1]) : (float) -1.0;
  }

  public int getNumberOfSeatsAvailable(String type) {
    String[] temp = getSeatTypeInfo(type);
    return temp.length >= 2 ? Integer.parseInt(temp[0]) : -1;
  }

  public Collection<Ticket> getTickets() {
    return ticketsSold.values();
  }

  public Venue getVenue() {
    return venue;
  }

  public float getTotalDiscounted() {
    return totalDiscounted;
  }

  //Setters
  public void setName(String name) {
    this.name = name;
  }

  public void setDate(String date) {
    String[] tokens = date.split("/");
    this.date.set(Calendar.MONTH, Integer.parseInt(tokens[0]));
    this.date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tokens[1]));
    this.date.set(Calendar.YEAR, Integer.parseInt(tokens[2]));
  }

  public void setTime(String time) {
    String[] tokens = time.split("[ :]");
    int hour = Integer.parseInt(tokens[0]);
    if (tokens[2].contains("PM") && hour != 12) {
      hour += 12;
    }
    this.date.set(Calendar.HOUR_OF_DAY, hour);
    this.date.set(Calendar.MINUTE, Integer.parseInt(tokens[1]));
  }

  public void setFireworksPlanned(boolean fireworksPlanned) {
    this.fireworksPlanned = fireworksPlanned;
  }

  public void setFireworksCost(int fireworksCost) {
    this.fireworksCost = fireworksCost;
  }

  public void setHowManyReservedSeats(int reservedSeats) {
    this.reservedSeats = reservedSeats;
  }

  public void setSeatInfo(String type, int nSeats, float price) {
    setSeatPrice(type, price);
    setNumSeats(type, nSeats);
  }

  public void setTotalDiscounted(float totalDiscounted) {
    this.totalDiscounted = totalDiscounted;
  }

  /**
   * Save the seat price given its type. Whatever value it was before gets overwritten.
   * 
   * @param type - String with the type of seat, e.g., VIP, Gold, etc.
   * @param price - float with a float number representing the seat price.
   */
  public void setSeatPrice(String type, float price) {
    if (seatsInfo.containsKey(type)) {
      seatsInfo.get(type)[1] = String.valueOf(price);
    } else {
      System.err.println(type + " seat price couldn't be updated because type doesn't exist.");
      Log.logWrite(Level.WARNING, type + " seat price couldn't be updated because type doesn't exist.");
    }
  }

  /**
   * Save the number of seats given its type. Whatever value it was before gets overwritten.
   * 
   * @param type - String with the type of seat, e.g., VIP, Gold, etc.
   * @param nSeats - String with an int number representing the number of seats.
   */
  public void setNumSeats(String type, int nSeats) {
    if (seatsInfo.containsKey(type)) {
      seatsInfo.get(type)[0] = String.valueOf(nSeats);
    } else {
      System.out.println(type + " number of seats couldn't be updated because it doesn't exist.");
      Log.logWrite(Level.WARNING, type + " number of seats couldn't be updated because type doesn't exist.");
    }
  }

  public void setVenue(Venue venue) {
    this.venue = venue;
  }

  //Methods
  public void addTaxesCollected(float taxes) {
    salesTotals[0] += taxes;
  }

  public void addServiceFee(float serviceFee) {
    salesTotals[1] += serviceFee;
  }

  public void addConvenienceFee(float convenienceFee) {
    salesTotals[2] += convenienceFee;
  }

  public void addCharityFee(float charityFee) {
    salesTotals[3] += charityFee;
  }

  public void removeTicket(int ticketID) {
    ticketsSold.remove(ticketID);
  }

  /**
   * Given the seat type we would set the price and increase the number of seat available.
   * If the number of seats haven't been set we start at 0 and then add the number of seats.
   * Otherwise, we get the number stored and add the number of seats.
   * 
   * @param seatType - String with the type of seat, e.g., VIP, Gold.
   * @param price - String containing a number in float/double format with the price of each seat.
   * @param nSeats - int with the number of seats to be added.
   */
  public void makeNSeatsByNum(String seatType, String price, int nSeats) {
    int prevNumSeats;

    //Add key if it didn't exist before
    if (!seatsInfo.containsKey(seatType)) {
      seatsInfo.put(seatType, new String[] {"0", price});
    }

    //Get number of open seats before creating more
    prevNumSeats = Integer.parseInt(seatsInfo.get(seatType)[0]);

    //Set number of available seats
    setNumSeats(seatType, prevNumSeats+nSeats);
  }

  /**
   * call makeNSeatsByNum but we multiply the pct by the capacity of the venue to give make the nSeats parameter. 
   * 
   * @param seatType - String with the type of seat, e.g., VIP, Gold.
   * @param price - String containing a number in float/double format with the price of each seat.
   * @param nSeats - int with the number of seats to be added.
   */
  public void makeNSeatsByPct(String seatType, String price, int pct) {
    float p = pct;
    //Rescale from 0-100 to 0-1
    if (pct > 1)
      p /= 100;
    makeNSeatsByNum(seatType, price, (int) (p*getVenue().getCapacity()));
  }

  /**
   * Save ticket given into our ticket sold list.
   * 
   * @param ticket - Ticket that was made after purchase.
   */
  public void addTicket(Ticket ticket) {
    this.ticketsSold.put(ticket.getPurchaseID(), ticket);
  }

  /**
  * Print ID#, name, date, and time of the event
  */
  protected void printEventInfo() {
    System.out.printf("ID: %3s - Name: %-22s - Date: %-18s", eventID, name, getDate());
  }

  /**
   * Print all seat options with its cost and number of available seats for sell.
   * I'm assuming that all of the seats on the same category cost the same.
   */
  public void printSeatsOptions() {
    String price;
    String nSeats;
    int i = 0;

    for (Map.Entry<String,String[]> entry : seatsInfo.entrySet()) {
      //Don't print option if there are no more seats available
      if (entry.getValue()[0].equals("0"))
        continue;
      nSeats = entry.getValue()[0];
      price = entry.getValue()[1];
      System.out.printf("%d. (%s): $%s, %s available.%n", ++i, entry.getKey(), price, nSeats);
    }
  }

  /**
   * Use option to see what seat type was selected and return a string containing the seat type.
   * 
   * @param option - int with the option selected by the user.
   * @return String with the seat type base on option. Empty string if nothing match.
   */
  public String getSeatOptionN(int option) {
    String[] seatTypes = Database.getSeatTypes();

    if ((option > seatTypes.length) || (option < 1) ) return "";

    for (String seatType : seatTypes) {
      if (--option == 0) return seatType;
    }

    //This point should never be reach
    System.err.println("Something went wrong in getSeatOptionN()");
    return "";
  }

  /**
   * Write the header for all the necessary information to replicate the event only by reading the file.
   * Last character appended to file is a new line character.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public static void writeCSVHeader(FileWriter writer) {
    try {
      //Write header of csv file
      writer.append("Event ID,Event Type,Name,Date,Time,");
      for (String seatType : Database.getSeatTypes()) {
        writer.append(seatType + " Price," + seatType + " Num,");
      }
      writer.append("Reserved Extra Num,Fireworks Planned,Fireworks Cost,");
      Venue.writeCSVHeader(writer);
      writer.append(",");
      Statistics.writeCSVHeader(writer);
      writer.append("," + "Total Discounted");
      writer.append("\n");
    } catch (IOException e) {
      e.printStackTrace();  //Writing the file was unsuccessful
    }
  }

  /**
   * Fill all the columns made by Event.writeCSVHeader() with the event information.
   * Last character added to file is an new line character.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public void writeContentToCSV(FileWriter writer) {
    try {
      writer.append(getEventID() + ",");
      writer.append(getClass().getName() + ",");
      writer.append(getName() + ",");
      //write date and time using format MM/DD/YYY and hh:mm a
      String[] temp = getDate().split(" ");
      writer.append(temp[0] + ",");                 //MM/DD/YYY
      writer.append(temp[1] + " " + temp[2] + ","); //hh:mm and AM/PM
      //Print seat prices and number per type
      for (String key : seatsInfo.keySet()) {
        writer.append(getSeatPrice(key) + "," + getNumberOfSeatsAvailable(key) + ",");
      }
      writer.append(getHowManyReservedSeats() + ",");
      writer.append((areFireworksPlanned() ? "Yes" : "No") + ",");
      writer.append(getFireworksCost() + ",");
      venue.writeContentToCSV(writer);
      writer.append(",");
      Statistics.writeContentToCSV(writer, this);
      writer.append("," + totalDiscounted);
      writer.append("\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
