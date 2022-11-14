package ticketmaster;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;

import ticketmaster.display.FileChooser;

/**
 * Database is an class to save all events, customer and tickets purchased. Events, venue (component of an event), and customers
 * are read from csv files. It also contains methods to print events, save tickets, and find and return specifics of each by the use of keys.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Database {
  private static HashMap<Integer, Event> events;
  private static HashMap<Integer, Customer> customers;
  private static HashMap<Integer, Ticket> ticketsPurchased;
  private static HashMap<String,Integer> mapNameToEventID;      //event.getName() as the key
  private static HashMap<String,Integer> mapNamesToCustomerID;  //customer.getFirstName() + customer.getLastName() as the key
  //seatTypes MUST BE ORDER IN DESCENDING IMPORTANCE!!!
  private static final String[] seatTypes = new String[] {"VIP","Gold","Silver","Bronze","General Admission"};
  private static final String NEWEVENTFILENAME = "NewEventList.csv";
  private static final String NEWCUSTOMERFILENAME = "NewCustomerList.csv";
  private static final String TICKETFILENAME = "NewTicketList.csv";
  private static boolean hasBeenPopulate = false;
  private static int largestEventID;
  private static float[] salesTotals = new float[] {(float) 0.0, (float) 0.0, (float) 0.0, (float) 0.0}; //0: taxes, 1: service, 2: convenience, 3: charity

  /**
   * Constructor with no parameters. It initiate events, customer, and tickets purchased.
   */
  private Database() {}

  //Getters
  public static String[] getSeatTypes() {
    return seatTypes;
  }

  public static Event getEvent(int eventID) {
    if (events.containsKey(eventID))
      return events.get(eventID);
    return null;
  }

  public static Event getEvent(String eventName) {
    if (mapNameToEventID.containsKey(eventName))
      return getEvent(mapNameToEventID.get(eventName));
    return null;
  }

  public static Customer getCustomer(int customerID) {
    if (customers.containsKey(customerID))
      return customers.get(customerID);
    return null;
  }

  public static Customer getCustomer(String username) {
    username = username.toLowerCase();
    if (mapNamesToCustomerID.containsKey(username))
      return getCustomer(mapNamesToCustomerID.get(username));
    return null;
  }

  public static Ticket getTicketPurchased(int ticketID) {
    if (ticketsPurchased.containsKey(ticketID))
      return ticketsPurchased.get(ticketID);
    return null;
  }

  public static int getNewEventID() {
    //Update largestEventID by 1, save the new value on itself, and return the new value
    return ++Database.largestEventID;
  }

  public static int getLargestEventID() {
    return Database.largestEventID;
  }

  public static float getTaxes() {
    return salesTotals[0];
  }

  public static float getServiceFee() {
    return salesTotals[1];
  }

  public static float getConvenienceFee() {
    return salesTotals[2];
  }

  public static float getCharityFee() {
    return salesTotals[3];
  }

  //Setters
  public static void setLargestEventID(int largestEventID) {
    Database.largestEventID = largestEventID;
  }

  //Methods
  public static void addTaxesCollected(float taxes) {
    salesTotals[0] += taxes;
  }

  public static void addServiceFee(float serviceFee) {
    salesTotals[1] += serviceFee;
  }

  public static void addConvenienceFee(float convenienceFee) {
    salesTotals[2] += convenienceFee;
  }

  public static void addCharityFee(float charityFee) {
    salesTotals[3] += charityFee;
  }

  public static void returnTaxes(float taxes) {
    salesTotals[0] -= taxes;
  }

  public static void returnServiceFee(float serviceFee) {
    salesTotals[1] -= serviceFee;
  }

  public static void returnConvenienceFee(float convenienceFee) {
    salesTotals[2] -= convenienceFee;
  }

  public static void returnCharityFee(float charityFee) {
    salesTotals[3] -= charityFee;
  }

  /**
   * Call populateDatabase from ReadCSV class with database, filename and the type of object to populate.
   */
  public static void populateDatabase() {
    if (hasBeenPopulate) {
      System.out.println("Information has been already populated into the Database, only admins can modify it after it has been load.");
      return;
    }

    events = new HashMap<>();
    customers = new HashMap<>();
    ticketsPurchased = new HashMap<>();
    mapNamesToCustomerID = new HashMap<>();
    mapNameToEventID = new HashMap<>();

    String filename = null;
    final String READ = "About to read ";

    //Populate Events information
    filename = FileChooser.chooseFile("to populate events");
    Log.logWrite(Level.FINE, READ + filename + " to populate Events and venues");
    ReadCSV.populateDatabase(filename, Event.class.getSimpleName());
    if (events.size() == 0) {
      System.out.println("File given don't contain the required minimum type of headers to create events.");
    }

    //Populate Customer information
    filename = FileChooser.chooseFile("to read customer information");
    Log.logWrite(Level.FINE, READ + filename + " to populate Customers.");
    ReadCSV.populateDatabase(filename, Customer.class.getSimpleName());
    if (customers.size() == 0) {
      System.out.println("File given don't contain the required minimum type of headers to create customers.");
    }

    //Populate Ticket information, would only read something if the software contain a file generated by a previous run
    filename = getNewerFile("", TICKETFILENAME);
    if (filename.length() > 0) {
      Log.logWrite(Level.FINE, READ + filename + " to populate Tickets");
      ReadCSV.populateDatabase(filename, Ticket.class.getName());
    }

    hasBeenPopulate = true;
  }

  /**
   * Given two filenames, it returns the one that was written the last.
   * 
   * @param originalFilename - String with filename provided that would always exist
   * @param newFilename - String with filename that is written at the end of every program execution
   * @return String with the latest filename
   */
  private static String getNewerFile(String originalFilename, String newFilename) {
    String filename = originalFilename;
    try {
      Path path = Paths.get(newFilename);
      if (Files.exists(path)) {
        filename = newFilename;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return filename;
  }

  /**
   * Add the event into the database.
   * 
   * @param event - Event to be added into the database.
   */
  public static void addEvent(Event event) {
    int id = event.getEventID();

    events.put(id, event);                      //Add event using the id as key
    mapNameToEventID.put(event.getName(), id);  //Map event name with its ID
  }

  /**
   * Remove the event from database.
   * 
   * @param event - Event to be remove from database.
   */
  public static void removeEvent(Event event) {
    int id = event.getEventID();

    events.remove(id);
    mapNameToEventID.remove(event.getName());
  }

  /**
   * Add the customer into the database.
   * 
   * @param customer - Customer to be added into the data base.
   */
  public static void addCustomers(Customer customer) {
    int id = customer.getCustomerID();

    customers.put(id, customer);
    //Use first name concatenated with the last name for the key
    mapNamesToCustomerID.put(customer.getUsername(), id);
  }

  /**
   * Given a ticket we add it into ticketsPurchased with the ticket ID as key and the ticket itself as value.
   * 
   * @param ticket - Ticket to be added into the database, event and customer lists.
   */
  public static void addTicket(Ticket ticket) {
    if (ticket == null) {
      return;
    }

    ticketsPurchased.put(ticket.getPurchaseID(), ticket);
    Event event = ticket.getEvent();
    Customer customer = ticket.getCustomer();

    //Add ticket to event, customer, and database if the purchase was successful. Otherwise, log unsuccessful purchase.
    if (ticket.getNumberOfSeatsPurchases() > 0) { //If seats were reserve
      event.addTicket(ticket);                    //Save ticket for the event
      customer.addTicketPurchased(ticket);        //Save ticket for the customer
      ticketsPurchased.put(ticket.getPurchaseID(), ticket); //Save ticket on data base
      Log.logWrite(Level.INFO, "Seats reserved:\n" + ticket.getRecord());
    } else {  //No purchases made
      Log.logWrite(Level.FINE,"No seats were purchased.");
    }
  }

  /**
   * Given a ticket, use its purchase ID, customer, and event attributes to removes it from the software database base on allFrom.
   * 
   * @param ticket - Ticket to be remove from the database.
   * @param allFrom - String[] with class names to remove the ticket from them.
   */
  public static void removeTicket(Ticket ticket, String[] allFrom) {
    int ticketID = ticket.getPurchaseID();

    for (String from : allFrom) {
      switch (from) {
        case ("Event"):
          ticket.getEvent().removeTicket(ticketID);
          break;
        case ("Customer"):
          ticket.getCustomer().removeTicket(ticketID);
          break;
        default:
      }
      ticketsPurchased.remove(ticketID);
    }
  }

  /**
   * Use oldName to delete the mapping between oldName and id and now map newName with id.
   * 
   * @param oldName - String name of event before it was change, therefore this is the previous key to event id.
   * @param newName - String name of event after it was change, therefore this is the new key to event id.
   * @param id - int event id.
   */
  public static void updateEventNameToIdMap(String oldName, String newName, int id) {
    mapNameToEventID.remove(oldName);
    mapNameToEventID.put(newName, id);
  }

  /**
   * Iterate over all events to print its ID, name, date and time.
   */
  public static void printAllEvents() {
    System.out.println("This are our current events:");
    for (Event event : events.values()) {
      event.printEventInfo();
    }
  }

  /**
   * Save all Event, Customer, and Ticket information into 3 different csv files.
   */
  public static void saveDatabase() {
    WriteCSV.writeEventList(events, NEWEVENTFILENAME);
    WriteCSV.writeCustomerList(customers, NEWCUSTOMERFILENAME);
    WriteCSV.writeTicketsList(ticketsPurchased, TICKETFILENAME);
  }

  /**
   * Write csv files for customers, events, and tickets. Move all ticket summaries into its
   * folder and all logs into its folder. Close scanner.
   */
  public static void closeProgram() {
    Admin.ticketSummary(getCustomer("robertalvarez"));
    Admin.ticketSummary(getCustomer("donaldduck"));
    Admin.ticketSummary(getCustomer("danielmejia"));
    Admin.ticketSummary(getCustomer("alinouri"));

    Log.logWrite(Level.FINE,"Writing new Customer, Event, and ticket lists.");
    saveDatabase();
    Admin.movTicketSummary();
    Log.movLogs();
    STDINScanner.getInstance().close();
  }
}
