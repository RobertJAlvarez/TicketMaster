package ticketmaster;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import ticketmaster.display.FileChooser;
import ticketmaster.display.Viewer;

/**
 * This class group all the possible interactions that an administrator can have on the system.
 * 
 * @author Robert J Alvarez
 * @date September 25th, 2022
 */
public class Admin {
  private static STDINScanner scnr = STDINScanner.getInstance();
  private static Set<String> ticketSummaryFiles = new HashSet<>();
  protected static final String FIREWORKSPLANNEDHEADER = "Fireworks Planned";
  protected static final String FIREWORKSCOSTHEADER = "Fireworks Cost";
  protected static final String VENUETYPEHEADER = "Venue Type";

  /**
   * Constructor with all parameters.
   */
  private Admin() {}

  //Methods
  public static void logged() {
    do {
      printOptions();
    } while (runOption() != 0);
    Viewer.makeVisible();
  }

  /**
   * Print all possible interactions that an administrator can have with the system.
   */
  private static void printOptions() {
    System.out.println("We have the following options for administrators:");
    System.out.println("1. Modify event information.");
    System.out.println("2. See event statistics.");
    System.out.println("3. Create an event.");
    System.out.println("4. Run automatic purchases.");
    System.out.println("5. Write electronic ticket summary for a customer.");
    System.out.println("6. Print amount of money gained by TicketMiner company for all events.");
    System.out.println("7. Print amount of money gained by TicketMiner company for a events.");
    System.out.println("8. Cancel an event.");
    System.out.println("Anything else to log out.");
  }

  /**
   * Execute the option mapped to an admin action.
   * 1. Modification of event characteristic.
   * 2. Calculate and print statistics of an event.
   * 3. Create an event from scratch.
   * 4. Run automatic purchases.
   * 5. Write electronic ticket summary for a customer.
   * 
   * @return int with the number of option run, if no option was run we return 0.
   */
  private static int runOption() {
    int option = scnr.readNextInt();

    switch (option) {
      case (1):
        modifyEvent();
        break;
      case (2):
        seeStatistics();
        break;
      case (3):
        createEvent();
        break;
      case (4):
        autoPurchase();
        break;
      case (5):
        ticketSummary();
        break;
      case (6):
        printTicketMinerFeesCollected();
        break;
      case (7):
        printEventFeesCollected();
        break;
      case (8):
        cancelEvent();
        break;
      default:
        System.out.println("Thank you for keeping our system up to day, hope to see you back soon:)");
        option = 0;
    }
    return option;
  }

  /**
   * Print all fees collected from TicketMiner.
   */
  private static void printTicketMinerFeesCollected() {
    System.out.println("TicketMiner fees collected:");
    System.out.println("\tService fees: " + Database.getServiceFee());
    System.out.println("\tConvenience fees: " + Database.getConvenienceFee());
    System.out.println("\tCharity fees: " + Database.getCharityFee());
  }

  /**
   * Print all fees collected from a event.
   */
  private static void printEventFeesCollected() {
    int inputID;
    Event event;

    do {
      inputID = scnr.askForEvent("to print fees amount collected");
      event = Database.getEvent(inputID);

      if (event != null) {
        Log.logWrite(Level.FINE, "Event ID enter " + inputID + ", proceed to print fees.");
        System.out.println(event.getName() + " fees collected:");
        System.out.println("\tService fees: " + event.getServiceFee());
        System.out.println("\tConvenience fees: " + event.getConvenienceFee());
        System.out.println("\tCharity fees: " + event.getCharityFee());
      }
    } while (inputID != -1);
  }

