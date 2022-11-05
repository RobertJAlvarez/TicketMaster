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
  /**
   * Manage all the interactions from system and user.
   */
  public static void userLogged() {
    int inputID = -1;
    Event event;
    Ticket ticket;

    //TODO: add option to sell seats already buy. Check the tickets that the customer have and when the seat is selected to be resell check the day of the event that is not passed.

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
      names = scnr.nextLine();

      System.out.print("What is your last name? ");
      names = names.concat(scnr.nextLine());

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
