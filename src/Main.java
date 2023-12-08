import View.View;
import Controller.Controller;

public class Main {

  public static void main(String[] args) {
    View view = new View(new Controller());

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
    // Call the handleLogin method in the View
    view.handleLogin();
  }



  private static void handleRegistration(View view) {
    // Show the registration form
    view.showRegistrationForm();

    // Display login or registration prompt based on user choice
    int choice = view.showLoginOrRegisterChoice();

    if (choice == View.LOGIN_CHOICE) {
      // User chose login
      handleLogin(view);
    } else if (choice == View.REGISTER_CHOICE) {
      // User chose register
      handleRegistration(view); // Go back to registration
    } else {
      // User clicked "Cancel" or closed the dialog
      System.out.println("Operation canceled by user.");
    }
  }


  private static void handleMainPage(View view) {
    // Display the main page with buttons
    view.showMainPage();
  }
}
