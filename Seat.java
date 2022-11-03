/**
 * Class used to model a seat for an event with a price and category, e.g., VIP, Gold, Silver.
 * 
 * @author Robert J Alvarez
 * @date September 16th, 2022
 */
public class Seat {
  private String seatType;
  private float price;

  /**
   * Constructor with all parameters. Initialize all attributes.
   * 
   * @param seatType
   * @param price
   */
  public Seat(String seatType, float price) {
    this.seatType = seatType;
    this.price = price;
  }

  /**
   * Constructor without parameters. Do nothing.
   */
  public Seat() {}

  //Getters
  public String getSeatType() {
    return seatType;
  }

  public float getPrice() {
    return price;
  }

  //Setters
  public void setSeatType(String seatType) {
    this.seatType = seatType;
  }

  public void setPrice(float price) {
    this.price = price;
  }
}
