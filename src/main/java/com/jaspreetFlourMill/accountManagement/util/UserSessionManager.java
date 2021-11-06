package com.jaspreetFlourMill.accountManagement.util;

import com.jaspreetFlourMill.accountManagement.model.Role;
import com.jaspreetFlourMill.accountManagement.model.User;

public class UserSessionManager {
    private User user;
    private Authentication authentication;
    private Role role;

    public UserSessionManager(User user) {
        this.user = user;
        this.authentication = new Authentication();
    }
}