  /**
   * Remove an event from the database. Doing that it would return taxes, fees, and seat cost after discount return to every customer that
   * had a seat reserved for the event. The TicketMiners profits are adjusted as needed.
   */
  private static void cancelEvent() {
    int inputID;
    Event event;
    Customer customer;

    do {
      inputID = scnr.askForEvent("to cancel the event");
      event = Database.getEvent(inputID);

      if (event != null) {
        Log.logWrite(Level.FINE, "Event ID enter " + inputID + ", proceed to cancel the event.");
        for (Ticket ticket : event.getTickets()) {
          //Return tax, fees, and subtotal to customer
          customer = ticket.getCustomer();
          customer.setMoneyAvailable(customer.getMoneyAvailable() + ticket.getTotalCost());

          //Update TicketMiner total fees colleted
          Database.returnTaxes(ticket.getTaxesPay());
          Database.returnServiceFee(ticket.getServiceFeePay());
          Database.returnConvenienceFee(ticket.getConvenienceFeePay());
          Database.returnCharityFee(ticket.getCharityFeePay());

          //Update customer saving from membership
          if (customer.hasTicketMinerMembership()) {
            float discount = ticket.getSubtotal()/((float) 9.0);
            //Update total customer saving
            customer.setTotalSave(customer.getTotalSave() - discount);
          }

          //Remove the ticket from database and customer list
          Database.removeTicket(ticket, new String[] {Customer.class.getSimpleName()});
        }

        //Remove event from database
        Database.removeEvent(event);
      }
    } while (inputID != -1);
  }

  /**
   * Create a .txt file with all the tickets purchase by the customer.
   * 
   * @param customer - Customer to make the .txt file for his/her tickets.
   */
  public static void ticketSummary(Customer customer) {
    if (customer != null) {
      customer.writeTicketSummary();
      ticketSummaryFiles.add(customer.getTicketSummaryFilename());
    }
  }

  /**
   * Its ask the admin for the user first and last name that he wants to make a summary for all tickets.
   */
  private static void ticketSummary() {
    String names;
    Customer customer;

    System.out.print("What is the customer first name? ");
    names = scnr.nextLine();

    System.out.print("What is the customer last name? ");
    names = names.concat(scnr.nextLine());

    customer = Database.getCustomer(names);
    if (customer != null) {
      customer.writeTicketSummary();
      ticketSummaryFiles.add(customer.getTicketSummaryFilename());
    } else {
      System.out.println("Names didn't match an existent user with it.");
    }
  }

  /**
   * Gives the admin the option to change characteristics of an event. Such options are listed inside the
   * printModificationsMenu method in Modifier class.
   */
  private static void modifyEvent() {
    Modifier modify = new Modifier();
    int inputID = 0;

    // Modify events if wanted
    do {
      inputID = scnr.askForEvent("you want to modify");
      modify.setEvent(Database.getEvent(inputID));

      if (modify.getEvent() != null) {
        Log.logWrite(Level.FINE, "Event ID enter " + inputID + ", proceed to ask for modifications.");
        System.out.println("Enter a number to indicate a modification from the following menu:");
        Modifier.printModificationsMenu();
        modify.setModificationNumber(scnr.readNextInt());
        if (modify.modificationExist()) {
          Log.logWrite(Level.FINE, "Modification to be change: " + modify.getModification());
          //Output the event modification request
          System.out.println("Previous " + modify.getModification() + ": " + modify.getEventCharacteristic());
          System.out.println("Enter new event " + modify.getModification() + ":");

          //Read input
          modify.readUpdate();

          //If user decide to finish the program mid modification input
          if (STDINScanner.isEndOfFileEnter()) {
            break;
          }

          //Update information
          if (modify.getModification().equalsIgnoreCase("name")) {  //If the name of the event is going to be change
            Database.updateEventNameToIdMap(modify.getEvent().getName(), (String) modify.getUpdate(), modify.getEvent().getEventID());
          }

          //Update event
          modify.updateEvent();

          //Record change
          Log.logWrite(Level.INFO, modify.getRecord());
        } else {  //If an invalid modification number was entered.
          Log.logWrite(Level.FINE, "Modification with index " + modify.getModificationNumber() + " doesn't exist.");
          System.out.println("Enter a valid modification number.");
        }
      }
    } while (inputID != -1);
  }

  /**
   * Ask user for the event that it would like to see the statistics for. Once a valid event has been given,
   * we calculate the statistics of the event and print them out to System.out.
   */
  private static void seeStatistics() {
    Event event;
    int inputID;

    // Modify events if wanted
    do {
      inputID = scnr.askForEvent("to calculate statistics");
      event = Database.getEvent(inputID);

      if (event != null) {
        Log.logWrite(Level.FINE, "Event ID enter " + inputID + ", proceed to calculate and print statistics.");
        Statistics.printStatistics(event);
      }
    } while (inputID != -1);
  }

