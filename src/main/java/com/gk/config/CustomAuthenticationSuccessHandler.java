package com.gk.config;

import com.gk.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Lazy
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private ObjectProvider<UserService> userServiceProvider;

    public CustomAuthenticationSuccessHandler() {
        setDefaultTargetUrl("/students/dashboard");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        try {
            if (authentication != null && authentication.getName() != null) {
                userServiceProvider.getObject().updateLastLogin(authentication.getName());
            }
            clearAuthenticationAttributes(request);
            super.onAuthenticationSuccess(request, response, authentication);
        } catch (Exception e) {
            logger.error("Error in authentication success handler", e);
            throw e;
        }
    }
}
