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
	//插入一条业务员信息
	public void insert(User user) {
		userDao.insert(user);
		// TODO Auto-generated method stub
		
	}
	//判断该手机号码是否存在
	public boolean phoneIsExist(String phone ) {
		// TODO Auto-generated method stub
		int num=userDao.countByPhone(phone);
		System.out.println("num="+num);
		if(num>0){
			System.out.println("false");
			return false;			
		}else{
			System.out.println("true");
			return true;
		}
		
	}
	//根据phone查询相应的user对象
	public User selectByPhone(String phone) {
		// TODO Auto-generated method stub
		return userDao.selectByPhone(phone);
	}
	//根据业务员id查询相应对象
	public User selectById(int userid) {
		// TODO Auto-generated method stub
		return userDao.selectByPrimaryKey((long)userid);
	}
	//根据新传入的User对象更新对应数据库中的数据
	public void update(User u) {
		// TODO Auto-generated method stub
		userDao.updateByPrimaryKey(u);
	}

}
