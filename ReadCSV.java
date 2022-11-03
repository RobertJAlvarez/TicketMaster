import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * This class provide function to read csv files and populate the HashMaps on the Database object
 * base on whatever the file suppose to fill,
 * 
 * @author Robert J Alvarez
 * @date September 13th, 2022
 */
public class ReadCSV {
  public static final String EVENTIDHEADER = "Event ID";

  private ReadCSV() {
    //This constructor is empty, class is use only to read files given the database as parameter
  }

  /**
   * This function read from the file provided and populate the respective HashMap in Database.
   * 
   * @param filename - String with the csv file containing the information
   * @param whatToPopulate - String with the class name saying what objects would be read.
   */
  public static void populateDatabase(String filename, String whatToPopulate) {
    Scanner fileScnr = prepareFileToBeRead(filename);
    int populateOption;

    //Choose that to populate
    if (whatToPopulate.equalsIgnoreCase(Event.class.getName())) {  //Events
      populateOption = 0;
    } else if (whatToPopulate.equalsIgnoreCase(Customer.class.getName())) { //Customers
      populateOption = 1;
    } else if (whatToPopulate.equalsIgnoreCase(Ticket.class.getName())) {   //Tickets
      populateOption = 2;
    } else if (whatToPopulate.equalsIgnoreCase("AutoPurchase")) { //Auto purchases
      populateOption = 3;
    } else {  //No valid option to populate was given
      System.out.println(whatToPopulate + " is not a valid option on what to populate in ReadCSV.populateDatabase().");
      return;
    }

    //Check that fileScnr was successfully open
    if (fileScnr == null) {
      return;
    }

    LinkedHashMap<String, String> lineInfo = new LinkedHashMap<>();
    String[] entries;
    int i;

    //Read header and use what is read as keys
    entries = ( fileScnr.nextLine() ).split(",");
    for (String columnName : entries)
      lineInfo.put(columnName, "");

    //Read the rest of the file provided to make events and venues
    while ( fileScnr.hasNextLine() ) {
      entries = ( fileScnr.nextLine() ).split(",");

      //Save line information read
      i = 0;
      for (String key : lineInfo.keySet())
        lineInfo.put(key, (i < entries.length ? entries[i++] : ""));  //Map key to entries[i++] if i < entries.length otherwise to ""

      //Save event and venue information
      switch (populateOption) {
        case (0):
          addEvent(lineInfo);
          break;
        case (1):
          addCustomer(lineInfo);
          break;
        case (2):
          addTicket(lineInfo);
          break;
        case (3):
          addPurchase(lineInfo);
          break;
        default:
          System.err.println(populateOption + " is not accounted for on the switch statement in ReadCSV.populateDatabase().");
      }
    }
    fileScnr.close();
  }

  /**
   * 
   * 
   * @param entries
   */
  private static void addPurchase(Map<String,String> entries) {
    Customer customer = Database.getCustomer(entries.get("First") + entries.get("Last"));
    int eventID = Integer.parseInt(entries.get(EVENTIDHEADER));
    Event event = Database.getEvent(eventID);
    int nSeats = Integer.parseInt(entries.get("Ticket Quantity"));
    Ticket ticket = customer.buyNSeats(event, entries.get("Ticket Type"), nSeats);
    Database.addTicket(ticket);
  }

  /**
   * 
   * 
   * @param entries
   */
  private static void addEvent(Map<String,String> entries) {
    Event event = Admin.makeEvent(entries);
    if (event != null) {
      //Update largest event ID if needed
      if (event.getEventID() > Database.getLargestEventID()) {
        Database.setLargestEventID(event.getEventID());
      }
      //Make the venue and give it to the event
      event.setVenue(Admin.makeVenue(entries));
      //Make the seats for the event
      makeSeats(entries, event);
      Database.addEvent(event);
    }
  }

