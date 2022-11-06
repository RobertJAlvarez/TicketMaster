package com.Ticketmaster.mypackage;
import java.util.HashMap;

/**
 * Class to save tax information base on State so it can return the state tax rate for an event.
 * 
 * @author Robert J Alvarez
 * @date October 20th, 2022
 */
public class Tax {
  private static HashMap<String, Float> taxInfo;

  private Tax() {}

  /**
   * Currently there is only one state use on the event, which is Texas with taxes of 8.25%.
   * So, we hard code the state and the percentage.
   */
  private static void populateTaxInfo() {
    taxInfo = new HashMap<>();
    taxInfo.put("Texas", (float) 8.25);
  }

  /**
   * Given an event, we use its state information and use it to return the state percentage value.
   * 
   * @param event - Event with the state attribute (it should had been assigned at the moment of its creations).
   * @return float with the state tax percentage.
   */
  public static float getTaxPercentage(Event event) {
    if (taxInfo == null) {
      populateTaxInfo();
    }

    return taxInfo.get(event.getState());
  }
}
