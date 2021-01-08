package com.arish.chatapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arish.chatapp.models.Message;
import com.arish.chatapp.repositories.MessageRepository;

@Service
public class MessageService {
	@Autowired
	private MessageRepository messageRepository;
	
	public Message saveMessage(Message message) {
		return messageRepository.save(message);
	}
	
	public List<Message> getMessages(Long sender, Long recipient) {
		return messageRepository.findBySenderEqualsAndRecipientEqualsOrRecipientEqualsAndSenderEqualsOrderByCreatedAt(sender, recipient, sender, recipient);
	}
}
