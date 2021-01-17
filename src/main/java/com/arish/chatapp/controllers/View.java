package com.arish.chatapp.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class View {
	
	@RequestMapping("/resetPassword")
	String resetPassword(@RequestParam String username, @RequestParam String token, Map<String, Object> model) {		
		model.put("username", username);
		model.put("token", token);
		return "resetPassword";
	}
}
