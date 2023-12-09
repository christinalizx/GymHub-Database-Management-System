package Model;

import java.util.Date;

public class Post {
  private int postId;
  private int forumId;
  private String creator;
  private Date postDate;
  private String postText;

  public Post(int postId, int forumId, String creator, Date postDate, String postText) {
    this.postId = postId;
    this.forumId = forumId;
    this.creator = creator;
    this.postDate = postDate;
    this.postText = postText;
  }

  // Getters and setters

  public int getPostId() {
    return postId;
  }

  public void setPostId(int postId) {
    this.postId = postId;
  }

  public int getForumId() {
    return forumId;
  }

  public void setForumId(int forumId) {
    this.forumId = forumId;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Date getPostDate() {
    return postDate;
  }

  public void setPostDate(Date postDate) {
    this.postDate = postDate;
  }

  public String getPostText() {
    return postText;
  }

  public void setPostText(String postText) {
    this.postText = postText;
  }
}
