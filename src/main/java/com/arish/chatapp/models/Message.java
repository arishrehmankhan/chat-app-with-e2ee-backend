package com.arish.chatapp.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import com.sun.istack.NotNull;

@Entity 
public class Message {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@NotNull 
	private Long sender;
	@NotNull
	private Long recipient;
	@NotNull
	private String message;
	@CreationTimestamp
    private LocalDateTime createdAt;
	
	public Message() {}
	
	public Message(Long sender, Long recipient, String message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
	}

	public Long getSender() {
		return sender;
	}
	public void setSender(Long sender) {
		this.sender = sender;
	}
	public Long getRecipient() {
		return recipient;
	}
	public void setRecipient(Long recipient) {
		this.recipient = recipient;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
