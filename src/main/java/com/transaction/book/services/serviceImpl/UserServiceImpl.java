package com.transaction.book.services.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transaction.book.entities.User;
import com.transaction.book.jwtSecurity.JwtProvider;
import com.transaction.book.repository.UserRepo;
import com.transaction.book.services.serviceInterface.UserService;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Override
    public User registerUser(User user) {
        return this.userRepo.save(user);
    }

    @Override
    public User getUserByMobileNo(String mobilNo) {
        return this.userRepo.findByMobileNo(mobilNo);
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepo.findByEmail(email);
    }

    @Override
    public User getUserById(long id) {
        return this.userRepo.findById(id).get();
    }

    @Override
    public User getUserByJwt(String jwt) {
        String email =JwtProvider.getEmailByJwt(jwt);
        return this.userRepo.findByEmail(email);
    }

    @Override
    public boolean isEmptyUserTable() {
        return !this.userRepo.existsByIdNotNull();
    }

    @Override
    public List<User> getAllApprovalRequests() {
        return this.userRepo.findApprovalRequest();
    }

    @Override
    public void deleteApprovalRequest(long id) {
        this.userRepo.deleteById(id);
        return;
    }

    @Override
    public List<String> getAllFcmTokens() {
        return this.userRepo.getAllFcmToken();
    }
    
}
