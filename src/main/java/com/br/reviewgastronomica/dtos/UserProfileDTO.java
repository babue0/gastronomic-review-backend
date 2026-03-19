package com.br.reviewgastronomica.dtos;

import com.br.reviewgastronomica.domain.review.Review;

import java.util.List;

public record UserProfileDTO (
        String name,
        Long totalReviews,
        Double averageRating,
        String favoriteCategory,
        List<Review> myReviews
){
}
