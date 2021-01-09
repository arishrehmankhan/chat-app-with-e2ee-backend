package com.arish.chatapp.controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.arish.chatapp.models.User;
import com.arish.chatapp.services.UserService;
import com.arish.chatapp.utils.JwtUtil;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/register")
    public HashMap<String, Object> register(@RequestBody User user) throws Exception {

        HashMap<String, Object> response = new HashMap<>();

        final String username = user.getUsername();
        final String password = user.getPassword();
        final String firstname = user.getFirstname();
        final String lastname = user.getLastname();

        final String validation = validate(username, password, firstname);

        if (validation.equals("validated")) {

            if (!userService.userExists(username)) {

                // hashing password before saving it to database
                String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                user.setPassword(hashedPassword);

                // saving user
                userService.saveUser(user);

                response.put("response", "Success");
                response.put("message", "User Registered");

            } else {

                response.put("response", "Error");
                response.put("message", "User with this username already exists.");

            }

        } else {

            response.put("response", "Error");
            response.put("message", validation);

        }

        return response;
    }

    @PostMapping(value = "/login")
    public HashMap<String, Object> login(@RequestBody User user) throws Exception {

        HashMap<String, Object> response = new HashMap<>();

        final String username = user.getUsername();
        final String password = user.getPassword();

        User tempUser = userService.findByUsername(username);

        if (tempUser != null) {

            // check password
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), tempUser.getPassword());

            if (result.verified) {

                // Generating JWT
                String jwt = jwtUtil.generateToken(username);

                response.put("response", "Success");
                response.put("jwt", jwt);

            } else {

                response.put("response", "Error");
                response.put("message", "Incorrect username or password");

            }

        } else {

            response.put("response", "Error");
            response.put("message", "Incorrect username or password");

        }

        return response;
    }

    @PostMapping(value = "/forgotPassword")
    public HashMap<String, Object> forgotPassword(@RequestBody User user) throws Exception {

        HashMap<String, Object> response = new HashMap<>();

        final String username = user.getUsername();

        User tempUser = userService.findByUsername(username);

        if (tempUser != null) {
            String token = jwtUtil.generateToken(username);
            System.out.println("under forgotten password if server : " + token);
            user.setForgotPasswordToken(token);
            // saving user
            userService.saveUser(user);
            response.put("response", "Successful");
            response.put("token", user.getForgotPasswordToken());
            response.put("message", "Token added successfully to database");

        } else {

            response.put("response", "Error");
            response.put("message", "Email Not Registered");

        }

        return response;
    }

    private String validate(String username, String password, String firstname) throws Exception {

        if (username == null || username.isEmpty()) {
            return "Username missing.";
        }

        if (password == null || password.isEmpty()) {
            return "Password missing.";
        }

        if (username.length() < 8) {
            return "Username must be more than 7 characters in length.";
        }

        if (password.length() > 15 || password.length() < 8) {
            return "Password must be less than 16 and more than 7 characters in length.";
        }

        if (password.indexOf(username) > -1) {
            return "Password must not be same as user name.";
        }

        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            return "Password must contain atleast one upper case alphabet.";
        }

        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            return "Password must contain atleast one lower case alphabet.";
        }

        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            return "Password must contain atleast one number.";
        }

        String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
        if (!password.matches(specialChars)) {
            return "Password should contain atleast one special character.";
        }

        if (firstname == null || firstname.isEmpty()) {
            return "First Name missing";
        }

        return "validated";
    }

}
