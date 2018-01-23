package com.p2b.front.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.p2b.front.dao.UserMapper;
import com.p2b.front.entity.User;
import com.p2b.front.service.UserService;




@Service
public class UserServiceImpl implements UserService{
	
	@Resource
	private UserMapper userDao;
	
	public List<User> queryAllUser() {
		return userDao.queryAllUser();
	}

}
