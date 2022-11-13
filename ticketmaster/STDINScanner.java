package ticketmaster;

import java.util.Scanner;
import java.util.logging.Level;

/**
 * Create a Scanner that reads from System.in. It can be gotten by calling getInstance() and it have functions to
 * read an int (readNextInt()), read a float (readNextFloat()), read a date with format DD/MM/YYYY (readDate()),
 * read time with format XX:XX AM (or PM), return next line trimmed (nextLine()), returns an event id (askForEvent()),
 * and it close the scanner from System.in by calling close().
 * 
 * @author Robert J Alvarez
 * @date September 25th, 2022
 */
public class STDINScanner {
  //Singleton Object
  private static STDINScanner obj;

  private static final String EXITWORD = "EXIT";
  private static boolean endOfFileEnter = false;
  private Scanner scnr;

  //Getter
  public static boolean isEndOfFileEnter() {
    return endOfFileEnter;
  }

  //Setter
  private static void setEndOfFileEnter(boolean status) {
    endOfFileEnter = status;
  }

  //Methods
  private STDINScanner() {
    scnr = new Scanner(System.in);
  }

  public static synchronized STDINScanner getInstance() {
    if (obj == null) {
      obj = new STDINScanner();
    }
    return obj;
  }

  //Methods
  /**
   * Read a line from System.in and convert the value to int. If a non-integer is given another option
   * is given to the user to give a right input.
   * 
   * @return int given by the user from System.in or -1 if a non-integer was given.
   */
  public int readNextInt() {
    String input = "";
    int num;

    try {
      input = nextLine();
      if (input.equals(EXITWORD)) {
        setEndOfFileEnter(true);
        num = -1;
      } else {
        num = Integer.parseInt(input);
      }
    } catch (NumberFormatException e) {
      System.err.println(e.toString());
      num = readNextInt();
    }
    return num;
  }

  /**
   * Read a line from System.in and convert the value to float. If an error occur, another option
   * is given to the user to give a right input.
   * 
   * @return float given by the user from System.in or -1 if a non-number was given.
   */
  public float readNextFloat() {
    String input;
    float num;

    try {
      input = nextLine();
      if (input.equals(EXITWORD)) {
        setEndOfFileEnter(true);
        num = (float) -1.0;
      } else {
        num = Float.parseFloat(input);
      }
    } catch (NumberFormatException e) {
      System.err.println(e.toString());
      num = readNextFloat();
    }
    return num;
  }

  /**
   * 
   * 
   * @return valid String date following the format MM/DD/YYYY
   */
  public String readDate() {
    //(MM/DD/YYYY)
    String[] temp;
    String day;
    boolean validDate = false;

    System.out.println("Enter event Date: (MM/DD/YYYY)");

    do {
      day = nextLine();
      temp = day.split("/");
      if (isEndOfFileEnter()) {
        break;
      }
      //We must have three tokens for a valid date format
      if ( (temp.length == 3) && checkValidDate(temp[0], temp[1], temp[2]) ) {
        validDate = true;
      }
    } while (!validDate);

    return day;
  }

  /**
   * 
   * 
   * @param day
   * @param month
   * @param year
   * @return
   */
  private boolean checkValidDate(String m, String d, String y) {
    final int[] dayRange = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int day;
    int month;
    int year;
    boolean valid = false;

    try {
      day = Integer.parseInt(d);
      month = Integer.parseInt(m);
      year = Integer.parseInt(y);
      //Check the day is positive, month is between 1 and 12, and year is between 2022 and 9999
      if ( (day >= 1) && (month >= 1) && (month <= 12) && (year >= 2022) && (year <= 9999) ) {
        //Check that the day is no larger than the last day number given the month
        valid = (day <= dayRange[month-1]);
        //TODO: Account for february having 29 days on leap years
      }
    } catch (NumberFormatException e) {
      System.out.println("Invalid day format used, try again with a valid format.");
    }

    return valid;
  }

