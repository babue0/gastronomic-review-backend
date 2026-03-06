package com.br.reviewgastronomica.repository;

import com.br.reviewgastronomica.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  List<Review> findAllByOrderByPostedAtDesc();
}
