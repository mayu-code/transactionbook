package com.transaction.book.jwtSecurity;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.transaction.book.entities.JwtToken;
import com.transaction.book.services.serviceImpl.JwtTokenServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidator extends OncePerRequestFilter {


    private final JwtTokenServiceImpl jwtTokenServiceImpl;

    public JwtValidator(JwtTokenServiceImpl jwtTokenServiceImpl) {
        this.jwtTokenServiceImpl = jwtTokenServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = request.getHeader(JwtConstants.JWT_HEADER);
        JwtToken token = this.jwtTokenServiceImpl.getTokenByToken(jwt.substring(7));

        if (jwt != null) {
            try {
                if(!token.isActive()){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token has expired, please login again\"}");
                return;
                }
                String mobileNo = JwtProvider.getEmailByJwt(jwt);
                String role = JwtProvider.getRoleByJwt(jwt);
                Authentication authentication = new UsernamePasswordAuthenticationToken(mobileNo, null,
                        List.of(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token has expired, please login again\"}");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid token, authentication failed\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

}
