package View;

import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import Controller.Controller;
import Model.Exercise;
import Model.ExerciseSet;
import Model.GymUsers;
import Model.Gym;
import Model.Post;
import Model.Workout;

public class View {
  public static final int LOGIN_CHOICE = 0;
  public static final int REGISTER_CHOICE = 1;
  private final Controller controller;

  public View(Controller controller) {
    this.controller = controller;
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

  public void showRegistrationForm() {
    List<String> gymNames = getController().getGymNamesFromDatabase();

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
        break;
      case "Gym Information":
        showGymInformation();
        break;
      case "Workout & Exercise Data":
        showWorkouts(controller.getWorkouts());
        break;
    }
  }

  private void showForumPage(List<String> forumNames) {
    // Create a spinner for selecting a forum
    JComboBox<String> forumSpinner = new JComboBox<>(forumNames.toArray(new String[0]));

    JPanel forumPanel = new JPanel(new GridLayout(2, 2));
    forumPanel.add(new JLabel("Select Post:"));
    forumPanel.add(forumSpinner);

    int result = JOptionPane.showConfirmDialog(null, forumPanel, "Select Post",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String selectedForum = (String) forumSpinner.getSelectedItem();
      // Call the controller method to handle the selected forum
      handleForumPost(selectedForum);
    }
  }

  public void handleForumPost(String selectedForum) {
    // Get the posts for the selected forum
    List<Post> posts = getController().getPostsForForum(selectedForum);

    // Display the forum page with posts and likes
    showForumPage(selectedForum, posts);
  }

  private void showForumPage(String forumName, List<Post> posts) {
    // Example: Display posts and likes in a JTextArea
    JTextArea forumTextArea = new JTextArea();
    forumTextArea.append("Post: " + forumName + "\n\n");

    for (Post post : posts) {
      forumTextArea.append("Post ID: " + post.getPostId() + "\n");
      forumTextArea.append("Creator: " + post.getCreator() + "\n");
      forumTextArea.append("Date: " + post.getPostDate() + "\n");
      forumTextArea.append("Post: " + post.getPostText() + "\n");

      int likes = getController().getPostLikes(post.getPostId());
      forumTextArea.append("Likes: " + String.valueOf(likes) + "\n\n");
    }

    // Add a "New Post" button
    JButton newPostButton = new JButton("New Post");
    newPostButton.addActionListener(e -> handleNewPost(forumName));
    forumTextArea.append("\n");
    forumTextArea.append("-----\n");
    forumTextArea.append("Click 'New Post' to create a new post.\n");
    forumTextArea.append("-----\n");

    JScrollPane scrollPane = new JScrollPane(forumTextArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(newPostButton, BorderLayout.SOUTH);

    JOptionPane.showMessageDialog(null, panel, "Post Page", JOptionPane.INFORMATION_MESSAGE);
  }

  private void handleNewPost(String forumName) {
    // Implement logic to collect information for the new post
    JTextField postTextField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(2, 2));
    panel.add(new JLabel("New Post:"));
    panel.add(postTextField);

    // Create a custom OK button
    JButton okButton = new JButton("OK");

    // Add an ActionListener to the OK button
    okButton.addActionListener(e -> {
      // Get the parent window of the button (which is the current panel)
      Window parentWindow = SwingUtilities.getWindowAncestor(SwingUtilities.getWindowAncestor((Component) e.getSource()));

      // Dispose of the parent window (close the current panel)
      if (parentWindow != null) {
        parentWindow.dispose();
      }

      // Retrieve information for the new post
      String postText = postTextField.getText();

      // Call a method in the controller to add the new post
      getController().addPost(forumName, postText);

      // Display a message indicating successful addition
      JOptionPane.showMessageDialog(null, "Post added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

      // Refresh the forum page with the updated posts
      showForumPage(forumName, getController().getPostsForForum(forumName));
    });

    Object[] options = {okButton, "Cancel"};
    int result = JOptionPane.showOptionDialog(null, panel, "Create New Post",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
  }

  public void showGymInformation() {
    String username = getController().getLoggedInUsername();
    GymUsers gymUser = getController().getUserInformation(username);

    if (gymUser != null) {
      // Fetch gym information based on the username
      Gym gym = getController().getGymByUser(username);

      if (gym != null) {
        // Display gym information
        String userInfo = "Username: " + gymUser.getUsername() + "\n" +
                "Password: " + gymUser.getPassword() + "\n" +
                "Address: " + gymUser.getAddress() + "\n" +
                "Gym Name: " + gym.getGymName() + "\n" +
                "Gym Address: " + gym.getAddress() + "\n" +
                "Opening Time: " + gym.getOpeningTime() + "\n" +
                "Closing Time: " + gym.getClosingTime();

        JTextArea textArea = new JTextArea(userInfo);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "User Information", JOptionPane.INFORMATION_MESSAGE);
      } else {
        showError("Gym information not found for the user.", "Error");
      }
    } else {
      showError("User information not found.", "Error");
    }
  }

  public void showUserInformation() {
    String username = getController().getLoggedInUsername();
    GymUsers gymUser = getController().getUserInformation(username);

    if (gymUser != null) {
      String userInfo = "Username: " + gymUser.getUsername() + "\n" +
              "Password: " + gymUser.getPassword() + "\n" +
              "Address: " + gymUser.getAddress() + "\n" +
              "Gym Name: " + getController().getGymName(username);

      JTextArea textArea = new JTextArea(userInfo);
      textArea.setEditable(false);

      JScrollPane scrollPane = new JScrollPane(textArea);
      scrollPane.setPreferredSize(new Dimension(400, 300));

      JButton editButton = new JButton("Edit");
      editButton.addActionListener(e -> handleEditUserInformation(gymUser));

      JButton deleteButton = new JButton("Delete");
      deleteButton.addActionListener(e -> handleDeleteUser(gymUser.getUsername()));

      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout());
      panel.add(scrollPane, BorderLayout.CENTER);

      // Add buttons to a separate panel
      JPanel buttonPanel = new JPanel();
      buttonPanel.add(editButton);
      buttonPanel.add(deleteButton);

      // Add the button panel to the main panel
      panel.add(buttonPanel, BorderLayout.SOUTH);

      JOptionPane.showMessageDialog(null, panel, "User Information", JOptionPane.INFORMATION_MESSAGE);
    } else {
      showError("User information not found.", "Error");
    }
  }

