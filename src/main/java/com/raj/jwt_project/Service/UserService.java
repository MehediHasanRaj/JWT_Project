package com.raj.jwt_project.Service;


import com.raj.jwt_project.Dto.LoginRequest;
import com.raj.jwt_project.Dto.LoginResponse;
import com.raj.jwt_project.Dto.ResponseUser;
import com.raj.jwt_project.Dto.UserRequest;
import com.raj.jwt_project.Entity.User;
import com.raj.jwt_project.config.MyUserDetails;
import com.raj.jwt_project.config.jwt.JwtUtils;
import com.raj.jwt_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseUser createUser(UserRequest user) {
        // we need to check that user exist or not
        if(userRepository.existsByEmail(user.getEmail())){
            return ResponseUser.builder()
                    .email(user.getEmail())
                    .message("Email already exists")
                    .build();
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());

        // we need encode password before store in database
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(newUser);
        return ResponseUser.builder()
                .email(user.getEmail())
                .message("User created")
                .build();
    }

    public ResponseUser getUser(String email) {

        if(!userRepository.existsByEmail(email)){
            return ResponseUser.builder()
                    .email(email)
                    .message("User not found")
                    .build();
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseUser.builder()
                .email(user.getEmail())
                .message("password: "+ user.getPassword())
                .build();
    }

    public ResponseEntity<?> LoginService(LoginRequest loginDto){

        Authentication authentication; // this gives the authentication object, we will check the authentication by Username
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
        }
        catch(AuthenticationException exception){
            Map<String, Object> mp = new HashMap<>();
            mp.put("message", "Bad Credentials");
            mp.put("status", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mp);

        }



        //we will generate token, as authenticated
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal(); // this method return the UserDetails object
        String token = jwtUtils.generateTokenFromUsername(userDetails);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setEmail(loginDto.getEmail());
        return ResponseEntity.ok(loginResponse);

    }
}
