package com.arish.chatapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.arish.chatapp.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public User findByUsername(String username);
	
	public List<User> findByFirstnameStartingWithOrLastnameStartingWith(String firstname, String lastname);
	
}
