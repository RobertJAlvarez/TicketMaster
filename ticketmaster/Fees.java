package ticketmaster;

/**
 * Class to save fee information and return the fee prices for convenience, service, and charity.
 * 
 * @author Daniel Marin
 * @date November 9 2022
 */
public class Fees {
  private static final float CONVENIENCEFEE = ((float) 2.5);  //Convenience fee is of $2.5
  private static final float SERVICEFEE = ((float) 0.005);     //Service fee is of 0.5%
  private static final float CHARITYFEE = ((float) 0.0075);    //Charity fee is of 0.75%

  private Fees() {}

  public static float getConveniencefee() {
    return CONVENIENCEFEE;
  }

  public static float getServicefee() {
    return SERVICEFEE;
  }

  public static float getCharityfee() {
    return CHARITYFEE;
  }
}
