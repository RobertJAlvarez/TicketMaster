package ticketmaster;

/*
 * Name: Robert Alvarez
 * Date: October 23rd, 2022
 * Instructor: Dr. Mejia
 * Lab description: Ticketmaster is a software that allows that using csv files as databases let users use the
 * system as either customers or administrator. Customers can buy seats for events and administrators can run
 * statistics for event, make events, run ticket summaries and process automatic purchases.
 * 
 * Honesty statement: "This work was done individually and completely on my own. I did not share,
 *    reproduce, or alter any part of this assignment for any purpose. I did not share code, upload
 *    this assignment online in any form, or view/received/modified code written from anyone else. All
 *    deliverables were produced entirely on my own. This assignment is part of an academic course at The
 *    University of Texas at El Paso and a grade will be assigned for the work I produced."
 */

import java.util.logging.Level;

import ticketmaster.viewer.Viewer;

/**
 * Ticketmaster have the main method that control the primary behaviors of the program such as reading csv files to
 * populate the database, log user or administrators, and close the program by writing updated csv files and loggers.
 * 
 * @author Robert J Alvarez
 * @date September 25th, 2022
 */
public class Ticketmaster {
  private static STDINScanner scnr = STDINScanner.getInstance();

  /**
   * Populate the Database, set the logger, and start running the customer/admin option to use the system.
   * At the end, it make three ticket summaries and close the program by calling closeProgram().
   * 
   * @param args - Array of strings with what was given when the code got compile.
   */
  public static void main(String[] args) {
    int input;
    int accessType = 0; //1 for Customer, 2 for administrator, and 0 to finish the program

    Database.populateDatabase();       //Fill Events, Venues, Customers and Tickets

    do {
      Log.logWrite(Level.FINE, "Ask for system access.");
      System.out.println("We have the following sign in options:");
      System.out.println("1. Customer.");
      System.out.println("2. Administrator.");
      System.out.println("Anything else to exit.");

      input = scnr.readNextInt();
      accessType = input;

      Log.logWrite(Level.FINE, "Input: " + input);
      switch (accessType) {
        case (1):
          Log.logWrite(Level.FINE,"Continue with Customer log in options.");
          Log.logWrite(Level.FINE,"Ask for user information to log in.");
          Viewer.logUser();
          break;
        case (2):
          Log.logWrite(Level.FINE,"Log as administrator.");
          Admin.logged();
          break;
        default:
          accessType = 0;
      }
    } while (accessType != 0);

    closeProgram();
  }

  /**
   * Write csv files for customers, events, and tickets. Move all ticket summaries into its
   * folder and all logs into its folder. Close scanner.
   */
  public static void closeProgram() {
    Admin.ticketSummary(Database.getCustomer("RobertAlvarez"));
    Admin.ticketSummary(Database.getCustomer("DonaldDuck"));
    Admin.ticketSummary(Database.getCustomer("AliNouri"));

    Log.logWrite(Level.FINE,"Writing new Customer, Event, and ticket lists.");
    Database.saveDatabase();
    Admin.movTicketSummary();
    Log.movLogs();
    scnr.close();
  }
}
