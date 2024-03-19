package org.launchcode.techjobsauth.controllers;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.launchcode.techjobsauth.models.User;
import org.launchcode.techjobsauth.models.data.UserRepository;
import org.launchcode.techjobsauth.models.dto.LoginFormDTO;
import org.launchcode.techjobsauth.models.dto.RegistrationFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

// 1. Create an AuthenticationController
@Controller
public class AuthenticationController {

    //Include an autowired UserRepository
    @Autowired
    UserRepository userRepository;

    // 2. Add session handling utilities. This includes:

    // 2.1. A static final variable for the session key
    private static final String userSessionKey = "user";

    //2.2. A method to set the user in the session
    private static void setUserInSession(HttpSession session, User user) {
        session.setAttribute(userSessionKey, user.getId());
    }

    //2.3. A method to get the user information from the session
    public User getUserFromSession(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(userSessionKey);
        if (userId == null) {
            return null;
        }
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return null;
        }
        return userOptional.get();
    }

    // 4. Handle the registration data

    // 1. Add a GET handler in AuthenticationController to display a registration form.
    @GetMapping("/register")
    public String displayRegistrationForm(Model model) {
        model.addAttribute(new RegistrationFormDTO());
        return "register"; //assuming there's a register.html template

    }

    // 3. Create a POST handler in AuthenticationController to process the form
    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute @Valid RegistrationFormDTO registrationFormDTO,
                                          Errors errors, HttpServletRequest request, Model model) {

        //3.1. If the form has validation errors, re-render the registration form with a useful message.
        if (errors.hasErrors()) {
            return "register";
        }
        //3.2.If the username is already tied to a user, add a fitting error message and re-render the form.

        User existingUser = userRepository.findByUsername(registrationFormDTO.getUsername());

        if (existingUser != null) {
            errors.rejectValue("username",
                    "username.alreadyExists",
                    "A user that username already exists."
            );
            return "register";
        }
      // 3.3. If the two form fields for passwords do not match, add an error message and re-render the form.

        String password = registrationFormDTO.getPassword();
        String verifyPassword = registrationFormDTO.getVerifyPassword();

        if(!password.equals(verifyPassword)){
            errors.rejectValue("verifyPassword",
                    "password.mismatch",
                    "Passwords do not match."
            );
            return "register";
        }
    //3.4 If none of the above conditions are met,
        //Create a new user with the form data,
        //Save the user to the database,
        //Create a new user session,
        //Redirect to the home page.

        User newUser = new User(registrationFormDTO.getUsername(),registrationFormDTO.getPassword());
        userRepository.save(newUser);
        setUserInSession(request.getSession(), newUser);
        return "redirect:"; //Redirect to the home page.
    }

    //5. Handle the login data.

    //5.1 Repeat steps 1 + 2 for handling the registration data, this time with the login information.
    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute(new LoginFormDTO());
        return "login"; //assuming there's a login.html template
    }
    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO,
                                          Errors errors, HttpServletRequest request) {


        if (errors.hasErrors()) {
            return "login";
        }
        //3.2.If the username is already tied to a user, add a fitting error message and re-render the form.

        User existingUser = userRepository.findByUsername(loginFormDTO.getUsername());

        String password = loginFormDTO.getPassword();

        if(existingUser == null || !existingUser.isMatchingPassword(password)){
            errors.rejectValue("password",
                    "password.invalid",
                    "Credentials invalid. Please try again"
            );
            return "login";
        }
        setUserInSession(request.getSession(), existingUser);
        return "redirect:"; //Redirect to the home page.
    }

    // 6. Handle logging out.

    // 6.1. Still in AuthenticationController, create a GET handler method for a path to logout.
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }


}


