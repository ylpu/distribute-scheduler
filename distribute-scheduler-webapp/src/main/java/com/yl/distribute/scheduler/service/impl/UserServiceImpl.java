package com.yl.distribute.scheduler.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yl.distribute.scheduler.dao.UserMapper;
import com.yl.distribute.scheduler.entity.User;
import com.yl.distribute.scheduler.service.UserService;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.yl.distribute.scheduler.utils.SecurityContextUtils;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserMapper userDao;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional
                .of(userDao.loadUserByUsername(username))
                .get();
    }
    
    @Override
    public UserDetails getCurrentUser(){
        return SecurityContextUtils.getCurrentUser();
    }

    @Override
    public Set<User> findMoreUser(@Nonnull String condition){
        UserDetails onlineUser = getCurrentUser();
        return doFindMoreUser( "%" + condition + "%").stream()
                .filter(user -> !user.getUsername().equals(onlineUser))
                .collect(toSet());
    }
    
    private List<User> doFindMoreUser(String condition){
        return userDao.findMoreUser(condition);
    }

    @Override
    @Transactional(readOnly = false)
    public void addUser(User user){
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );
        if(userDao.loadUserByUsername(user.getUsername()) != null){
            throw new RuntimeException("用户名重复");
        }
        userDao.addUser(user);

    }

    @Override
    public User getUserInfo(@Nullable String username){
        if(StringUtils.isEmpty(username)){
            username = getCurrentUser().getUsername();
        }
        return userDao.loadUserByUsername(username);
    }
    
    @Autowired
    public void setUserDao(UserMapper userDao){
        this.userDao = userDao;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }
}
