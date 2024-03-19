package org.launchcode.techjobsauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.launchcode.techjobsauth.controllers.AuthenticationController;
import org.launchcode.techjobsauth.models.User;
import org.launchcode.techjobsauth.models.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthenticationFilter implements HandlerInterceptor {

    @Autowired
    //UserRepository userRepository;
    AuthenticationController authenticationController;

    private static final List<String> whitelist = Arrays.asList("/logout","/login","/register","/css");

    private static boolean isWhitelisted(String path){
        for(String pathRoot: whitelist){
            if(path.startsWith(pathRoot)){
                return true;
            }
        }
        return false;
    }

    // 3. Add a preHandle method.
    //This must override the inherited method of the same name.
    //Grab the session information from the request object.
    //Query the session data for a user.
    //If a user exists, return true. Otherwise, redirect to the login page and return false.
    @Override
    public boolean preHandle(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler) throws IOException{

        if(isWhitelisted(request.getRequestURI())){
            return true;
        }
        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        //If a user exists, return true. Otherwise, redirect to the login page and return false.
        if(user != null){
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }

}
