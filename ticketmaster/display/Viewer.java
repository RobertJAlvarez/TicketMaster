package ticketmaster.display;
public class Viewer {
  private Viewer() {}

  public static void userOrAdmin() {
    askUserOrAdmin();
  }

  public static void logUser() {
    LoginViewer.logUser();
  }

  private static void askUserOrAdmin() {
    //
  }
}
