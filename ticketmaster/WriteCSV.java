package ticketmaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.io.FileWriter;

/**
 * Class made to write a csv file for events, customers, and tickets.
 * 
 * @author Robert J Alvarez
 * @date September 29th, 2022
 */
public class WriteCSV {
  private WriteCSV() {
    // This constructor is empty, class is use only to write csv files given the
    // database as parameter
  }

  /**
   * Write a csv file that save all the customers information.
   * 
   * @param customers - HashMap with all the possible customers.
   * @param filename  - String with name of file to be created and write
   *                  information into.
   */
  public static void writeCustomerList(Map<Integer, Customer> customers, String filename) {
    changeFilename(filename, "prev" + filename);

    // Write to file
    try (FileWriter writer = new FileWriter(filename)) {
      // Write header
      Customer.writeCSVHeader(writer);
      for (Customer customer : customers.values()) {
        customer.writeContentToCSV(writer);
      }
      writer.flush();
      // Closure of write is automatic by try-with-resources
    } catch (IOException e) {
      e.printStackTrace();
      // Writing the file was unsuccessful
    }
  }

  /**
   * Write a csv file that save all the events information.
   * 
   * @param events   - HashMap with all the events listed
   * @param filename - String with name of file to be created and write
   *                 information into.
   */
  public static void writeEventList(Map<Integer, Event> events, String filename) {
    changeFilename(filename, "prev" + filename);
    // Write to file
    try (FileWriter writer = new FileWriter(filename)) {
      Event.writeCSVHeader(writer);
      // Fill all columns for the header by using one event at the time
      for (Event event : events.values()) {
        event.writeContentToCSV(writer);
      }
      writer.flush();
      // Closure of write is automatic by try-with-resources
    } catch (IOException e) {
      e.printStackTrace(); // Writing the file was unsuccessful
    }
  }

  /**
   * Write a csv file that save all the tickets information.
   * 
   * @param tickets  - HashMap with all the tickets sell
   * @param filename - String with name of file to be created and write
   *                 information into.
   */
  public static void writeTicketsList(Map<Integer, Ticket> tickets, String filename) {
    changeFilename(filename, "prev" + filename);
    // Write to file
    try (FileWriter writer = new FileWriter(filename)) {
      Ticket.writeCSVHeader(writer);
      for (Ticket ticket : tickets.values()) {
        ticket.writeContentToCSV(writer);
      }
      writer.flush();
      // Closure of write is automatic by try-with-resources
    } catch (IOException e) {
      e.printStackTrace(); // Writing the file was unsuccessful
    }
  }

  /**
   * First string contain the name of the previous filename to be rename as
   * newFilename, if it exist. If newFilename exist before calling this function,
   * we would deleted.
   * 
   * @param prevFilename - String with name of file to be change of names to
   *                     newFilename, if it exist.
   * @param newFilename  - String with name of file to be deleted if newFilename
   *                     file exist and create a new one with prevFilename file
   *                     information.
   */
  private static void changeFilename(String prevFilename, String newFilename) {
    // Delete to file if it exist
    try {
      Path path = Paths.get(newFilename);
      if (Files.exists(path)) {
        Files.delete(path);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Change prevFilename file name to newFilename
    try {
      Path path = Paths.get(prevFilename);
      if (Files.exists(path)) {
        Files.move(path, path.resolveSibling(newFilename));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
