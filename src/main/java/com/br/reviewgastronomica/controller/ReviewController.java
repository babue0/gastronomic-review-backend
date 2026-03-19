package com.br.reviewgastronomica.controller;

import com.br.reviewgastronomica.domain.review.Review;
import com.br.reviewgastronomica.domain.user.User;
import com.br.reviewgastronomica.dtos.UserProfileDTO;
import com.br.reviewgastronomica.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
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
  public ResponseEntity<List<Review>> getFeed(@RequestParam(required = false) String category, @RequestParam(required = false) String restaurantName){
    List<Review> feed;

    if(category != null && !category.isEmpty()){
      feed = this.repository.findByCategoryOrderByPostedAtDesc(category);
    } else if (restaurantName != null && !restaurantName.isEmpty()) {
      feed = this.repository.findByRestaurantNameContainingIgnoreCaseOrderByPostedAtDesc(restaurantName);
    } else{
      feed = this.repository.findAllByOrderByPostedAtDesc();
    }
    return ResponseEntity.ok(feed);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity deleteReview(@PathVariable Long id){
    try{
      User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      Review review = repository.findById(id).orElse(null);
      if (review == null){
        return ResponseEntity.notFound().build();
      }

      if (!review.getUser().getId().equals(loggedUser.getId())){
        return ResponseEntity.status(403).body("Você não tem permissão para excluir este post.");
      }

      if (review.getImagePath() != null){
        Path imagePath = Paths.get(UPLOAD_DIR).resolve(review.getImagePath());
        Files.deleteIfExists(imagePath);
      }

      this.repository.delete(review);
      return ResponseEntity.ok("Post excluido com sucesso!");

    } catch (Exception e){
      e.printStackTrace();
      return ResponseEntity.badRequest().body("Erro ao excluir o post");
    }
  }


  @PostMapping("/{id}/like")
  public ResponseEntity toggleLike(@PathVariable Long id){
    try{
      User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Review review = repository.findById(id).orElse(null);
      if (review == null) return ResponseEntity.notFound().build();

      boolean alreadyLiked = review.getLikedBy().stream()
              .anyMatch(u -> u.getId().equals(loggedUser.getId()));

      if (alreadyLiked){
        review.getLikedBy().removeIf(u -> u.getId().equals(loggedUser.getId()));
      } else {
        review.getLikedBy().add(loggedUser);
      }

      this.repository.save(review);
      return ResponseEntity.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body("Erro ao processar like");
    }
  }



  @GetMapping("/me")
  public ResponseEntity<UserProfileDTO> getMyProfile(){

    User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = loggedUser.getId();

    Long total = repository.countByUserId(userId);
    Double avg = repository.getAverageRatingByUserId(userId);
    String favCategory = repository.getFavoriteCategoryByUserId(userId);
    List<Review> myReviews = repository.findByUserIdOrderByPostedAtDesc(userId);

    Double finalAvg = (avg != null) ? Math.round(avg * 10.0) / 10.0 : 0.0;
    String finalFav = (favCategory != null) ? favCategory : "Nenhuma";

    UserProfileDTO profile =
            new UserProfileDTO(loggedUser.getName(), total, finalAvg, finalFav, myReviews);

    return ResponseEntity.ok(profile);
  }

}
