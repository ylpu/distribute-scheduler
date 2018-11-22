package com.yl.distribute.scheduler.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.yl.distribute.scheduler.entity.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface UserService extends UserDetailsService {
	
    UserDetails getCurrentUser();
    
    Set<User> findMoreUser(@Nonnull String condition);

    void addUser(User user);

    User getUserInfo(@Nullable String username);
}