  private void handleDeleteUser(String username) {
    int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

    if (choice == JOptionPane.YES_OPTION) {
      // Call the controller method to delete the user
      boolean isUserDeleted = getController().removeUserByUsername(username);

      if (isUserDeleted) {
        // Show a message indicating successful deletion
        showOutput("User deleted successfully.", "Deletion Successful");
      } else {
        // Show an error message if deletion failed
        showError("Failed to delete user. Please try again.", "Deletion Error");
      }

      // Exit the application
      System.exit(0);
    }
  }

  private void handleEditUserInformation(GymUsers gymUser) {
    JTextField usernameField = new JTextField(gymUser.getUsername());
    JPasswordField passwordField = new JPasswordField(gymUser.getPassword());
    JTextField addressField = new JTextField(gymUser.getAddress());

    JPanel editPanel = new JPanel(new GridLayout(4, 2));
    editPanel.add(new JLabel("Username:"));
    editPanel.add(usernameField);
    editPanel.add(new JLabel("Password:"));
    editPanel.add(passwordField);
    editPanel.add(new JLabel("Address:"));
    editPanel.add(addressField);

    int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit User Information",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String newUsername = usernameField.getText();
      char[] newPasswordChars = passwordField.getPassword();
      String newPassword = new String(newPasswordChars);
      String newAddress = addressField.getText();

      // Update user information in the database using stored procedures
      getController().updateUsername(gymUser.getUsername(), newUsername);
      getController().updatePassword(newUsername, newPassword);
      getController().updateAddress(newUsername, newAddress);

      // Show a dialog indicating successful update
      showOutput("User information updated successfully!", "Success");

      // Close the user information page
      Container parentContainer = editPanel.getTopLevelAncestor();
      if (parentContainer instanceof Window) {
        Window parentWindow = (Window) parentContainer;
        parentWindow.dispose();
      }
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

  // Method to display the list of workouts
  public void showWorkouts(List<Workout> workouts) {
    // Create a panel to hold the components
    JPanel panel = new JPanel(new BorderLayout());

    // Create a combo box for existing workouts
    JComboBox<Workout> workoutComboBox = new JComboBox<>(workouts.toArray(new Workout[0]));
    panel.add(workoutComboBox, BorderLayout.CENTER);

    // Create a button for adding a new workout
    JButton newWorkoutButton = new JButton("New Workout");
    newWorkoutButton.addActionListener(e -> {
      // Get the parent window of the button (which is the current panel)
      Window parentWindow = SwingUtilities.getWindowAncestor((Component) e.getSource());

      // Dispose of the parent window (close the current panel)
      if (parentWindow != null) {
        parentWindow.dispose();
      }

      handleNewWorkout();
    });
    panel.add(newWorkoutButton, BorderLayout.SOUTH);

    // Show the dialog with the panel
    int result = JOptionPane.showConfirmDialog(null, panel, "Select or Create Workout",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      if (workoutComboBox.getSelectedItem() != null) {
        // User selected an existing workout
        Workout selectedWorkout = (Workout) workoutComboBox.getSelectedItem();
        // Call a method to display exercises for the selected workout
        showExercises(selectedWorkout.getWorkoutId(), controller.getExercises(selectedWorkout.getWorkoutId()));
      } else {
        // User clicked "New Workout" button (handled by the ActionListener)
      }
    }
  }

  // Method to display the list of exercises
  public void showExercises(int workoutId, List<Exercise> exercises) {
    // Create a panel to hold the components
    JPanel panel = new JPanel(new BorderLayout());

    // Create a combo box for existing exercises
    JComboBox<Exercise> exerciseComboBox = new JComboBox<>(exercises.toArray(new Exercise[0]));
    panel.add(exerciseComboBox, BorderLayout.CENTER);

    // Create a button for adding a new exercise
    JButton newExerciseButton = new JButton("New Exercise");
    newExerciseButton.addActionListener(e -> {
      // Get the parent window of the button (which is the current panel)
      Window parentWindow = SwingUtilities.getWindowAncestor((Component) e.getSource());

      // Dispose of the parent window (close the current panel)
      if (parentWindow != null) {
        parentWindow.dispose();
      }

      handleNewExercise(workoutId);
    });
    panel.add(newExerciseButton, BorderLayout.SOUTH);

    // Show the dialog with the panel
    int result = JOptionPane.showConfirmDialog(null, panel, "Select or Create Exercise",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      if (exerciseComboBox.getSelectedItem() != null) {
        // User selected an existing exercise
        Exercise selectedExercise = (Exercise) exerciseComboBox.getSelectedItem();
        // Call a method to display exercise sets for the selected exercise
        showExerciseSets(workoutId, selectedExercise.getExerciseId(), controller.getExerciseSets(selectedExercise.getExerciseId()));
      } else {
        // User clicked "New Exercise" button (handled by the ActionListener)
      }
    }
  }

  // Method to display the list of exercise sets
  public void showExerciseSets(int workoutId, int exerciseId, List<ExerciseSet> exerciseSets) {
    // Create a panel to hold the components
    JPanel panel = new JPanel(new BorderLayout());

    // Create a text area for displaying exercise sets
    JTextArea exerciseSetsTextArea = new JTextArea();
    exerciseSets.forEach(set -> exerciseSetsTextArea.append(set.toString() + "\n"));

    JScrollPane scrollPane = new JScrollPane(exerciseSetsTextArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));
    panel.add(scrollPane, BorderLayout.CENTER);

    // Create a button for adding a new exercise set
    JButton newExerciseSetButton = new JButton("New Exercise Set");
    newExerciseSetButton.addActionListener(e -> {
      // Get the parent window of the button (which is the current panel)
      Window parentWindow = SwingUtilities.getWindowAncestor((Component) e.getSource());

      // Dispose of the parent window (close the current panel)
      if (parentWindow != null) {
        parentWindow.dispose();
      }

      handleNewExerciseSet(workoutId, exerciseId);
    });
    panel.add(newExerciseSetButton, BorderLayout.SOUTH);

    // Show the dialog with the panel
    JOptionPane.showConfirmDialog(null, panel, "Exercise Sets",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
  }

  // Method to handle adding a new workout
  public void handleNewWorkout() {
    // Implement logic to collect information for the new workout
    JTextField descriptionField = new JTextField();
    JTextField durationField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(2, 2));
    panel.add(new JLabel("Description:"));
    panel.add(descriptionField);
    panel.add(new JLabel("Duration:"));
    panel.add(durationField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Workout",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      // Retrieve information for the new workout
      String description = descriptionField.getText();
      int duration = Integer.parseInt(durationField.getText());

      // Call a method in the controller to add the new workout
      getController().addWorkout(Date.valueOf(LocalDate.now()), description, duration);

      // Display a message indicating successful addition
      Window parentWindow = SwingUtilities.getWindowAncestor(panel);

      // Dispose of the parent window (which is the JDialog in this case)
      if (parentWindow != null) {
        parentWindow.dispose();
      }
      showOutput("Workout added successfully!", "Success");
      showWorkouts(controller.getWorkouts());
    }
  }