  /**
   * Let the user create an event (with its venue) given some minimal characteristics,
   * e.g., event type, name, date, time, etc.
   */
  private static void createEvent() {
    HashMap<String, String> entries = new HashMap<>();
    String[] eventTypes = {"Sport", "Concert", "Special"};
    Event event;

    entries.put(ReadCSV.EVENTIDHEADER, String.valueOf(Database.getNewEventID()));

    System.out.println("What type of event would be hold?");
    chooseOption(entries, "Event Type", eventTypes, "Enter a valid number to choose an event type.");

    System.out.print("Enter event name: ");
    entries.put("Name", scnr.nextLine());

    entries.put("Date", scnr.readDate());
    entries.put("Time", scnr.readTime());

    System.out.println("Would the event have fireworks? (Yes/No)");
    entries.put(FIREWORKSPLANNEDHEADER, scnr.nextLine());

    //Set the price of the fireworks if they are planned, otherwise put an empty string.
    if (entries.get(FIREWORKSPLANNEDHEADER).equalsIgnoreCase("Yes")) {
      System.out.println("How much would the fireworks cost? ");
      entries.put(FIREWORKSCOSTHEADER, String.valueOf(scnr.readNextInt()));
    } else {
      entries.put(FIREWORKSCOSTHEADER, "");
    }

    //If at any point the end of file character was given, we don't make the event
    if (STDINScanner.isEndOfFileEnter()) {
      return;
    }

    event = makeEvent(entries);
    if (event != null) {
      Venue venue = createVenue();
      if (venue == null) {
        return;
      }
      event.setVenue(venue);
      //Make the seats for the event
      createSeats(event);
      Database.addEvent(event);
    } else {
      System.out.println("We couldn't create an event");
    }
  }

  /**
   * Given a Map that match the header of the csv column as key and the value inside the cell as the value we
   * make an event base on its type and return it.
   * 
   * @param entries - Map<String,String> with the header of the column names as keys and the line information just read as values
   */
  public static Event makeEvent(Map<String, String> entries) {
    int eventID = Integer.parseInt(entries.get(ReadCSV.EVENTIDHEADER));
    Event event = null;
    boolean haveFireworks = entries.get(FIREWORKSPLANNEDHEADER).equalsIgnoreCase("Yes");
    int fireworksCost = ((entries.get(FIREWORKSCOSTHEADER).length() != 0) ? Integer.parseInt(entries.get(FIREWORKSCOSTHEADER)) : 0);
    String nameHeader = "Name";

    GregorianCalendar date = getCalendar(entries.get("Date") + " " + entries.get("Time"));

    switch (entries.get("Event Type")) {
      case ("Sport"):
        event = new Sport(eventID, entries.get(nameHeader), date, haveFireworks, fireworksCost);
        break;
      case ("Concert"):
        event = new Concert(eventID, entries.get(nameHeader), date, haveFireworks, fireworksCost);
        break;
      case ("Special"):
        event = new Special(eventID, entries.get(nameHeader), date, haveFireworks, fireworksCost);
        break;
      default:
        System.out.println("Event type didn't match Sport, Concert nor Special.");
        Log.logWrite(Level.WARNING, "event with id: " + eventID + " could not be created.\n");
    }
    return event;
  }

