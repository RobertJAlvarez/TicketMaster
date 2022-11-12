package ticketmaster;

import java.util.logging.Level;

/**
 * This class group all the possible interactions that an administrator can have on the system.
 * 
 * @author Robert J Alvarez
 * @date October 18th, 2022
 */
public class User {
  private static STDINScanner scnr = STDINScanner.getInstance();

  private static Customer customer;

  private User() {}

  //Getter
  public static Customer getUser() {
    return customer;
  }

  //Setter
  public static void setCustomer(Customer customer) {
    User.customer = customer;
  }

  //Methods
  public static void userLogged() {
    if (customer == null) {
      System.out.println("A user must be logged for the system to display user options.");
      return;
    }

    do {
      printOptions();
    } while (runOption() != 0);
  }

  /**
   * Print all possible interactions that a user can have with the system.
   */
  private static void printOptions() {
    System.out.println("We have the following options for users:");
    System.out.println("1. Buy seats for an event.");
    System.out.println("2. Sell seats purchased.");
    System.out.println("Anything else to log out.");
  }

  /**
   * Execute the option mapped to a user action.
   * 1. Purchase from 1 to 6 seat at a time for an event for as many events wanted.
   * 2. Sell seats or an entire ticket already purchase from this system.
   * 
   * @return
   */
  private static int runOption() {
    int option = scnr.readNextInt();

    switch (option) {
      case (1):
        buySeats();
        break;
      case (2):
        sellSeats();
        break;
      default:
        System.out.println("Thank you for keeping our system up to day, hope to see you back soon:)");
        option = 0;
    }

    return option;
  }

  /**
   * Sell seats or an entire ticket already purchase from this system.
   */
  private static void sellSeats() {
    //TODO:
  }

  /**
   * Manage all the interactions from system and user.
   */
  private static void buySeats() {
    int inputID = -1;
    Event event;
    Ticket ticket;

    //TODO: Add option to sell seats already buy. Check the tickets that the customer have and when the seat is selected to be resell check the day of the event that is not passed.

    do {
      inputID = scnr.askForEvent("to buy a seat");
      event = Database.getEvent(inputID);

      if (event != null) { //Valid event ID enter
        Log.logWrite(Level.FINE, "Event ID enter " + inputID + ", proceed to purchased seats.");
        ticket = customer.buySeats(event);      //Reserve seats
        Database.addTicket(ticket);
      }
    } while (inputID != -1);
  }

  /**
   * Get username and password from user and return the customer that match those characteristics, or null otherwise.
   */
  public static void logUser() {
    customer = matchNames();
    if ( (customer != null) && (!customer.checkPassword()) ) {
      customer = null;
    }
  }

  /**
   * Give three opportunities to input first name and last name correctly. If withing the three tries
   * a user match first name and last name, we return the customer object.
   * 
   * @return Customer that match first and last name.
   */
  private static Customer matchNames() {
    String names;
    Customer customer;

    for (int iTries = 3; iTries > 0; iTries--) {
      System.out.print("What is your first name? ");
      names = scnr.nextLine().toLowerCase();

      System.out.print("What is your last name? ");
      names = names.concat(scnr.nextLine().toLowerCase());

      customer = Database.getCustomer(names);
      if (customer != null)
        return customer;
      else
        System.out.println("Names didn't match an existent user with it.");
    }
    return null;
  }

  /**
   * Set customer to null so we have to log another one back in to use User methods.
   */
  public static void logOffUser() {
    customer = null;
  }
}