  /**
   * 
   * 
   * @return valid String time following the format XX:XX A/PM
   */
  public String readTime() {
    //XX:XX AM (or PM)
    String temp;
    String[] token;
    boolean validTime = false;

    System.out.println("Enter event time: [XX:XX AM (or PM)]");

    do {
      //Get new line and make all the letter upper case
      temp = nextLine().toUpperCase();
      if (isEndOfFileEnter()) {
        temp = "";
        break;
      }

      token = temp.split("[ :]");
      if (token.length == 3) {
        if (checkValidTime(token[0], token[1], token[2].trim())) {
          validTime = true;
        } else {
          validTime = false;
        }
      }
    } while (!validTime);

    return temp;
  }

  /**
   * 
   * 
   * @param h
   * @param m
   * @param time
   * @return
   */
  private boolean checkValidTime(String h, String m, String time) {
    int hour;
    int minute;
    boolean result = false;

    try {
      //Get last two character from input
      hour = Integer.parseInt(h);
      minute = Integer.parseInt(m);
      //Check validity of input by checking that hour is less than 11, and minutes are less than 59.
      result = (( (hour > 0) && (hour <= 12) && (minute >= 0) && (minute <= 59) ) &&
          //Check that we have a PM or AM time given
          ( (time.charAt(0) == 'P') || (time.charAt(0) == 'A') ) &&
            (time.charAt(1) == 'M'));
    } catch (NumberFormatException e) {
      System.out.println("Invalid time format used, try again with a valid format.");
    }

    return result;
  }

  /**
   * @return String read from System.in as input by calling java.util.Scanner.nextLine() and trim()
   */
  public synchronized String nextLine() {
    String read = "";

    try {
      if (scnr.hasNextLine()) {
        read = scnr.nextLine().trim();
      }
      //If end of file is reach (usually given by ctrl+d)
      else {
        read = EXITWORD;
        setEndOfFileEnter(true);
      }
    } catch (IllegalStateException e) {
      obj = new STDINScanner();
      return nextLine();
    }
    return read;
  }

  /**
   * We gave the option to access an event by ID number or by the name and return the ID number of the event wanted.
   * If a non valid ID is given, we let the user know that input doesn't match an event and return a non valid ID.
   * If a non valid name is given, we let the user know that the input doesn't match an event and return 0 so the menu would get printed.
   * 
   * @param reason - String explaining why the user would like to input an event ID #.
   * @return int number with the event ID # input during the execution of the program (if event name is given we map it to its event ID #).
   */
  public int askForEvent(String reason) {
    Event event = null;
    String temp;
    int inputID = 0;

    Log.logWrite(Level.FINE,"Ask for input.");
    System.out.print("Enter ID number or name of the event " + reason + ", 0 see all event options, or -1 to exit: ");

    temp = nextLine();
    if (temp.equals(EXITWORD)) {
      setEndOfFileEnter(true);
      return -1;
    }

    //Try to get event assuming the name was input
    event = Database.getEvent(temp);
    if (event == null) {  //If name of event wasn't found, try converting the input to int and match it to an event ID
      try {
        inputID = Integer.parseInt(temp);
      } catch (NumberFormatException e) {
        Log.logWrite(Level.FINE,"User enter {}, which doesn't match an event ID or name." + inputID);
        System.out.println("Enter a valid event ID or name.");
      }
    } else {
      inputID = event.getEventID();
    }

    if (inputID == 0) {
      Database.printAllEvents();
    } else if (inputID == -1) {
      Log.logWrite(Level.FINE, "User enter " + inputID + ", so we are exiting the system.");
      System.out.println("Thank you for coming, hope to see you back soon:)");
    } else if (Database.getEvent(inputID) == null) {    //Invalid ID enter
      Log.logWrite(Level.FINE, "User enter " + inputID + ", which is a nonexistence event ID or name.");
      System.out.println("Invalid input ID, try again.");
    }

    return inputID;
  }

  /**
   * Close scanner from System.in and set the synchronize object to null
   */
  public synchronized void close() {
    scnr.close();
    obj = null;
  }
}