  // Method to handle adding a new exercise within a workout
  public void handleNewExercise(int workoutId) {
    // Implement logic to collect information for the new exercise
    JTextField nameField = new JTextField();
    JTextField notesField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(2, 2));
    panel.add(new JLabel("Name:"));
    panel.add(nameField);
    panel.add(new JLabel("Notes:"));
    panel.add(notesField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Exercise",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      // Retrieve information for the new exercise
      String exerciseName = nameField.getText();
      String notes = notesField.getText();

      // Call a method in the controller to add the new exercise to the specified workout
      getController().addExercise(workoutId, exerciseName, notes);

      // Display a message indicating successful addition
      showOutput("Exercise added successfully!", "Success");
      showExercises(workoutId, controller.getExercises(workoutId));
    }
  }

  // Method to handle adding a new exercise set within an exercise
  public void handleNewExerciseSet(int workoutId, int exerciseId) {
    // Implement logic to collect information for the new exercise set

    JTextField setsField = new JTextField();
    JTextField repsField = new JTextField();
    JTextField weightField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(3, 2));
    panel.add(new JLabel("Sets:"));
    panel.add(setsField);
    panel.add(new JLabel("Reps:"));
    panel.add(repsField);
    panel.add(new JLabel("Weight:"));
    panel.add(weightField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Add Exercise Set",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      // Retrieve information for the new exercise set
      int sets = Integer.parseInt(setsField.getText());
      int reps = Integer.parseInt(repsField.getText());
      int weight = Integer.parseInt(weightField.getText());

      // Call a method in the controller to add the new exercise set to the specified exercise
      getController().addExerciseSet(exerciseId, workoutId, sets, reps, weight);

      // Display a message indicating successful addition
      showOutput("Exercise Set added successfully!", "Success");
      showExerciseSets(workoutId, exerciseId, controller.getExerciseSets(exerciseId));
    }
  }

}