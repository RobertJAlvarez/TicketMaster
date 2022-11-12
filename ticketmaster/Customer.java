package ticketmaster;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class module a user that can buy tickets with seats for different events. Customer class include all important attributes
 * of a user like first and last name, money available, tickets purchased, ticket miner membership, username and password.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class Customer {
  private static STDINScanner scnr = STDINScanner.getInstance();

  private int customerID;
  private String firstName;
  private String lastName;
  private float moneyAvailable;
  private HashMap<Integer,Ticket> ticketsPurchased;
  private boolean ticketMinerMembership;
  private String username;
  private String password;
  private float totalSave;
  private String ticketSummaryFilename;

  /**
   * Constructor which initialize customer names, membership and passwords. It also instantiate the tickets purchased.
   * 
   * @param customerID - int for customer id
   * @param firstName - String for customer first name
   * @param lastName - String for customer last name
   * @param moneyAvailable - float representing amount of money left
   * @param ticketMinerMembership - boolean with true value if customer has a ticket miner membership
   * @param username - String for customer username
   * @param password - String for customer password
   */
  public Customer(int customerID, String firstName, String lastName, float moneyAvailable, boolean ticketMinerMembership, String username, String password) {
    this.customerID = customerID;
    this.firstName = firstName;
    this.lastName = lastName;
    this.moneyAvailable = moneyAvailable;
    this.ticketsPurchased = new HashMap<>();
    this.ticketMinerMembership = ticketMinerMembership;
    this.username = username;
    this.password = password;
    totalSave = (float) 0.0;
  }

  /**
   * Constructor without parameters. Only instantiate ticketsPurchased.
   */
  public Customer() {
    ticketsPurchased = new HashMap<>();
  }

  //Getters
  public int getCustomerID() {
    return customerID;
  }
  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public float getMoneyAvailable() {
    return moneyAvailable;
  }

  public boolean hasTicketMinerMembership() {
    return ticketMinerMembership;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public float getTotalSave() {
    return totalSave;
  }

  public String getTicketSummaryFilename() {
    return ticketSummaryFilename;
  }

  //Setters
  public void setCustomerID(int customerID) {
    this.customerID = customerID;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setMoneyAvailable(float moneyAvailable) {
    this.moneyAvailable = moneyAvailable;
  }

  public void setTicketMinerMembership(boolean ticketMinerMembership) {
    this.ticketMinerMembership = ticketMinerMembership;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setTotalSave(float totalSave) {
    this.totalSave = totalSave;
  }

  private void setTicketSummaryFilename(String filename) {
    ticketSummaryFilename = filename;
  }

  //Methods
  /**
   * Ask three times for a password and compare it with the one of the user.
   * 
   * @return true if the password was input correctly within 3 tries, false otherwise.
   */
  public boolean checkPassword() {
    int nTriesLeft = 3;

    System.out.println("You have " + nTriesLeft + " tries to enter your password.");
    while (nTriesLeft-- > 0) {
      System.out.print("Enter your password: ");
      if ( checkPassword(scnr.nextLine()) )
        break;
      System.out.println("Incorrect password, try again:");
      System.out.println(nTriesLeft + " tries left.");
    }
    return nTriesLeft > 0;
  }

  /**
   * 
   * 
   * @param passwordInput
   * @return
   */
  public boolean checkPassword(String passwordInput) {
    return passwordInput.equals(getPassword());
  }

  /**
   * Add ticket to ticketsPurchased where the purchase id is the key and the ticket is the value.
   * 
   * @param ticket
   */
  public void addTicketPurchased(Ticket ticket) {
    ticketsPurchased.put(ticket.getPurchaseID(), ticket);
  }

  /**
   * Make a Ticket, call buySeat() to buy an individual seat and return the ticket with all the purchases made.
   * 
   * @param event - Event to buy seats for.
   * @return Ticket with the seats purchase made.
   */
  public Ticket buySeats(Event event) {
    Ticket ticket = new Ticket(event, event.getVenue(), this);
    buySeat(ticket);
    if (ticket.getNumberOfSeatsPurchases() == 0) {
      ticket.setPurchaseTime();
      ticket.setPurchaseID();
    } else {
      ticket = null;
    }
    return ticket;
  }

  /**
   * Make a Ticket, call buySeat() to buy an individual seat and return the ticket with all the purchases made.
   * 
   * @param event - Event to buy seats for.
   * @return Ticket with the seats purchase made.
   */
  public Ticket buySeats(Event event, String seatType, int nSeats) {
    Ticket ticket = new Ticket(event, event.getVenue(), this);
    buyNSeats(ticket, seatType, nSeats);
    if (ticket.getNumberOfSeatsPurchases() == 0) {
      ticket.setPurchaseTime();
      ticket.setPurchaseID();
    } else {
      ticket = null;
    }
    return ticket;
  }

  /**
   * Recursive function that allows the customer to buy seats for the event and save them on a ticket.
   * 
   * @param ticket - Ticket to save the information about the purchases made.
   */
  private void buySeat(Ticket ticket) {
    Event event = ticket.getEvent();
    String seatType;

    do {
      System.out.println("This are our remaining seat options:");
      event.printSeatsOptions();
      System.out.println("Anything else to exit.");

      System.out.println("Which seat type would you like to buy?");
      seatType = event.getSeatOptionN(scnr.readNextInt());

      //Check that seat type exist
      if (seatType.length() != 0) {
        buyNSeats(ticket, seatType, 1);
      } else {
        break;
      }
    } while (ticket.getNumberOfSeatsPurchases() < Ticket.getMaxNumberOfSeats());

    //If customer had purchase six seats, we let the user know that 6 purchases is the maximum number per transaction and stop the purchase with only six.
    if (ticket.getNumberOfSeatsPurchases() >= Ticket.getMaxNumberOfSeats()) {
      System.out.println("I'm sorry but the maximum number of purchases allow per transaction are 6.");
      System.out.println("You can continue buying by completing this transaction and starting a new one.");
      System.out.println("Thank you for you comprehension:)");
    }
  }

  /**
   * Check event to see if it have equal or more amount of seats of the type given. If it does and the
   * customer can afford it, we process the purchases. Otherwise, we don't do anything.
   * 
   * @param event - Event from where to try to process the seats purchase from.
   * @param seatType - String with the type of seat to purchase the seats.
   * @param nSeats - int with the number of seats wanted to be purchase.
   * @return Ticket with all the transactions made.
   */
  private void buyNSeats(Ticket ticket, String seatType, int nSeats) {
    Event event = ticket.getEvent();
    int nAvailableSeats;
    float seatCost;
    final float taxes = Tax.getTaxPercentage(event)/(float)100.0; //Get taxes base on event location

    nAvailableSeats = event.getNumberOfSeatsAvailable(seatType);
    seatCost = event.getSeatPrice(seatType);

    //Check that there are enough seats to purchase
    if (nAvailableSeats >= nSeats) {
      //Give 10% discount in subtotal if customer is ticket miner member
      if (hasTicketMinerMembership()) {
        //Update how much your customer had save
        setTotalSave(getTotalSave() + seatCost*nSeats/(float)10.0);
        //Update how much the event had lost from discounts
        event.setTotalDiscounted(event.getTotalDiscounted() + seatCost*nSeats/(float)10.0);
        //Actual cost of seat after discount
        seatCost -= seatCost/(float)10.0;
      }

      //Calculate tax
      seatCost += seatCost*taxes;
      Database.addTaxesCollected(taxes);

      //Check that user have enough money to purchase nSeats
      if (getMoneyAvailable() >= nSeats*seatCost) {
        //Update number of seats
        event.setNumSeats(seatType, nAvailableSeats-nSeats);

        //Update customer available money
        setMoneyAvailable(getMoneyAvailable()-seatCost*nSeats);

        //Create seats
        Seat seat;
        for (int i = 0; i < nSeats; i++) {
          seat = new Seat(seatType, seatCost);
          //Add purchase to ticket
          ticket.addPurchase(seat);
        }

        //Set ticket attributes
        ticket.setTotalCost(ticket.getTotalCost() + seatCost*nSeats);
      } else {
        System.out.println("Customer " + getLastName() + ", " + getFirstName() + " don't have enough money to purchase " + nSeats + seatType + " seats.");
      }
    } else {
      System.out.println("Event (event id: " + event.getEventID() + ") have less than " + nSeats + seatType + " seats to sell.");
    }
  }

  /**
   * Write the header for all the necessary information to replicate the customer only by reading the file.
   * Last character appended to file is a new line character.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public static void writeCSVHeader(FileWriter writer) {
    try {
      writer.append("ID,First Name,Last Name,Money Available,Concerts Purchased,TicketMiner Membership,Username,Password\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fill all the columns made by Customer.writeCSVHeader() with the customer information.
   * Last character appended to file is a new line character.
   * 
   * @param writer
   */
  public void writeContentToCSV(FileWriter writer) {
    try {
      writer.append(getCustomerID() + ",");
      writer.append(getFirstName() + ",");
      writer.append(getLastName() + ",");
      writer.append(getMoneyAvailable() + ",");
      writer.append(ticketsPurchased.size() + ",");
      writer.append(hasTicketMinerMembership() + ",");
      writer.append(getUsername() + ",");
      writer.append(getPassword() + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create a file using customerFirstName_customerLastName_Ticket_Summary.txt as name of the file
   * and save all the ticket information for all tickets of the user.
   */
  public void writeTicketSummary() {
    setTicketSummaryFilename(getFirstName() + "_" + getLastName() + "_Ticket_Summary.txt");

    try (FileWriter writer = new FileWriter(getTicketSummaryFilename())) {
      for (Ticket ticket : ticketsPurchased.values()) {
        writer.append(ticket.getSummary());
      }
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
