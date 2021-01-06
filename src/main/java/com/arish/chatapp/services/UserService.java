package com.arish.chatapp.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arish.chatapp.models.User;
import com.arish.chatapp.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	public Boolean userExists(String username) {
		return userRepository.findByUsername(username) != null;
	}
	
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public List<User> getAll() {
		return userRepository.findAll();
	}
	
}

