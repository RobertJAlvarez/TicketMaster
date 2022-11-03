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
    STDINScanner.endOfFileEnter = status;
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
    String temp;
    String[] token;
    int day;
    int month;
    int year;
    boolean validDate = false;

    do {
      temp = nextLine();
      if (temp.equals(EXITWORD)) {
        setEndOfFileEnter(true);
        temp = "";
        break;
      }
      token = temp.split("/");
      //We must have three tokens for a valid format
      if (token.length == 3) {
        //Check that the month is between 1 and 12
        day = Integer.parseInt(token[0].replaceAll("\\D", "")); //\\D = [^0-9]
        //Check that the day is between 1 and 31
        month = Integer.parseInt(token[1].replaceAll("\\D", "")); //\\D = [^0-9]
        //Check that the year is between 2022 and 2099
        year = Integer.parseInt(token[2].replaceAll("\\D", "")); //\\D = [^0-9]
        //TODO: Count for months that don't have 31 days
        validDate = ( (day >= 1) && (day <= 12) && (month >= 1) && (month <= 31) && (year >= 2022) && (year <= 9999) );
        if (!validDate) {
          System.out.println("Invalid day format used, try again with a valid format.");
        }
      }
    } while (!validDate);

    return temp;
  }

  /**
   * 
   * 
   * @return valid String time following the format XX:XX AM (or PM)
   */
  public String readTime() {
    //XX:XX AM (or PM)
    String temp;
    String[] token;
    int hour;
    int minutes;
    String substring;
    boolean validTime = false;

    do {
      //Get new line and make all the letter upper case
      temp = nextLine().toUpperCase();
      if (temp.equals(EXITWORD)) {
        setEndOfFileEnter(true);
        temp = "";
        break;
      }
      token = temp.split(":");

      if (token.length == 2) {
        //Get last two character from input
        substring = token[1].length() > 2 ? token[1].substring(token[1].length() - 2) : token[1];
        hour = Integer.parseInt(token[0].replaceAll("\\D", "")); //\\D = [^0-9]
        minutes = Integer.parseInt(token[1].replaceAll("\\D", "")); //\\D = [^0-9]
        //Check validity of input by checking that hour is less than 11, and minutes are less than 59.
        validTime = (( (hour > 0) && (hour <= 12) && (minutes >= 0) && (minutes <= 59) ) &&
            //Check that we have a PM or AM time given
            ( (substring.charAt(0) == 'P') || (substring.charAt(0) == 'A') ) &&
              (substring.charAt(1) == 'M'));
        if (!validTime) {
          System.out.println("Invalid time format used, try again with a valid format.");
        }
      }
    } while (!validTime);

    return temp;
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
