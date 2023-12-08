package Model;

public class ExerciseSet {
  private int setId;
  private int exerciseId;
  private int workoutId;

  public int getSetId() {
    return setId;
  }

  public int getExerciseId() {
    return exerciseId;
  }

  public int getWorkoutId() {
    return workoutId;
  }

  public int getSet() {
    return set;
  }

  public int getReps() {
    return reps;
  }

  public int getWeight() {
    return weight;
  }

  private int set;
  private int reps;
  private int weight;

  public ExerciseSet(int setId, int exerciseId, int workoutId, int set, int reps, int weight) {
    this.setId = setId;
    this.exerciseId = exerciseId;
    this.workoutId = workoutId;
    this.set = set;
    this.reps = reps;
    this.weight = weight;
  }
}
