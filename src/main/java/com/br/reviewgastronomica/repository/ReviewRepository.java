package com.br.reviewgastronomica.repository;

import com.br.reviewgastronomica.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findAllByOrderByPostedAtDesc();

  List<Review> findByCategoryOrderByPostedAtDesc(String category);

  List<Review> findByRestaurantNameContainingIgnoreCaseOrderByPostedAtDesc(String restaurantName);


  List<Review> findByUserIdOrderByPostedAtDesc(Long userId);

  Long countByUserId(Long userId);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.user.id = :userId")
  Double getAverageRatingByUserId(@Param("userId") Long userId);

  @Query(value = "SELECT category FROM reviews WHERE user_id = :userId GROUP BY category ORDER BY COUNT(id) DESC LIMIT 1", nativeQuery = true)
  String getFavoriteCategoryByUserId(@Param("userId") Long userId);
}
