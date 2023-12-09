package Model;

public class ExerciseSet {
  private int setId;
  private int exerciseId;
  private int workoutId;
  private int sets;
  private int reps;
  private int weight;

  public ExerciseSet(int setId, int exerciseId, int workoutId, int sets, int reps, int weight) {
    this.setId = setId;
    this.exerciseId = exerciseId;
    this.workoutId = workoutId;
    this.sets = sets;
    this.reps = reps;
    this.weight = weight;
  }

  public int getSetId() {
    return setId;
  }

  public int getExerciseId() {
    return exerciseId;
  }

  public int getWorkoutId() {
    return workoutId;
  }

  public int getSets() {
    return sets;
  }

  public int getReps() {
    return reps;
  }

  public int getWeight() {
    return weight;
  }

  @Override
  public String toString() {
    return weight + ": " + sets + "x" + reps;
  }
}
