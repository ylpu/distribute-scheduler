package com.yl.distribute.scheduler.controller;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.yl.distribute.scheduler.common.bean.SchedulerResponse;
import com.yl.distribute.scheduler.entity.User;
import com.yl.distribute.scheduler.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @ResponseBody
    @RequestMapping(path = "/findMoreUser", method = RequestMethod.GET,produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,})
    public SchedulerResponse<Set<User>> findMoreUser(@RequestBody  String condition){
    	return new SchedulerResponse<Set<User>>(userService.findMoreUser(condition));
    }

    @ResponseBody
    @RequestMapping(path = "/addUser",method = RequestMethod.POST)
    public void addUser(@RequestBody User user){
    	userService.addUser(user);
    }
    
    @ResponseBody
    @RequestMapping(path = "/userInfo", method = RequestMethod.GET)
    public SchedulerResponse<User> getUserInfo(){
        return new SchedulerResponse<User>(userService.getUserInfo(null));
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }
}