  /**
   * 
   * 
   * @param entries - Map<String,String> with the header of the column names as keys and the line information just read as values
   */
  private static void addCustomer(Map<String,String> entries) {
    int id = Integer.parseInt(entries.get("ID"));
    float moneyAvailable = Float.parseFloat(entries.get("Money Available"));
    boolean hasMembership = entries.get("TicketMiner Membership").equalsIgnoreCase("TRUE");
    Customer customer = new Customer(id, entries.get("First Name"), entries.get("Last Name"), moneyAvailable, hasMembership, entries.get("Username"), entries.get("Password"));
    Database.addCustomers(customer);
  }

  /**
   * 
   * 
   * @param entries - Map<String,String> with the header of the column names as keys and the line information just read as values
   * @return
   */
  private static void addTicket(Map<String,String> entries) {
    int id = Integer.parseInt(entries.get("Purchase ID"));
    Event event = Database.getEvent(Integer.parseInt(entries.get(EVENTIDHEADER)));
    Customer customer = Database.getCustomer(Integer.parseInt(entries.get("Customer ID")));
    Ticket ticket = new Ticket(event, event.getVenue(), customer);
    String seatType;
    float seatPrice;

    ticket.setPurchaseID(id);
    ticket.setPurchaseTime(entries.get("Purchase Time"));
    ticket.setTotalCost(Float.parseFloat(entries.get("Total Cost")));

    for (int i = 1; i <= Ticket.getMaxNumberOfSeats(); i++) {
      seatType = entries.get("Seat Type " + i);
      if (seatType.length() == 0) {
        break;
      }
      seatPrice = Float.parseFloat(entries.get("Price " + i));
      ticket.addPurchase(new Seat(seatType, seatPrice));
    }

    Database.addTicket(ticket);
  }

  /**
   * Try to open the file. If successful we return it, otherwise we print an error message and return null.
   * 
   * @param filename - String with the csv file name
   * @return Scanner containing the file opened to be read if successful or null otherwise
   */
  private static Scanner prepareFileToBeRead(String filename) {
    Scanner fileScnr = null;

    try {
      fileScnr = new Scanner(new File(filename));
    } catch (FileNotFoundException | NullPointerException e) {
      System.err.println(e.toString());
      Log.logWrite(Level.WARNING, e.toString());
    }
    return fileScnr;
  }

  /**
   * Use entries to fill the seats on the event base on all the seat types available.
   * 
   * @param entries - Map<String,String> with the header of the column names as keys and the line information just read as values.
   * @param event - Event type pass to fill its seatsInfo attribute using entries.
   */
  public static void makeSeats(Map<String,String> entries, Event event) {
    String tempPrice;
    int tempSeats;

    //Set number of available seat with their respective prices
    for (String seatType : Database.getSeatTypes()) {
      tempPrice = entries.get(seatType.concat(" Price"));
      if (entries.containsKey(seatType.concat(" Pct"))) {
        tempSeats = Integer.parseInt(entries.get(seatType.concat(" Pct")));
        event.makeNSeatsByPct(seatType, tempPrice, tempSeats);
      } else if (entries.containsKey(seatType.concat(" Num"))) {
        tempSeats = Integer.parseInt(entries.get(seatType.concat(" Num")));
        event.makeNSeatsByNum(seatType, tempPrice, tempSeats);
      } else {
        System.err.println("csv file don't contain number nor percentage of " + seatType + " seat type.");
      }
    }

    //Set number of reserved seats
    if (entries.containsKey("Reserved Extra Pct")) {
      tempSeats = Integer.parseInt(entries.get("Reserved Extra Pct"));
      event.setHowManyReservedSeats(tempSeats*event.getVenue().getCapacity()/100);
    } else if (entries.containsKey("Reserved Extra Num")) {
      tempSeats = Integer.parseInt(entries.get("Reserved Extra Num"));
      event.setHowManyReservedSeats(tempSeats*event.getVenue().getCapacity());
    }
  }
}
