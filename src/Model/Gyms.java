package Model;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "gyms")
public class Gyms {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "gym_id")
  private int gymId;
  @Column(name = "gym_name", nullable = false)
  private String gymName;
  @Column(name = "address", nullable = false, unique = true)
  private String address;
  @Column(name = "opening_time")
  private Time openingTime;
  @Column(name = "closing_time")
  private Time closingTime;

  // Default constructor
  public Gyms() {}

  // Constructor with parameters
  public Gyms(int gymId, String gymName, String address, Time openingTime, Time closingTime) {
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
    return "Gyms{" +
            "gymId=" + gymId +
            ", gymName='" + gymName + '\'' +
            ", address='" + address + '\'' +
            ", openingTime=" + openingTime +
            ", closingTime=" + closingTime +
            '}';
  }
}
