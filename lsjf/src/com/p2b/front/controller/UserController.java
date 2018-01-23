package com.p2b.front.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.google.gson.Gson;
import com.p2b.front.entity.User;
import com.p2b.front.service.UserService;
import org.springframework.ui.Model; 


@Controller
public class UserController {
	Model model;
	@Resource
	private UserService userService;
	
	
	
	@RequestMapping(value="/list.do",method=RequestMethod.POST)
	public String list(){
		List<User> list = userService.queryAllUser();
		Gson gson = new Gson();
		String str = gson.toJson(list);
		model.addAttribute("", str);
		return  "login";
	}
}

