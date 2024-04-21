package ticketmaster;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Statistics class allow administrators to calculate the number of seats sold,
 * total revue, current and expect profit from an event by providing one.
 * 
 * @author Robert J Alvarez
 * @date September 24th, 2022
 */
public class Statistics {
  private static LinkedHashMap<String,String[]> seatsSold; //key = seatType | value[0] = # of sold seats, value[1] = total revenue
  private static float totalRevenue;
  private static float expectedProfit;
  private static int totalSeatsSold;

  private Statistics() {}

  //Getters
  public static float getTotalRevenue() {
    return totalRevenue;
  }

  public static float getExpectedProfit() {
    return expectedProfit;
  }

  public static int getTotalSeatsSold() {
    return totalSeatsSold;
  }

  //Methods
  /**
   * Calculate number of seats sold, total revue, expect profit from an event by providing one.
   */
  private static void calculateStatistics(Event event) {
    //Make sure that there is an event before starting a calculation.
    if (event == null) {
      System.out.println("An event must be provided (use setEvent()) before starting a calculation.");
      return;
    }

    String seatType;
    int nSeats;
    float seatRevenue;

    seatsSold = new LinkedHashMap<>();

    //Get the type of seats that could have been sold for the event and calculate expected profit from the unsold seats
    expectedProfit = (float) 0.0;
    for (String setKey : Database.getSeatTypes()) {
      seatsSold.put(setKey, new String[] {"0", "0.0"}); //First entry if for the number of seats, second entry for the total profit
      //Multiply the number of seats available by their price to get the expected profit from selling all of them.
      expectedProfit += event.getNumberOfSeatsAvailable(setKey) * event.getSeatPrice(setKey);
    }

    totalRevenue = (float) 0.0;
    totalSeatsSold = 0;
    //Iterate over the tickets sold and update seatsSold information (# of seats sold and revenue).
    for (Ticket ticket : event.getTickets()) {
      for (Seat seat : ticket.getSeatsPurchased()) {
        seatType = seat.getSeatType();                //Get seat type that was purchased.
        nSeats = Integer.parseInt(seatsSold.get(seatType)[0]);      //Get number of seats that had been sold.
        seatRevenue = Float.parseFloat(seatsSold.get(seatType)[1]); //Get total amount of money collected from selling curring tickets.

        seatsSold.get(seatType)[0] = String.valueOf(nSeats+1);      //Increase the number of seat and save it
        seatsSold.get(seatType)[1] = String.valueOf(seatRevenue+seat.getPrice()); //Increase the total money earn by selling the seats and save it
        totalRevenue += seatRevenue;    //Update total amount of money collected from selling seats
        totalSeatsSold++;       //Increase count of total number of seats sold
      }
    }

    //Expected profit contain the total price from all of the seats (sold and unsold).
    expectedProfit += totalRevenue;
  }

  /**
   * Print event information as well as what was calculated by calling calculateStatistics() before hand.
   */
  public static void printStatistics(Event event) {
    if (event == null) {
      System.err.println("A null event has been passed to printStatistics()");
      return;
    }

    calculateStatistics(event);

    float cost = event.getVenue().getCost();
    //Print event information
    event.printEventInfo();
    System.out.println("Event capacity: " + event.getVenue().getCapacity());
    //Print number of seats sold information
    System.out.println("Total Seats sold: " + totalSeatsSold);
    for (Map.Entry<String,String[]> entry : seatsSold.entrySet()) {
      System.out.printf("Total %s Seats Sold: %s%n", entry.getKey(), entry.getValue()[0]);
    }
    //Print total revenue per seat type
    for (Map.Entry<String,String[]> entry : seatsSold.entrySet()) {
      System.out.printf("Total revenue for %s tickets: $%s%n", entry.getKey(), entry.getValue()[1]);
    }
    System.out.printf("Total revenue for all tickets: $%.2f%n", totalRevenue);
    //Print expected profit and actual profit
    System.out.printf("Expected profit (Sell Out): $%.2f%n", expectedProfit-cost);
    System.out.printf("Actual profit: $%.2f%n", totalRevenue-cost);
  }

  /**
   * Write the header for all the necessary information to replicate the event only by reading the file.
   * Last character appended to file is a new line character.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public static void writeCSVHeader(FileWriter writer) {
    try {
      String[] seatTypes = Database.getSeatTypes();
      writer.append("Total Seats sold,");
      for (String seatType : seatTypes) {
        writer.append("Total " + seatType + " Seats sold," + "Total revenue for " + seatType + " tickets,");
      }
      writer.append("Total revenue for all tickets,");
      writer.append("Expected profit (Sell Out),");
      writer.append("Actual profit");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fill all the columns made by Statistics.writeCSVHeader() with the event statistics information.
   * Last character added to file is not a special character like \n or comma.
   * 
   * @param writer - FileWriter ready to be write on
   */
  public static void writeContentToCSV(FileWriter writer, Event event) {
    if (event == null) {
      System.err.println("A null event has been passed to printStatistics()");
      return;
    }

    calculateStatistics(event);

    try {
      float cost = event.getVenue().getCost();
      //Print number of seats and revenue information
      writer.append(totalSeatsSold + ",");
      for (String[] seatInfo : seatsSold.values()) {
        writer.append(seatInfo[0] + "," + seatInfo[1] + ",");
      }
      writer.append(totalRevenue + ",");
      writer.append((expectedProfit-cost) + ",");
      writer.append((totalRevenue-cost) + "");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
