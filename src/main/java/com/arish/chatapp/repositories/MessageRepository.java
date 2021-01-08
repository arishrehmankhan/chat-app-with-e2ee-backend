package com.arish.chatapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arish.chatapp.models.Message;
import com.arish.chatapp.models.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	public List<Message> findBySenderEqualsAndRecipientEqualsOrRecipientEqualsAndSenderEqualsOrderByCreatedAt(Long sender, Long recipient, Long sender1, Long recipient1);
	
}
