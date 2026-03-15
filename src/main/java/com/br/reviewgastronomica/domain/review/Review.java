package com.br.reviewgastronomica.domain.review;

import com.br.reviewgastronomica.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "reviews")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String restaurantName;
  private String category;
  private Integer rating;

  @Column(columnDefinition = "TEXT")
  private String comment;

  @ManyToMany
  @JoinTable(
          name = "review_likes",
          joinColumns = @JoinColumn(name = "review_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  @com.fasterxml.jackson.annotation.JsonIgnore
  private Set<User> likedBy = new HashSet<>();

  private String imagePath;

  private LocalDateTime postedAt;

  public Review() {
    this.postedAt = LocalDateTime.now();
  }

  public Long getId() {
    return Id;
  }

  public void setId(Long id) { this.Id = id; }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public void setRestaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public LocalDateTime getPostedAt() {
    return postedAt;
  }

  public void setPostedAt(LocalDateTime postedAt) {
    this.postedAt = postedAt;
  }

  public int getLikeCount(){
    return this.likedBy != null ? this.likedBy.size() : 0;
  }

  public Set<String> getLikedByEmails(){
    if (this.likedBy == null) return new HashSet<>();

    return this.likedBy.stream().map(User::getEmail).collect(Collectors.toSet());
  }

  public Set<User> getLikedBy(){return likedBy;}
  public void setLikedBy(Set<User> likedBy) {this.likedBy = likedBy;}
}
