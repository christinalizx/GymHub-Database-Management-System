package Model;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;


@Entity
@Table(name = "gym_users")
public class GymUsers {
  private String username;
  private String password;
  private String address;
  @Column(name = "gym_id")
  private int gymId;

  /** Default constructor
   */
  public GymUsers(){};

  /**
   * Constructor with params.
   */
  public GymUsers(String username, String password, String address, int gymId){
    this.username = username;
    this.password = password;
    this.address = address;
    this.gymId = gymId;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getGymId() {
    return gymId;
  }

  public void setGymId(int gymId) {
    this.gymId = gymId;
  }
}
