package com.yl.distribute.scheduler.entity;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.userdetails.UserDetails;

public class User extends AbstractUserDetails implements UserDetails {

    private static final long serialVersionUID = -5509256807259591938L;
    private Integer Id;
    private String username;
    private String password;
    private String email;    
    private Date createTime;
    private Date updateTime;
    
    @Override
    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User(String id, String username, String password, String email) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public User(UserDetails userDetails){
        this(userDetails.getUsername(),userDetails.getPassword());
    }

    public User() {
        super();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
    
    

    public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(obj instanceof User){
            if(obj instanceof UserDetails){
                UserDetails userDetails = (UserDetails)obj;
                if(this.getUsername().equals(userDetails.getUsername())){
                    return true;
                }
            }else{
                User user = (User)obj;
                if(this.getUsername().equals(user.getUsername())){
                    return true;
                }
            }
        }
        return false;
    }
}
