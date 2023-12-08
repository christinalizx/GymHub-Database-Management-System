package Model;

public class Exercise {
  private int exerciseId;
  private int workoutId;
  private String exercise_name;
  private String notes;

  public Exercise(int exerciseId, int workoutId, String exercise_name, String notes) {
    this.exerciseId = exerciseId;
    this.workoutId = workoutId;
    this.exercise_name = exercise_name;
    this.notes = notes;
  }

  public int getExerciseId() {
    return exerciseId;
  }

  public int getWorkoutId() {
    return workoutId;
  }

  public String getExercise_name() {
    return exercise_name;
  }

  public String getNotes() {
    return notes;
  }
}
