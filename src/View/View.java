package View;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import Controller.Controller;
import Model.GymUsers;
import Model.JDBC;

public class View {
  public static final int LOGIN_CHOICE = 0;
  public static final int REGISTER_CHOICE = 1;
  private final Controller controller;
  private JDBC jdbc;
  public static final int FORUMS_CHOICE = 1;
  public static final int GYM_INFO_CHOICE = 2;
  public static final int WORKOUT_DATA_CHOICE = 3;
  public static final int USER_INFO_CHOICE = 4;

  public View(Controller controller, JDBC jdbc) {
    this.controller = controller;
    this.jdbc = jdbc;
  }
  public int showLoginOrRegisterChoice() {
    Object[] options = {"Login", "Register", "Cancel"};
    int choice = JOptionPane.showOptionDialog(
            null,
            "Choose an operation:",
            "Login or Register",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]);

    return choice;
  }
  public void handleLogin() {
    // Display login prompt
    String[] credentials = promptCredentials();

    // Check if the user clicked "Cancel"
    if (credentials == null || credentials.length == 0 || credentials[0].equals("CANCEL")) {
      System.out.println("Login canceled by user.");
      System.exit(0);
    }

    // Extract username and password
    String username = credentials[0];
    String password = credentials[1];

    // Verify credentials using the controller
    boolean isCredentialsValid = getController().verifyCredentials(username, password);

    if (isCredentialsValid) {
      // Successful login
      getController().setLoggedInUsername(username); // Set the logged-in username
      showOutput("Login successful!", "Success");
      showMainPage();
    } else {
      // Failed login
      showError("Invalid username or password.", "Login Failed");
    }
  }


  public String[] promptCredentials() {
    JPanel panel = new JPanel(new GridLayout(2, 2));
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    panel.add(new JLabel("Username:"));
    panel.add(usernameField);
    panel.add(new JLabel("Password:"));
    panel.add(passwordField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Enter Credentials",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String username = usernameField.getText();
      char[] passwordChars = passwordField.getPassword();
      String password = new String(passwordChars);

      // Check if both the username and password are not empty
      if (!username.trim().isEmpty() && !password.trim().isEmpty()) {
        String[] credentials = {username, password};
        return credentials;
      } else {
        return null;
      }

    } else {
      String[] cancel = {"CANCEL"};
      return cancel;
    }
  }

  public List<String> getGymNamesFromDatabase() {
    List<String> gymNames = new ArrayList<>();

    try {
      Connection connection = jdbc.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT gym_name FROM gyms");

      while (resultSet.next()) {
        String gymName = resultSet.getString("gym_name");
        gymNames.add(gymName);
      }

      resultSet.close();
      statement.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return gymNames;
  }


  // Inside the View class
  public void showRegistrationForm() {
    List<String> gymNames = getGymNamesFromDatabase();

    JPanel panel = new JPanel(new GridLayout(5, 2));
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JTextField addressField = new JTextField();

    DefaultComboBoxModel<String> gymModel = new DefaultComboBoxModel<>(gymNames.toArray(new String[0]));
    JComboBox<String> gymSpinner = new JComboBox<>(gymModel);

    panel.add(new JLabel("Username:"));
    panel.add(usernameField);
    panel.add(new JLabel("Password:"));
    panel.add(passwordField);
    panel.add(new JLabel("Address:"));
    panel.add(addressField);
    panel.add(new JLabel("Select Gym:"));
    panel.add(gymSpinner);

    int result = JOptionPane.showConfirmDialog(null, panel, "Register",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String username = usernameField.getText();
      char[] passwordChars = passwordField.getPassword();
      String password = new String(passwordChars);
      String address = addressField.getText();

      String selectedGym = (String) gymSpinner.getSelectedItem();

      if (!username.trim().isEmpty() && !password.trim().isEmpty() && !address.trim().isEmpty() && selectedGym != null) {
        controller.addUser(username, password, address, selectedGym);
      } else {
        JOptionPane.showMessageDialog(null, "All fields are required for registration.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void showMainPage() {
    // Create the main page with buttons
    JPanel panel = new JPanel(new GridLayout(2, 2));

    JButton forumsButton = new JButton("Forums");
    JButton gymInfoButton = new JButton("Gym Information");
    JButton workoutButton = new JButton("Workout & Exercise Data");
    JButton userInfoButton = new JButton("User Information");

    panel.add(forumsButton);
    panel.add(gymInfoButton);
    panel.add(workoutButton);
    panel.add(userInfoButton);

    JFrame frame = new JFrame("Main Page");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setSize(400, 300);
    frame.setVisible(true);

    // Add action listeners for each button
    forumsButton.addActionListener(e -> handleButtonAction("Forums"));
    gymInfoButton.addActionListener(e -> handleButtonAction("Gym Information"));
    workoutButton.addActionListener(e -> handleButtonAction("Workout & Exercise Data"));
    userInfoButton.addActionListener(e -> handleButtonAction("User Information"));
  }

  private void handleButtonAction(String buttonName) {
    // Handle different button actions here
    switch (buttonName) {
      case "Forums":
        List<String> forumNames = controller.getForumNames();
        showForumPage(forumNames);
        break;
      case "User Information":
        showUserInformation();
    }
  }

  private void showForumPage(List<String> forumNames) {
    // Create a spinner for selecting a forum
    JComboBox<String> forumSpinner = new JComboBox<>(forumNames.toArray(new String[0]));

    JPanel forumPanel = new JPanel(new GridLayout(2, 2));
    forumPanel.add(new JLabel("Select Forum:"));
    forumPanel.add(forumSpinner);

    int result = JOptionPane.showConfirmDialog(null, forumPanel, "Select Forum",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String selectedForum = (String) forumSpinner.getSelectedItem();
      // Call the controller method to handle the selected forum
      controller.handleForumPost(selectedForum);
    }
  }

  public void showGymInformation() {
    // Implement logic to display gym information
    showMessage("Gym Information Placeholder", "Gym Information");
  }

  public void showWorkoutData() {
    // Implement logic to display workout & exercise data
    showMessage("Workout & Exercise Data Placeholder", "Workout & Exercise Data");
  }

  public void showUserInformation() {
    String username = getController().getLoggedInUsername();
    GymUsers gymUser = getController().getUserInformation(username);

    if (gymUser != null) {
      String userInfo = "Username: " + gymUser.getUsername() + "\n" +
              "Password: " + gymUser.getPassword() + "\n" +
              "Address: " + gymUser.getAddress() + "\n" +
              "Gym Name: " + getController().getGymName(username);

      showOutput(userInfo, "User Information");
    } else {
      showError("User information not found.", "Error");
    }
  }

  private void showMessage(String message, String title) {
    JTextArea textArea = new JTextArea(message);
    textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
  }





  public void showError(String message, String title) {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public String inputPrompt(String message, String title) {
    JTextArea textArea = new JTextArea(message);
    textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    return JOptionPane.showInputDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public void showOutput(String message, String title) {
    JTextArea textArea = new JTextArea(message);
    textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public Controller getController() {
    return controller;
  }
}