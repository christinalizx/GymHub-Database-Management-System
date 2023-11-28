import Model.JDBC;
import View.View;
import Controller.Controller;

public class Main {


  public static void main(String[] args) {
    JDBC jdbc = JDBC.getInstance();
    View view = new View(new Controller(),jdbc);

    // Display login or registration prompt based on user choice
    int choice = view.showLoginOrRegisterChoice();

    if (choice == View.LOGIN_CHOICE) {
      // User chose login
      handleLogin(view);
    } else if (choice == View.REGISTER_CHOICE) {
      // User chose register
      handleRegistration(view);
    } else {
      // User clicked "Cancel" or closed the dialog
      System.out.println("Operation canceled by user.");
    }
  }

  private static void handleLogin(View view) {
    // Display login prompt
    String[] credentials = view.promptCredentials();

    // Check if the user clicked "Cancel"
    if (credentials == null || credentials.length == 0 || credentials[0].equals("CANCEL")) {
      System.out.println("Login canceled by user.");
      System.exit(0);
    }

    // Extract username and password
    String username = credentials[0];
    String password = credentials[1];

    // Verify credentials using the controller
    boolean isCredentialsValid = view.getController().verifyCredentials(username, password);

    if (isCredentialsValid) {
      // Successful login
      view.showOutput("Login successful!", "Success");
    } else {
      // Failed login
      view.showError("Invalid username or password.", "Login Failed");
    }
  }

  private static void handleRegistration(View view) {

    // Show registration form
    view.showRegistrationForm();
  }
}
