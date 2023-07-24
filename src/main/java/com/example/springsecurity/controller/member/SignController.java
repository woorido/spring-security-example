package com.example.springsecurity.controller.member;

import com.example.springsecurity.member.SignService;
import com.example.springsecurity.member.dto.SignRequest;
import com.example.springsecurity.member.dto.SignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @PostMapping(value = "/login")
    public ResponseEntity<SignResponse> signin(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(signService.login(request), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> signup(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(signService.register(request), HttpStatus.OK);
    }

    @GetMapping("/user/get")
    public ResponseEntity<SignResponse> getUser(@RequestParam String email) throws Exception {
        System.out.println("email = " + email);
        return new ResponseEntity<>(signService.getMember(email), HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<SignResponse> getUser2(@RequestParam String email) throws Exception {
        System.out.println("email = " + email);
        return new ResponseEntity<>(signService.getMember(email), HttpStatus.OK);
    }

    @PostMapping("/get")
    public ResponseEntity<SignResponse> getUser3(@RequestParam String email) throws Exception {
        System.out.println("email = " + email);
        return new ResponseEntity<>(signService.getMember(email), HttpStatus.OK);
    }

    @GetMapping("/admin/get")
    public ResponseEntity<SignResponse> getUserForAdmin(@RequestParam String email) throws Exception {
        return new ResponseEntity<>(signService.getMember(email), HttpStatus.OK);
    }
}
