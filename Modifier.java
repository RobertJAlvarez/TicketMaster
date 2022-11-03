import java.util.ArrayList;

/**
 * This class provide all the options that a admin can do to modify event characteristics.
 * 
 * @author Robert J Alvarez
 * @date September 23th, 2022
 */
public class Modifier {
  private static STDINScanner scnr = STDINScanner.getInstance();

  private static ArrayList<String> possibleChanges = new ArrayList<>();
  private Event event;
  private int nModification;
  private String update;

  /**
   * Constructor without parameters. Initialize and populate possibleChanges.
   */
  public Modifier () {
    populateChanges();
  }

  //Getters
  /**
   * If modification number exist, return modification selected.
   * 
   * @return String saying what modification was selected or if it doesn't exist.
   */
  public String getModification(int nModification) {
    if (modificationExist(nModification))
      return possibleChanges.get(nModification-1);  //Index start at 0, not at 1
    return "(modification requested doesn't exist)";
  }

  /**
   * Call getModification() with the store number of nModification.
   * 
   * @return String saying what modification was selected or if it doesn't exist.
   */
  public String getModification() {
    return getModification(nModification);
  }

  public Event getEvent() {
    return event;
  }

  public int getModificationNumber() {
    return nModification;
  }

  public String getUpdate() {
    return update;
  }

  //Setters
  public void setModificationNumber(int nModification) {
    this.nModification = nModification;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public void setUpdate(String update) {
    this.update = update;
  }

  //Methods
  /**
   * Fill all possible changes that can be made to the events.
   */
  private static synchronized void populateChanges() {
    //Don't populate the possible changes more than onces
    if (!possibleChanges.isEmpty()) {
      return;
    }

    possibleChanges.add("Name");
    possibleChanges.add("Date");
    possibleChanges.add("Time");
    for (String seatType : Database.getSeatTypes()) {
      possibleChanges.add(seatType + " price");
    }
  }

  /**
   * Print all modification that can be done to an event.
   */
  public static void printModificationsMenu() {
    System.out.println("Here is what you can change from the event:");
    int i = 1;
    for (String entry : possibleChanges)
      System.out.printf("%d: %s.%n", i++, entry);
  }

  /**
   * Check if there is a modification mapped to the number provided.
   * 
   * @param nModification - number of modification selected.
   * @return true or false depending if a modification is mapped to the number provided.
   */
  public boolean modificationExist(int nModification) {
    return possibleChanges.size() >= nModification;
  }

  /**
   * Call modificationExist() with the store number of nModification
   * 
   * @param nModification - number of modification selected.
   * @return true or false depending if a modification is mapped to the number provided.
   */
  public boolean modificationExist() {
    return modificationExist(nModification);
  }

  /**
   * If an event with the provided id exist and a valid modification number was provided, a string with the
   * attribute corresponding to the modification selected is returned.
   * 
   * @param event - Event to get the characteristic form.
   * @param nModification - number of modification selected.
   * @return String with the attribute corresponding to the modification selected.
   */
  public String getEventCharacteristic(int nModification) {
    String temp = "-1";

    if (nModification == 1) {
      temp = event.getName();
    } else if (nModification == 2) {
      temp = event.getDate();
    } else if (nModification == 3) {
      temp = event.getTime();
    } else if (nModification >= 4 && nModification <= 4+Database.getSeatTypes().length) {
      temp = "" + event.getSeatPrice(Database.getSeatTypes()[nModification-4]); //Convert float to string
    } else {
      System.out.println("Modify switch statement in Database to handle all modifications.");
    }
    return temp;
  }

  /**
   * Call getEventCharacteristic() with Event and the store number of nModification.
   * 
   * @param event - Event to get the characteristic form.
   * @return String with the attribute corresponding to the modification selected.
   */
  public String getEventCharacteristic() {
    return getEventCharacteristic(nModification);
  }

  /**
   * Use id number and modification selected to update the event attribute to what is in update.
   */
  public void updateEvent() {
    if (nModification == 1) {
      event.setName(update);
    } else if (nModification == 2) {
      event.setDate(update);
    } else if (nModification == 3) {
      event.setTime(update);
    } else if (nModification >= 4 && nModification <= 4+Database.getSeatTypes().length) {
      event.setSeatPrice(Database.getSeatTypes()[nModification-4], Float.parseFloat(update));
    } else {
      System.out.println("Modify switch statement in Database to handle all modifications.");
    }

    //Warn if new price is higher than next better ticket
    if ((nModification >= 5) && (nModification <= 4+Database.getSeatTypes().length) &&
        (Float.parseFloat(getEventCharacteristic(nModification-1)) < Float.parseFloat(update))) {
      System.out.println("WARNING: new " + getModification() + " is, at least, more expensive than " + getModification(nModification-1));
    //Warn if new price is lower than previous best ticket
    } else if ((nModification >= 4) && (nModification <= 3+Database.getSeatTypes().length) &&
               (Float.parseFloat(getEventCharacteristic(nModification+1)) > Float.parseFloat(update))) {
      System.out.println("WARNING: new " + getModification() + " is, at least, cheaper than " + getModification(nModification+1));
    }
  }

  /**
   * Read and save information to update event characteristic.
   */
  public void readUpdate() {
    String updateS;
    float updateF;

    if (nModification == 1) {
      updateS = scnr.nextLine();
    } else if (nModification == 2) {
      updateS = scnr.readDate();
    } else if (nModification == 3) {
      updateS = scnr.readTime();
    } else {
      do {
        System.out.println("(Make sure that the cost is greater than 0)");
        updateF = scnr.readNextFloat();
      } while (updateF < 0.01);   //Entrance most cost at least a penny
      updateS = Float.toString(updateF);
    }
    update = updateS;
  }

  /**
   * Use id, modification, and update to make a string to be save in records to save the changes just make.
   */
  public String getRecord() {
    return "Event ID " + event.getEventID() + " update " + getModification() + " to " + update + "\n";
  }
}
