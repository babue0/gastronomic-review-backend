package com.br.reviewgastronomica.controller;

import com.br.reviewgastronomica.domain.review.Review;
import com.br.reviewgastronomica.domain.user.User;
import com.br.reviewgastronomica.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

  @Autowired
  private ReviewRepository repository;

  private final String UPLOAD_DIR = "uploads/";

  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity createReview(@RequestParam("restaurantName") String restaurantName,
                                     @RequestParam("category") String category,
                                     @RequestParam("rating") Integer rating,
                                     @RequestParam("comment") String comment,
                                     @RequestParam(value = "image", required = false) MultipartFile image){

    try{
      User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      Review newReview = new Review();
      newReview.setUser(loggedUser);
      newReview.setRestaurantName(restaurantName);
      newReview.setCategory(category);
      newReview.setRating(rating);
      newReview.setComment(comment);

      if(image != null && !image.isEmpty()){
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
          Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(image.getInputStream(), filePath);

        newReview.setImagePath(fileName);
      }

      this.repository.save(newReview);
      return ResponseEntity.ok("Post publicado com sucesso!");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body("erro ao publicar o post");
    }
  }



  @GetMapping
  public ResponseEntity<List<Review>> getFeed(){
    List<Review> feed =this.repository.findAllByOrderByPostedAtDesc();
    return ResponseEntity.ok(feed);
  }
}
