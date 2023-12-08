package Model;

import java.sql.Date;

public class Workout {
  private int workoutId;
  private String username;
  private Date completionDate;
  private String description;
  private int duration;

  public Workout(int workoutId, String username, Date completionDate, String description, int duration) {
    this.workoutId = workoutId;
    this.username = username;
    this.completionDate = completionDate;
    this.description = description;
    this.duration = duration;
  }

  public int getWorkoutId() {
    return workoutId;
  }

  public String getUsername() {
    return username;
  }

  public Date getCompletionDate() {
    return completionDate;
  }

  public String getDescription() {
    return description;
  }

  public int getDuration() {
    return duration;
  }
}
