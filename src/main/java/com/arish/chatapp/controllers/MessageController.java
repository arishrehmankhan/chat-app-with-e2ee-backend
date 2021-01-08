package com.arish.chatapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.arish.chatapp.models.Message;
import com.arish.chatapp.models.User;
import com.arish.chatapp.services.MessageService;

@RestController
public class MessageController {
	
	@Autowired
	private MessageService messageService;

	@PostMapping(value = "/send-message")
	public HashMap<String, Object> sendMessage(@RequestBody Message message) throws Exception {
		
		HashMap<String, Object> response = new HashMap<>();
		
		messageService.saveMessage(message);
		
		response.put("response", "success");
		response.put("message", "message_saved");
		
		return response;
	}
	
	@PostMapping(value = "/get-messages")
	public HashMap<String, Object> searchUser(@RequestBody Map<String, String> request) throws Exception {
		
		HashMap<String, Object> response = new HashMap<>();

		Long sender = Long.parseLong(request.get("sender"));
		Long recipient = Long.parseLong(request.get("recipient"));
		
		List<Message> messages = messageService.getMessages(sender, recipient);
		
		if (messages != null && messages.size() > 0) {
			response.put("response", "Success");
			response.put("messages", messages);
		} else {
			response.put("response", "Error");
			response.put("message", "No messages found");
		}
		
		return response;
	}
		
}
