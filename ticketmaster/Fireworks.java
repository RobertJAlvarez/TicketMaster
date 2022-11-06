package ticketmaster;

/**
 * Fireworks interface for setters and getters.
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public interface Fireworks {
  //Getters
  public boolean areFireworksPlanned();
  public int getFireworksCost();
  //Setters
  public void setFireworksPlanned(boolean fireworksPlanned);
  public void setFireworksCost(int fireworksCost);
}
