package com.sanderdsz.grocery.infrastructure.security;

import com.sanderdsz.grocery.domain.jwt.JwtHelper;
import com.sanderdsz.grocery.domain.model.User;
import com.sanderdsz.grocery.infrastructure.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Log4j2
public class AccessTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {

            Optional<String> accessToken = parseAccessToken(request);

            if(accessToken.isPresent() && jwtHelper.validateAccessToken(accessToken.get())) {

                logger.info("ACCESS_TOKEN: " + accessToken.get());

                String userId = jwtHelper.getUserIdFromAccessToken(accessToken.get());

                User user = userService.findById(Long.parseLong(userId));

                logger.info("USER_NAME: " + user.getEmail());

                UsernamePasswordAuthenticationToken passwordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                passwordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
            }
        } catch (Exception e) {
            log.error("Cannot authenticate: ", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Parse the Access Token from the request header and removes the Bearer from it
     * @param request
     * @return String (Access Token)
     */
    private Optional<String> parseAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.replace("Bearer ", ""));
        }

        return Optional.empty();
    }
}
