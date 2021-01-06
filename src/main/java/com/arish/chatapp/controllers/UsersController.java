package com.arish.chatapp.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.arish.chatapp.models.User;
import com.arish.chatapp.services.UserService;
import com.arish.chatapp.utils.JwtUtil;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
public class UsersController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@GetMapping(value = "/get-users")
	public HashMap<String, Object> getUsers() throws Exception {
		
		HashMap<String, Object> response = new HashMap<>();
		
		List<User> users = userService.getAll();
		response.put("users", users);
		
		return response;
	}

}
