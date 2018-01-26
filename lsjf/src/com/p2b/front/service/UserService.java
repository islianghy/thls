package com.p2b.front.service;

import java.util.List;

import com.p2b.front.entity.User;



public interface UserService {
	public List<User> queryAllUser();
	//插入一条记录
	public void insert(User user);
	//查询phone这个手机号码是否已经注册
	public boolean phoneIsExist(String phone);
	//根据phone查询相应的user对象
	public User selectByPhone(String phone);
	//根据业务员id查询相应对象
	public User selectById(int userid);
	//根据新传入的User对象更新对应数据库中的数据
	public void update(User u);
}
