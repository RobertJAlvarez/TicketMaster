package ticketmaster;

import java.util.List;
import java.util.logging.Level;

import ticketmaster.display.LogUser;

/**
 * This class group all the possible interactions that an administrator can have
 * on the system.
 * 
 * @author Robert J Alvarez
 * @date October 18th, 2022
 */
public class User {
  private static STDINScanner scnr = STDINScanner.getInstance();

  private static Customer customer;

  private User() {
  }

  // Setter
  public static void setCustomer(Customer customer) {
    User.customer = customer;
  }

  // Methods
  public static void userLogged() {
    if (customer == null) {
      System.out.println("A user must be logged for the system to display user options.");
      return;
    }

    do {
      printOptions();
    } while (runOption() != 0);

    logOffUser();

    LogUser.makeVisible();
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
   * 1. Purchase from 1 to 6 seat at a time for an event for as many events
   * wanted.
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
        System.out.println("Thank you for using our system, hope to see you back soon:)");
        option = 0;
    }

    return option;
  }

  /**
   * Sell seats or an entire ticket already purchase from this system.
   */
  private static void sellSeats() {
    Ticket ticket;
    int option = 0;

    do {
      // Print all tickets that belongs to the user
      System.out.println("You had purchase all this tickets:");
      customer.printAllTickets();

      // Ask for ticket ID to continue with purchase or cancel it
      System.out.println("Enter ticket ID to continue with the sell options or enter a invalid ticket ID to cancel.");
      ticket = Database.getTicketPurchased(scnr.readNextInt());

      // If no valid ticket ID was given, cancel purchase
      if (ticket == null) {
        break;
      }

      // Ask what seat or all want to be sell
      System.out.println("What would you like to sell?");
      System.out.println(++option + ". All seats.");
      List<Seat> seats = ticket.getSeatsPurchased();
      for (Seat seat : seats) {
        System.out.println(++option + ". " + seat.toString());
      }
      System.out.println("Any other number to cancel.");

      // Read and do the option given
      option = scnr.readNextInt();
      if ((option == 1) || ((option == 2) && (seats.size() == 1))) {
        sellAllSeats(ticket);
      } else if ((option > 1) && (option <= seats.size() + 1)) {
        sellSeat(ticket, seats.get(option - 2));
      } else {
        System.out.println("Sell has been cancelled.");
      }
    } while (true);
  }

  /**
   * Iterate over all seats calling sellSeat and finishing removing the ticket
   * from the database.
   * 
   * @param ticket
   */
  public static void sellAllSeats(Ticket ticket) {
    List<Seat> seats = ticket.getSeatsPurchased();

    for (int i = seats.size() - 1; i >= 0; i--) {
      sellSeat(ticket, seats.get(i));
    }

    // Remove the ticket from database, event, and customer list
    Database.removeTicket(ticket, new String[] { Event.class.getSimpleName(), Customer.class.getSimpleName() });
  }

  /**
   * Return subtotal payed for the seat, no fees nor taxes are return. Make
   * another seat base on the type available for the event and remove the seat
   * form the ticket.
   * 
   * @param ticket
   * @param atIdx
   */
  private static void sellSeat(Ticket ticket, Seat seat) {
    float cost = seat.getPrice();
    Event event = ticket.getEvent();
    Customer customer = ticket.getCustomer();

    // Update subtotal
    ticket.setSubtotal(ticket.getSubtotal() - cost);
    // Make a seat base on the type, price would be ignore if a seat of the same
    // type already exist
    event.makeNSeatsByNum(seat.getSeatType(), String.valueOf(cost), 1);
    if (customer.hasTicketMinerMembership()) {
      float discount = cost / ((float) 9.0);
      // Update total discount given
      event.setTotalDiscounted(event.getTotalDiscounted() - discount);
      // Update total customer saving
      customer.setTotalSave(customer.getTotalSave() - discount);
    }
    // Remove seat from the ticket
    ticket.removeSeat(seat);
  }

  /**
   * Manage all the interactions from system and user.
   */
  private static void buySeats() {
    int inputID = -1;
    Event event;
    Ticket ticket;

    do {
      inputID = scnr.askForEvent("to buy a seat");
      event = Database.getEvent(inputID);

      if (event != null) { // Valid event ID enter
        Log.logWrite(Level.FINE, "Event ID enter " + inputID + ", proceed to purchased seats.");
        ticket = customer.buySeats(event); // Reserve seats
        Database.addTicket(ticket);
      }
    } while (inputID != -1);
  }

  /**
   * Set customer to null so we have to log another one back in to use User
   * methods.
   */
  private static void logOffUser() {
    customer = null;
  }
}
