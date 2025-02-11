package com.transaction.book.jwtSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.transaction.book.entities.User;
import com.transaction.book.services.serviceImpl.UserServiceImpl;

@Service
public class CustomUserDetail implements UserDetailsService{

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userServiceImpl.getUserByEmail(username);
        if(user==null){
            throw new UsernameNotFoundException("User Not Found !");
        }
        return org.springframework.security.core.userdetails.User.builder().username(user.getEmail()).password(user.getPassword())
                                                                .roles(user.getRole().toString()).authorities(user.getAuthorities()).build();
    }
    
}
