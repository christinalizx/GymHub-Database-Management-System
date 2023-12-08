package Model;

import java.sql.Time;

public class Gym {
  private int gymId;
  private String gymName;
  private String address;
  private Time openingTime;
  private Time closingTime;

  // Constructor with parameters
  public Gym(int gymId, String gymName, String address, Time openingTime, Time closingTime) {
    this.gymId = gymId;
    this.gymName = gymName;
    this.address = address;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
  }

  // Getter and setter methods for attributes
  public int getGymId() {
    return gymId;
  }

  public void setGymId(int gymId) {
    this.gymId = gymId;
  }

  public String getGymName() {
    return gymName;
  }

  public void setGymName(String gymName) {
    this.gymName = gymName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Time getOpeningTime() {
    return openingTime;
  }

  public void setOpeningTime(Time openingTime) {
    this.openingTime = openingTime;
  }

  public Time getClosingTime() {
    return closingTime;
  }

  public void setClosingTime(Time closingTime) {
    this.closingTime = closingTime;
  }

  @Override
  public String toString() {
    return "Gym{" +
            "gymId=" + gymId +
            ", gymName='" + gymName + '\'' +
            ", address='" + address + '\'' +
            ", openingTime=" + openingTime +
            ", closingTime=" + closingTime +
            '}';
  }
}