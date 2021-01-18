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
import com.arish.chatapp.javamail.JavaMail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                final String token = getAlphaNumericString(10);
                user.setEmailVerificationToken(token);
                // saving user
                userService.saveUser(user);
                JavaMail mail = new JavaMail();
                String status = mail.sendMail(username, "Talkpad reset password",
                        "Please click this link to verify email :  http://localhost:8080/verifyEmail?username="
                        + username + "&token=" + token);
                if (status.equals("Successful")) {
                    response.put("response", "Success");
                    response.put("message", "Mail verification link sent to your email.");
                } else {
                    response.put("response", "Error");
                    response.put("message", "Error in sending mail");
                }

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
            if (tempUser.getEmailVerified() == true) {
                // check password
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), tempUser.getPassword());

                if (result.verified) {
                    // Generating JWT
                    String jwt = jwtUtil.generateToken(username);

                    response.put("response", "Success");
                    response.put("message", "Login successsful");
                    response.put("jwt", jwt);

                } else {

                    response.put("response", "Error");
                    response.put("message", "Incorrect username or password");

                }
            } else {
                response.put("response", "Error");
                response.put("message", "Please verify your email first !!");
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
            String token = getAlphaNumericString(10);
            tempUser.setForgotPasswordToken(token);
            // saving user
            userService.saveUser(tempUser);
            JavaMail mail = new JavaMail();
            String status = mail.sendMail(username, "Talkpad reset password",
                    "Please click this link :  http://localhost:8080/resetPassword?username="
                    + username + "&token=" + token);

            if (status.equals("Successful")) {
                response.put("response", "Successful");
                response.put("message", "Mail sent successfully");
            } else {
                response.put("response", "Error");
                response.put("message", "Error in sending mail");
            }

        } else {
            response.put("response", "Error");
            response.put("message", "Email Not Registered");

        }

        return response;
    }

    @PostMapping(value = "/resetPassword")
    public HashMap<String, String> resetPassword(@RequestParam String username,
            @RequestParam String token, @RequestParam String password) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        User tempUser = userService.findByUsername(username);
        String passwordValidation = validatePassword(password);
        if (tempUser != null) {
            if (passwordValidation.equals("validated")) {
                if (tempUser.getForgotPasswordToken().equals(token)) {
                    // hashing password before saving it to database
                    String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                    tempUser.setPassword(hashedPassword);
                    // saving user
                    userService.saveUser(tempUser);
                    map.put("status", "changed");
                    map.put("message", "Successfully changed password");
                    return map;
                } else {
                    map.put("status", "Error");
                    map.put("message", "Something went wrong");
                    return map;
                }
            } else {
                map.put("status", "Error");
                map.put("message", "Password criteria incorrect");
                return map;
            }

        } else {

            map.put("status", "userNotExist");
            map.put("message", "User not exist !!");
            return map;
        }
    }

    @GetMapping(value = "/verifyEmail")
    public String verifyEmail(@RequestParam String username, @RequestParam String token) throws Exception {
        User tempUser = userService.findByUsername(username);

        if (tempUser != null) {
            if (tempUser.getEmailVerificationToken().equals(token)) {
                tempUser.setEmailVerified(true);
                // saving user
                userService.saveUser(tempUser);
                return "Email verified successfully";
            } else {
                return "Unauthorised verification";
            }

        } else {
            return "User not exist";
        }
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

    private String validatePassword(String password) throws Exception {

        if (password == null || password.isEmpty()) {
            return "Password missing.";
        }

        if (password.length() > 15 || password.length() < 8) {
            return "Password must be less than 16 and more than 7 characters in length.";
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

        return "validated";
    }

    // function to generate a random string of length n 
    private String getAlphaNumericString(int n) {
        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb 
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
