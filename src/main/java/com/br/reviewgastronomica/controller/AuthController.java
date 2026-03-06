package com.br.reviewgastronomica.controller;

import com.br.reviewgastronomica.domain.user.User;
import com.br.reviewgastronomica.dtos.AuthDTO;
import com.br.reviewgastronomica.dtos.RegisterDTO;
import com.br.reviewgastronomica.repository.UserRepository;
import com.br.reviewgastronomica.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository repository;

  @Autowired
  private TokenService tokenService;


  @PostMapping("/login")
  public ResponseEntity login(@RequestBody AuthDTO data){
    var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var token = tokenService.generateToken((User) auth.getPrincipal());

    return ResponseEntity.ok(token);
  }


  @PostMapping("/register")
  public ResponseEntity register (@RequestBody RegisterDTO data){
    if (this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    User newUser = new User(data.name(), data.email(), encryptedPassword);

    this.repository.save(newUser);

    return ResponseEntity.ok().build();
  }
}
