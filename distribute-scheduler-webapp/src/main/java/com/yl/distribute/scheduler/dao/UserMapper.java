package com.yl.distribute.scheduler.dao;

import com.yl.distribute.scheduler.entity.User;
import java.util.List;

public interface UserMapper {

    User loadUserByUsername(String username);

    List<User> findMoreUser(String condition);

    void addUser(User user);

}