  /**
   * Input string must have the following format: "MM/dd/yyyy hh:mm a" following the GregorianCalendar definitions for each character.
   * 
   * @param input - String with the format "MM/dd/yyyy hh:mm a" use to make a GregorianCalendar object to be return.
   * @return GregorianCalendar with the information provided in input
   */
  public static GregorianCalendar getCalendar(String input) {
    String[] token;
    int day;
    int month;
    int year;
    int hour;
    int minute;

    token = input.split("[ /:]");

    try {
      month = Integer.parseInt(token[0]) - 1; //GregorianCalendar starts with January at 0
      day = Integer.parseInt(token[1]);
      year = Integer.parseInt(token[2]);
      hour = Integer.parseInt(token[3]);
      minute = Integer.parseInt(token[4]);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    //The original csv files have year as YY so we make it YYYY
    if (year < 100) {
      year += 2000;
    }

    if (token[5].contains("PM")) {
      hour += 12;
    }

    return new GregorianCalendar(year, month, day, hour, minute);
  }

  /**
   * Print all the options, read the user answer, if it selected a valid options store it in entries using header as the key.
   * If no valid option was selected print the error message and ask again for another input.
   * 
   * @param entries - HashMap on where to store the option wanted as the value and the header as the key.
   * @param header - Use this header as the key of the entries.
   * @param options - Contain all the different options that the user can choose from.
   * @param errorMsg - If no valid option is entered, print this message.
   */
  private static void chooseOption(HashMap<String,String> entries, String header, String[] options, String errorMsg) {
    int input;
    boolean goodOption = false;

    for (int i = 1; i <= options.length; i++) {  //Print venue options
      System.out.println(i + ": " + options[i-1]);
    }
    while ( (!goodOption) && (!STDINScanner.isEndOfFileEnter()) ) {
      input = scnr.readNextInt();
      if ( (input >= 1) && (input <= options.length) ) {
        entries.put(header, options[input-1]);
        goodOption = true;
      } else {
        System.out.println(errorMsg);
      }
    }
  }

  /**
   * Let the customer create a venue with some minimum requires like venue type, name, capacity, and cost.
   */
  private static Venue createVenue() {
    HashMap<String, String> entries = new HashMap<>();
    String[] venueTypes = {"Stadium","Arena","Auditorium","Open Air"};
    String[] venueNames = {"Sun Bowl Stadium","Don Haskins Center","Magnoffin Auditorium","San Jacinto Plaza","Centennial Plaza"};
    String nameHeader = "Venue Name";
    int capacity = -1;
    int cost = -1;

    //Get venue type
    System.out.println("What Venue is needed to hold the event?");
    chooseOption(entries, VENUETYPEHEADER, venueTypes, "Enter a valid number to choose a venue to hold the event.");

    //Get venue name
    System.out.println("On which venue would you like to hold the event?");
    chooseOption(entries, nameHeader, venueNames, "Enter a valid number to choose a venue to hold the event.");

    //Get venue capacity
    System.out.println("What is the capacity of " + entries.get(nameHeader) + "?");
    while ( (capacity <= 0) && (!STDINScanner.isEndOfFileEnter())) {
      capacity = scnr.readNextInt();
    }
    entries.put("Capacity", String.valueOf(capacity));

    //Get venue cost
    System.out.println("What is the cost for renting " + entries.get(nameHeader) + "?");
    while ( (cost <= 0) && (!STDINScanner.isEndOfFileEnter())) {
      cost = scnr.readNextInt();
    }
    entries.put("Cost", String.valueOf(cost));

    //If at any point the end of file character was given, we don't make the venue
    if (STDINScanner.isEndOfFileEnter()) {
      return null;
    }

    //Make venue and set it to event
    return makeVenue(entries);
  }

  /**
   * Use entries to make and return a venue with the respective type.
   * 
   * @param entries - Map<String,String> with the header of the column names as keys and the line information just read as values
   * @return Venue created using entries.
   */
  public static Venue makeVenue(Map<String,String> entries) {
    Venue venue = null;
    int capacity = Integer.parseInt(entries.get("Capacity"));
    int cost = Integer.parseInt(entries.get("Cost"));
    String venueName = "Venue Name";
    int numSeatsUnavailable;

    if (entries.containsKey("Pct Seats Unavailable")) {
      numSeatsUnavailable = Integer.parseInt(entries.get("Pct Seats Unavailable"))*capacity;
    } else if (entries.containsKey("Seats Unavailable Pct")) {
      numSeatsUnavailable = Integer.parseInt(entries.get("Seats Unavailable Pct"))*capacity;
    } else if (entries.containsKey("Num Seats Unavailable")) {
      numSeatsUnavailable = Integer.parseInt(entries.get("Num Seats Unavailable"));
    } else if (entries.containsKey("Seats Unavailable Num")) {
      numSeatsUnavailable = Integer.parseInt(entries.get("Seats Unavailable Num"));
    } else {
      System.err.println("There is no information about number of seats unavailable in csv file.");
      numSeatsUnavailable = 0;
    }

    switch (entries.get(VENUETYPEHEADER)) {
      case ("Stadium"):
        venue = new Stadium(entries.get(venueName), numSeatsUnavailable, capacity, cost);
        break;
      case ("Arena"):
        venue = new Arena(entries.get(venueName), numSeatsUnavailable, capacity, cost);
        break;
      case ("Auditorium"):
        venue = new Auditorium(entries.get(venueName), numSeatsUnavailable, capacity, cost);
        break;
      case ("Open Air"):
      case ("OpenAir"):
        venue = new OpenAir(entries.get(venueName), numSeatsUnavailable, capacity, cost);
        break;
      default:
        System.err.println("Venue type didn't match Stadium, Arena, Auditorium, nor Open Air");
        System.err.println("Venue type read:" + entries.get(VENUETYPEHEADER));
    }
    return venue;
  }

  /**
   * Ask the admin for the ticket general price cost and auto generate all the other ticket prices.
   * 
   * @param event - Event where to store the seats created.
   */
  private static void createSeats(Event event) {
    float genPrice;

    //Make regular seats
    System.out.println("Enter the price of a general admission ticket: ");
    do {
      System.out.println("Price for a general admission seat is between $0.01 and $1,000.");
      genPrice = scnr.readNextFloat();
    } while ( (genPrice < (float) 0.0) || (genPrice > (float) 1000.0) );

    for (String seatType : Database.getSeatTypes()) {
      switch (seatType) {
        case ("VIP"):
          //VIP takes 5% of the venue capacity and is 5 times more than the general admission cost.
          event.makeNSeatsByPct(seatType, String.valueOf(genPrice*5.0), 5);
          break;
        case ("Gold"):
          //Gold takes 10% of the venue capacity and is 3 times more than the general admission cost.
          event.makeNSeatsByPct(seatType, String.valueOf(genPrice*3.0), 10);
          break;
        case ("Silver"):
          //Silver takes 15% of the venue capacity and is 2.5 times more than the general admission cost.
          event.makeNSeatsByPct(seatType, String.valueOf(genPrice*2.5), 15);
          break;
        case ("Bronze"):
          //Bronze takes 20% of the venue capacity and is 1.5 times more than the general admission cost.
          event.makeNSeatsByPct(seatType, String.valueOf(genPrice*1.5), 20);
          break;
        case ("General Admission"):
          //General Admission takes 45% of the venue capacity.
          event.makeNSeatsByPct(seatType, String.valueOf(genPrice), 45);
          break;
        default:
          System.out.println("Not all seat types options are been accounted for in Admin.getVenueInfo(), " + seatType + " is missing.");
      }
    }

    //Reserved seats takes 5% of the venue capacity.
    event.setHowManyReservedSeats((5*event.getVenue().getCapacity())/100);
  }

  /**
   * Let the user select a file to process some purchases on the system.
   */
  private static void autoPurchase() {
    String filename = FileChooser.chooseFile("to populate events");
    ReadCSV.populateDatabase(filename, "AutoPurchase");
  }

  /**
   * Move all the tickets summaries created into TicketSummaries folder.
   */
  public static void movTicketSummary() {
    File logDir = new File("./TicketSummaries");

    try {
      if (!logDir.exists()) {  //If directory doesn't exist, we make it
        logDir.mkdirs();
      } else {    //If directory exist, remove all its files
        for (File file : logDir.listFiles()) {
          Files.delete(Paths.get("./TicketSummaries/" + file.getName()));
        }
      }

      //Move all the ticket summaries created from current directory to TicketSummaries directory
      for (String filename : ticketSummaryFiles) {
        //Add file to TicketSummaries, if it wasn't success full, we print an error message
        if (!new File(filename).renameTo(new File("./TicketSummaries/" + filename))) {
          System.err.println("File: " + filename + " couldn't be move to TicketSummaries.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ticketSummaryFiles.clear();
    }
  }
}
