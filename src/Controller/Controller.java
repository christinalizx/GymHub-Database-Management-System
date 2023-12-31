package Controller;

import Model.Exercise;
import Model.ExerciseSet;
import Model.GymUsers;
import Model.JDBC;
import Model.Gym;
import Model.Post;
import Model.Workout;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {
  private final JDBC jdbc;
  private String loggedInUsername;


  public Controller() {
    this.jdbc = JDBC.getInstance();
  }


  /**
   * Method to verify user credentials using the MySQL function
   *
   * @param username The username to be verified
   * @param password The password to be verified
   * @return True if the credentials are valid, false otherwise
   */
  public boolean verifyCredentials(String username, String password) {
    Connection connection = null;
    CallableStatement callableStatement = null;

    try {
      connection = JDBC.getInstance().getConnection();

      // Prepare the SQL call to the MySQL function
      String callStatement = "{? = CALL VerifyUserPassword(?, ?)}";
      callableStatement = connection.prepareCall(callStatement);

      // Register the output parameter as BOOLEAN
      callableStatement.registerOutParameter(1, java.sql.Types.BOOLEAN);

      // Set the input parameters
      callableStatement.setString(2, username);
      callableStatement.setString(3, password);

      // Execute the function call
      callableStatement.execute();

      // Get the result from the output parameter
      boolean isVerified = callableStatement.getBoolean(1);

      return isVerified;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Return false if an exception occurred
    return false;
  }

  public void addUser(String username, String password, String address, String gymName, boolean isStaff) {
    try {
      // Get the gymId based on the selected gymName
      int gymId = getGymIdByName(gymName);

      // Create a GymUsers object with the provided data
      GymUsers user = new GymUsers(username, password, address, gymId);

      // Add the user to the database
      addUserToDatabase(user);

      System.out.println("User added successfully!");

      // If the user is staff, add staff information to the gym_staff table
      if (isStaff) {
        addStaffToDatabase(username, gymId);
        System.out.println("Staff information added successfully!");
      }
    } catch (SQLException e) {
      // Handle database-related errors
      e.printStackTrace();
    }
  }

  private void addStaffToDatabase(String username, int gymId) throws SQLException {
    try (Connection connection = jdbc.getConnection()) {
      String sql = "INSERT INTO gym_staff (username, gym_id) VALUES (?, ?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, username);
        statement.setInt(2, gymId);
        statement.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean isUserStaff() {

    String query = "SELECT COUNT(*) FROM gym_staff WHERE username = ?";

    try (Connection connection = jdbc.getConnection()) {
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setString(1, loggedInUsername);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          return count > 0;
        }

      } catch (SQLException e) {
        e.printStackTrace();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void updateGymInformation(Gym gym) {
    try (Connection connection = jdbc.getConnection()) {
      String sql = "UPDATE gyms SET gym_name=?, address=?, opening_time=?, closing_time=? WHERE gym_id=?";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, gym.getGymName());
        statement.setString(2, gym.getAddress());
        statement.setTime(3, gym.getOpeningTime());
        statement.setTime(4, gym.getClosingTime());
        statement.setInt(5, gym.getGymId());


        statement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }
  }

  public void setLoggedInUsername(String username) {
    this.loggedInUsername = username;
  }

  public String getLoggedInUsername() {
    return loggedInUsername;
  }

  private int getGymIdByName(String gymName) throws SQLException {
    // Implement the logic to retrieve the gymId based on the gymName
    Connection connection = jdbc.getConnection();
    String query = "SELECT gym_id FROM gyms WHERE gym_name = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, gymName);
      var resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt("gym_id");
      } else {
        throw new SQLException("Gym not found: " + gymName);
      }
    }
  }

  private void addUserToDatabase(GymUsers user) throws SQLException {
    // Implement the logic to add a user to the database
    Connection connection = jdbc.getConnection();
    String query = "INSERT INTO gym_users (username, password, address, gym_id) VALUES (?, ?, ?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, user.getUsername());
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getAddress());
      preparedStatement.setInt(4, user.getGymId());
      preparedStatement.executeUpdate();
    }
  }

  public List<String> getForumNames() {
    // Use JDBC to fetch forum names from the database
    List<String> forumNames = new ArrayList<>();

    try {
      Connection connection = jdbc.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT forum_name FROM forums");

      while (resultSet.next()) {
        String forumName = resultSet.getString("forum_name");
        forumNames.add(forumName);
      }

    } catch (Exception e) {
      e.printStackTrace();
      // Handle exceptions as needed
    }

    return forumNames;
  }

  public GymUsers getUserInformation(String username) {
    Connection connection = null;
    CallableStatement callableStatement = null;
    ResultSet resultSet = null;
    GymUsers gymUser = null;

    try {
      connection = jdbc.getConnection();
      String query = "{CALL GetUserInformation(?)}";
      callableStatement = connection.prepareCall(query);
      callableStatement.setString(1, username);
      resultSet = callableStatement.executeQuery();

      if (resultSet.next()) {
        String password = resultSet.getString("password");
        String address = resultSet.getString("address");
        int gymId = resultSet.getInt("gym_id");

        // Create a GymUsers object with the retrieved information
        gymUser = new GymUsers(username, password, address, gymId);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return gymUser;
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

  public String getGymName(String username) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String gymName = null;

    try {
      connection = jdbc.getConnection();

      // Prepare the SQL statement to retrieve the gym name
      String query = "SELECT gyms.gym_name FROM gyms " +
              "JOIN gym_users gu ON gu.gym_id = gyms.gym_id " +
              "WHERE gu.username = ?";
      preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, username);

      // Execute the query
      resultSet = preparedStatement.executeQuery();

      // Check if the result set is not empty
      if (resultSet.next()) {
        // Extract gym name from the result set
        gymName = resultSet.getString("gym_name");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return gymName;

  }

  public void updateUsername(String oldUsername, String newUsername) {
    try {
      Connection connection = jdbc.getConnection();
      String query = "{CALL UpdateUsername(?, ?)}";
      try (CallableStatement callableStatement = connection.prepareCall(query)) {
        callableStatement.setString(1, oldUsername);
        callableStatement.setString(2, newUsername);
        callableStatement.executeUpdate();
        loggedInUsername = newUsername;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle exceptions as needed
    }
  }

  public void updatePassword(String username, String newPassword) {
    try {
      Connection connection = jdbc.getConnection();
      String query = "{CALL UpdatePassword(?, ?)}";
      try (CallableStatement callableStatement = connection.prepareCall(query)) {
        callableStatement.setString(1, username);
        callableStatement.setString(2, newPassword);
        callableStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle exceptions as needed
    }
  }

  public void updateAddress(String username, String newAddress) {
    try {
      Connection connection = jdbc.getConnection();
      String query = "{CALL UpdateAddress(?, ?)}";
      try (CallableStatement callableStatement = connection.prepareCall(query)) {
        callableStatement.setString(1, username);
        callableStatement.setString(2, newAddress);
        callableStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle exceptions as needed
    }
  }

  public boolean removeUserByUsername(String username) {
    System.out.println(username);
    try (Connection connection = jdbc.getConnection();
         PreparedStatement statement = connection.prepareStatement("CALL RemoveUserByUsername(?)")) {
      statement.setString(1, username);
      statement.executeUpdate();
      return true;  // Return true if the deletion was successful
    } catch (SQLException e) {
      e.printStackTrace(); // Handle the exception appropriately
      return false;  // Return false if the deletion failed
    }
  }

  public Gym getGymByUser(String username) {
    try {
      Connection connection = jdbc.getConnection();
      CallableStatement callableStatement = connection.prepareCall("{call get_gym_by_user(?)}");
      callableStatement.setString(1, username);

      ResultSet resultSet = callableStatement.executeQuery();

      if (resultSet.next()) {
        // Create a Gym object with the retrieved data
        int gymId = resultSet.getInt("gym_id");
        String gymName = resultSet.getString("gym_name");
        String address = resultSet.getString("address");
        Time openingTime = resultSet.getTime("opening_time");
        Time closingTime = resultSet.getTime("closing_time");

        return new Gym(gymId, gymName, address, openingTime, closingTime);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null; // Return null if gym information is not found
  }

  public List<Workout> getWorkouts() {
    List<Workout> workouts = new ArrayList<>();

    try (Connection connection = jdbc.getConnection()) {
      CallableStatement callableStatement = connection.prepareCall("{call GetWorkoutsByUsername(?)}");
      callableStatement.setString(1, loggedInUsername);

      ResultSet resultSet = callableStatement.executeQuery();

      while (resultSet.next()) {
        int workoutId = resultSet.getInt("workout_id");
        String username = resultSet.getString("username");
        Date completionDate = resultSet.getDate("completion_date");
        String description = resultSet.getString("description");
        int duration = resultSet.getInt("duration");

        Workout workout = new Workout(workoutId, username, completionDate, description, duration);

        workouts.add(workout);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
      System.err.print("Closed");
    }

    return workouts;
  }

  public List<Exercise> getExercises(int workoutId) {
    List<Exercise> exercises = new ArrayList<>();

    try (Connection connection = jdbc.getConnection()) {
      CallableStatement callableStatement = connection.prepareCall("{call GetExercisesByWorkoutId(?)}");
      callableStatement.setInt(1, workoutId);

      ResultSet resultSet = callableStatement.executeQuery();

      while (resultSet.next()) {
        int exerciseId = resultSet.getInt("exercise_id");
        String exerciseName = resultSet.getString("exercise_name");
        String notes = resultSet.getString("notes");

        Exercise exercise = new Exercise(exerciseId, workoutId, exerciseName, notes);

        exercises.add(exercise);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }

    return exercises;
  }

  public List<ExerciseSet> getExerciseSets(int exerciseId) {
    List<ExerciseSet> exerciseSets = new ArrayList<>();

    try (Connection connection = jdbc.getConnection()) {
      CallableStatement callableStatement = connection.prepareCall("{call GetExerciseSetsByExerciseId(?)}");
      callableStatement.setInt(1, exerciseId);

      ResultSet resultSet = callableStatement.executeQuery();

      while (resultSet.next()) {
        int setId = resultSet.getInt("set_id");
        int workoutId = resultSet.getInt("workout_id");
        int sets = resultSet.getInt("sets");
        int reps = resultSet.getInt("reps");
        int weight = resultSet.getInt("weight");

        ExerciseSet exerciseSet = new ExerciseSet(setId, exerciseId, workoutId, sets, reps, weight);

        exerciseSets.add(exerciseSet);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }

    return exerciseSets;
  }

  public void addWorkout(Date completionDate, String description, int duration) {
    try (Connection connection = jdbc.getConnection()) {
      CallableStatement callableStatement = connection.prepareCall("{call AddWorkout(?, ?, ?, ?)}");
      callableStatement.setString(1, loggedInUsername);
      callableStatement.setDate(2, completionDate);
      callableStatement.setString(3, description);
      callableStatement.setInt(4, duration);

      callableStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }
  }

  public void addExercise(int workoutId, String exerciseName, String notes) {
    try (Connection connection = jdbc.getConnection()) {
      CallableStatement callableStatement = connection.prepareCall("{call AddExercise(?, ?, ?)}");
      callableStatement.setInt(1, workoutId);
      callableStatement.setString(2, exerciseName);
      callableStatement.setString(3, notes);

      callableStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }
  }

  public void addExerciseSet(int exerciseId, int workoutId, int sets, int reps, int weight) {
    try (Connection connection = jdbc.getConnection()) {
      CallableStatement callableStatement = connection.prepareCall("{call AddExerciseSet(?, ?, ?, ?, ?)}");
      callableStatement.setInt(1, exerciseId);
      callableStatement.setInt(2, workoutId);
      callableStatement.setInt(3, sets);
      callableStatement.setInt(4, reps);
      callableStatement.setInt(5, weight);

      callableStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }
  }

  public List<Post> getPostsForForum(String forumName) {
    List<Post> posts = new ArrayList<>();

    try (Connection connection = jdbc.getConnection()) {
      // Prepare the call to the stored procedure
      String callStatement = "{call GetPostsForForum(?)}";
      try (CallableStatement callableStatement = connection.prepareCall(callStatement)) {
        // Set the input parameter
        callableStatement.setString(1, forumName);

        // Execute the stored procedure
        ResultSet resultSet = callableStatement.executeQuery();

        // Process the result set
        while (resultSet.next()) {
          int postId = resultSet.getInt("post_id");
          int forumId = resultSet.getInt("forum_id");
          String creator = resultSet.getString("creator");
          Date postDate = resultSet.getDate("post_date");
          String postText = resultSet.getString("post");

          // Create a Post object with the retrieved information
          Post post = new Post(postId, forumId, creator, postDate, postText);
          posts.add(post);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }

    return posts;
  }

  public int getPostLikes(int postId) {
    int likesCount = 0;

    try (Connection connection = jdbc.getConnection();
         CallableStatement callableStatement = connection.prepareCall("{CALL GetLikesCountForPost(?, ?)}")) {

      callableStatement.setInt(1, postId);
      callableStatement.registerOutParameter(2, Types.INTEGER);

      callableStatement.execute();
      likesCount = callableStatement.getInt(2);

    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }

    return likesCount;
  }

  public void addPost(String forumName, String postText) {
    try (Connection connection = jdbc.getConnection()) {
      // Get the forum_id for the given forumName
      int forumId = getForumIdByName(connection, forumName);

      // Prepare the call to the stored procedure
      String callStatement = "{call AddPost(?, ?, ?, ?)}";
      try (CallableStatement callableStatement = connection.prepareCall(callStatement)) {
        // Set the input parameters
        callableStatement.setInt(1, forumId);
        callableStatement.setString(2, getLoggedInUsername()); // Assuming a method to get the logged-in user
        callableStatement.setDate(3, Date.valueOf(LocalDate.now()));
        callableStatement.setString(4, postText);

        // Execute the stored procedure
        callableStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }
  }

  private int getForumIdByName(Connection connection, String forumName) throws SQLException {
    int forumId = -1;

    // Implement the logic to retrieve the forum_id for the given forumName
    String query = "SELECT forum_id FROM forums WHERE forum_name = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, forumName);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        forumId = resultSet.getInt("forum_id");
      }
    }

    return forumId;
  }

  public void toggleLike(int postId) {
    try (Connection connection = jdbc.getConnection()) {
      // Prepare the call to the stored procedure
      String callStatement = "{call ToggleLike(?, ?)}";
      try (CallableStatement callableStatement = connection.prepareCall(callStatement)) {
        // Set the input parameters
        callableStatement.setString(1, getLoggedInUsername()); // Assuming a method to get the logged-in user
        callableStatement.setInt(2, postId);

        // Execute the stored procedure
        callableStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }
  }

  public List<String> getAllUsernames() {
    List<String> usernames = new ArrayList<>();

    try (Connection connection = jdbc.getConnection()) {
      String sql = "SELECT username FROM gym_users";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet resultSet = statement.executeQuery()) {
          while (resultSet.next()) {
            String username = resultSet.getString("username");
            usernames.add(username);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // Handle the exception appropriately
    }

    return usernames;
  }

  public boolean followUser(String followerUsername, String targetUsername) {
    try (Connection connection = jdbc.getConnection()) {
      String sql = "INSERT INTO user_follows_user (user1, user2) VALUES (?, ?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, followerUsername);
        statement.setString(2, targetUsername);
        statement.executeUpdate();
        return true; // Operation successful
      }
    } catch (SQLException e) {
      // Handle the exception appropriately
      return false; // Operation failed
    }
  }
}