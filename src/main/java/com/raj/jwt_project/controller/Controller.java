package com.raj.jwt_project.controller;

import com.raj.jwt_project.Dto.LoginRequest;
import com.raj.jwt_project.Dto.ResponseUser;
import com.raj.jwt_project.Dto.UserRequest;
import com.raj.jwt_project.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    @Autowired
    private UserService userService;


    @GetMapping("/")
    public String homePage(){
        return "Hello World";
    }

    @PostMapping("/api/signup")
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody UserRequest user){
        ResponseUser responseUser = userService.createUser(user);
        return ResponseEntity.ok(responseUser);
    }
    @GetMapping("/api/getuser")
    public ResponseEntity<ResponseUser> getUser(@RequestHeader String email){
        System.out.println(email);
        ResponseUser user = userService.getUser(email);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginDto){ // ? is use, so we can send any type of object
        System.out.println("inside login");
        return userService.LoginService(loginDto);
    }


}
