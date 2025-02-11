package com.transaction.book.services.serviceInterface;

import java.util.List;

import com.transaction.book.entities.User;

public interface UserService {
    User registerUser(User user);
    User getUserByMobileNo(String mobilNo);
    User getUserByEmail(String email);
    User getUserById(long id);
    User getUserByJwt(String jwt);
    boolean isEmptyUserTable();
    List<User> getAllApprovalRequests();
    void deleteApprovalRequest(long id);
    List<String> getAllFcmTokens();
}
