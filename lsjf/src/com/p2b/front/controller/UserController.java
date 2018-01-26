package com.p2b.front.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import codeMD5.CodeMD5;

import com.google.gson.Gson;
import com.p2b.front.entity.User;
import com.p2b.front.service.UserService;

import cookies.CookieUtils;
import redis.clients.jedis.Jedis;
import sendMessage.sendsms;

@Controller
public class UserController {	
	@Resource
	private UserService userService;
	
	/*	功能：	业务员注册时发送验证码，并将手机号码和验证码推入redis缓存
	 * 	传入参数：	手机号码——String类型
	 * 	返回值：	“success”json字符串*/
	@RequestMapping(value="/sendMessageRegister.do",method=RequestMethod.POST)
	public @ResponseBody String sendMessageRegister(String phone){
		sendsms sd=new sendsms();	
		Gson gson = new Gson();
		//执行发送短信和推入缓存的方法
		sd.sendMessage(phone);	
		//返回前台结果		
		String str = gson.toJson("success");				
		return  str;
	}
	/*	功能：	业务员注册，对比手机验证码是否正确，将用户基本信息存入mysql数据库
	 * 	传入参数：	输入参数为（手机号码，密码）——User类型；验证码——String类型
	 * 	返回值：	成功或报错信息的json串*/
	@RequestMapping(value="/userRegister.do",method=RequestMethod.POST)
	public @ResponseBody String userRegister(User user,String verificationCode){    //前台传来的user数据和验证码verificationCode
		Gson gson = new Gson();		
		Jedis jedis = new Jedis("localhost");
	    System.out.println("连接redis成功");
	    System.out.println("服务正在运行: "+jedis.ping());		       
        System.out.println("连接成功");
        //声明变量redisCode用于存储从redis查询出来的验证码
        String redisCode="";
        redisCode=jedis.get(user.getPhone());
        //判断redis中该手机号对应的验证码是否过期
        if (redisCode.equals("")||redisCode==null){
        	String str = gson.toJson("验证码已失效");
        	return str;
        //验证前台传来的验证码是否为空
        }else if(verificationCode.equals("")||verificationCode==null){  
        	String str = gson.toJson("验证码不能为空");
        	return str;
        //验证前台传来的验证码和redis数据库中存储的是否一致
        }else if(verificationCode.equals(redisCode)){
        	System.out.println("redis 存储的字符串为: "+ redisCode);
        	//判断该手机号码是否已经注册
        	if(userService.phoneIsExist(user.getPhone())){
        		CodeMD5 cm=new CodeMD5();
        		user.setPassword(cm.MD5(user.getPassword()));
        		userService.insert(user);
            	String str = gson.toJson("成功");
            	return str;        		
        	}else {
        		String str = gson.toJson("手机号已注册");
            	return str;
        	}
        	
        //前台传来的验证码和redis数据库中存储的不一致，返回错误信息
        }else{
        	String str = gson.toJson("验证码错误");
        	return str;
        }		
	}
	/*	功能：	业务员登录，判断用户是否存在，对比手机号码与密码是否一致，若一致向客户端写入cookie向服务器端写入session
	 * 	传入参数：	输入参数为（手机号码，密码）——User类型
	 * 	返回值：	成功或报错信息的json串*/
	@RequestMapping(value="/userLogin.do",method=RequestMethod.POST)
	public @ResponseBody String userLogin(User user,HttpServletRequest request,HttpServletResponse response){
		Gson gson = new Gson();
		CodeMD5 cm=new CodeMD5();
		//根据前台传来的手机号查询在数据库中是否有相应的业务员信息
		User userInMysql=userService.selectByPhone(user.getPhone());
		//如数据库中没有信息，则返回用户名不存在
		if(userInMysql==null){
			String str = gson.toJson("用户名不存在");
        	return str; 
        //若存在相应的业务员信息，则判断密码是否一致
		}else if(userInMysql.getPassword().equals(cm.MD5(user.getPassword()))){			
			// 新建cookie 客户浏览器端保存当前用户的id
			// key-value格式 key-loginuser value-当前用户的id
			Cookie cookie = new Cookie("loginuser", userInMysql.getUserid()+"");
			// 设置客户端cookie
			response.addCookie(cookie);			
			// 设置session 服务器端保存登录用户信息			
			// session是key-value格式
			// 把userid作为key,value是user对象
			request.getSession().setAttribute(userInMysql.getUserid() + "", user);
			//获取cookie中的信息,预留使用
//			int user_id = Integer.parseInt(CookieUtils.getCookieFromCookies(request.getCookies(), "loginuser").getValue());
			System.out.println("session中保存的是"+request.getSession().getAttribute(String.valueOf(userInMysql.getUserid())));
			String str = gson.toJson("登陆成功");
        	return str;
        //若密码不一致则返回提示
		}else{
			String str = gson.toJson("密码不正确");
        	return str;
		}
		
	}
	/*	功能：	业务员在登陆状态验证并修改密码
	 * 	传入参数：	旧密码——String类型；新密码——String类型
	 * 	返回值：	“success”json字符串*/
	@RequestMapping(value="/modifyPassword.do",method=RequestMethod.POST)
	public @ResponseBody String modifyPassword(String oldPassword,String newPassword,HttpServletRequest request){
		CodeMD5 cm=new CodeMD5();
		Gson gson = new Gson();
		//获取cookie中的信息，即业务员表主键id，
		int userid = Integer.parseInt(CookieUtils.getCookieFromCookies(request.getCookies(), "loginuser").getValue());
		User u=userService.selectById(userid);
		//如用户输入的原密码与数据库中密码一致，则修改密码，密码采用post方式明文传输，MD5加密存储
		if(u.getPassword().equals(cm.MD5(oldPassword))){
			u.setPassword(cm.MD5(newPassword));
			userService.update(u);
			String str = gson.toJson("更改密码成功");				
			return  str;
		//如用户输入的原密码与数据库中密码不一致，则返回错误信息
		}else{
			String str = gson.toJson("原密码不正确");				
			return  str;
		}		
	}
	/*	功能：	在业务员忘记密码时通过手机号发送短信验证码，并推入redis缓存
	 * 	传入参数：	手机号码——String类型
	 * 	返回值：	“success”json字符串*/
	@RequestMapping(value="/sendMessageForget.do",method=RequestMethod.POST)
	public @ResponseBody String sendMessageForget(String phone){
		sendsms sd=new sendsms();	
		Gson gson = new Gson();
		//执行发送短信和推入缓存的方法
		sd.sendMessage(phone);	
		//返回前台结果		
		String str = gson.toJson("success");				
		return  str;		
	}
	/*	功能：	业务员忘记密码通过手机号和短信验证码进行密码重置
	 * 	传入参数：	手机号码——String类型；新密码——String类型；验证码——String类型
	 * 	返回值：	“success”json字符串*/
	@RequestMapping(value="/forgetPassword.do",method=RequestMethod.POST)
	public @ResponseBody String forgetPassword(String phone,String newPassword,String verificationCode){
		CodeMD5 cm=new CodeMD5();
		Gson gson = new Gson();		
		Jedis jedis = new Jedis("localhost");
	    System.out.println("连接redis成功");
	    System.out.println("服务正在运行: "+jedis.ping());		       
        System.out.println("连接成功");
        //声明变量redisCode用于存储从redis查询出来的验证码
        String redisCode="";
        redisCode=jedis.get(phone);
        //判断redis中该手机号对应的验证码是否过期
        if (redisCode.equals("")||redisCode==null){
        	String str = gson.toJson("验证码已失效");
        	return str;
        //验证前台传来的验证码是否为空
        }else if(verificationCode.equals("")||verificationCode==null){  
        	String str = gson.toJson("验证码不能为空");
        	return str;
        //验证前台传来的验证码和redis数据库中存储的是否一致
        }else if(verificationCode.equals(redisCode)){
        	System.out.println("redis 存储的字符串为: "+ redisCode);
        	//判断该手机号码是否已经注册，若存在数据则更新数据库
        	User u=userService.selectByPhone(phone);
        	u.setPassword(cm.MD5(newPassword));
        	userService.update(u);        	
            String str = gson.toJson("成功");     
            return str;
        //前台传来的验证码和redis数据库中存储的不一致，返回错误信息
        }else{
        	String str = gson.toJson("验证码错误");
        	return str;
        }			
	}
	/*	功能：	完善业务员信息
	 * 	传入参数：	user——User类型
	 * 	返回值：	“success”json字符串*/
	//数据库表结构需进一步落实，例如银行卡，暂时挂起
	@RequestMapping(value="/modifyUserInfo.do",method=RequestMethod.POST)
	public @ResponseBody String modifyUserInfo(User user){		
		Gson gson = new Gson();				
		String str = gson.toJson("success");				
		return  str;		
	}
	/*	功能：	业务员实名制认证，上传业务员的身份证正反面两张图片
	 * 	传入参数：	正面图片——MultipartFile类型，反面图片——MultipartFile类型，
	 * 	返回值：	“success”json字符串*/
	@RequestMapping(value="/realNameVerPicUp.do",method=RequestMethod.POST)
	public @ResponseBody String realNameVerPicUp(MultipartFile frontPhoto,MultipartFile backPhoto,HttpServletRequest request){
		//获取文件存储路径
		String path = request.getSession().getServletContext().getRealPath("UserRealNamePicDir"); 
		//获取传入文件名称
		String frontFileName = frontPhoto.getOriginalFilename();    
		String backFileName = backPhoto.getOriginalFilename();  
		//从客户端cookie获取当前登录用户id
		int userid = Integer.parseInt(CookieUtils.getCookieFromCookies(request.getCookies(), "loginuser").getValue());
		//将当前用户id写入到文件名中
		File frontDir = new File(path,"user-"+userid+"-frontPhoto"+frontFileName.substring(frontFileName.lastIndexOf(".")));
        if(!frontDir.exists()){  
        	frontDir.mkdirs();  
        }  
        File backDir = new File(path,"user-"+userid+"-backPhoto"+backFileName.substring(backFileName.lastIndexOf(".")));
        if(!backDir.exists()){  
        	backDir.mkdirs();  
        }  
        //MultipartFile自带的解析方法  
        try {
        	frontPhoto.transferTo(frontDir);
        	backPhoto.transferTo(backDir);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //文件路径存入数据库时正斜线和反斜线存储或异常，保留代码，用于处理这个问题
//      path=path.replaceAll("\\\\", "/");
//		System.out.println(path);
//		path=path+"/"+frontFileName;
//		System.out.println(path);
        //根据cookie中的id读取对应的User
		User u=userService.selectById(userid);
		//将存入两张图片的的路径用分号拼接起来，存入数据库
		u.setIdphoto(frontDir.getPath()+";"+backDir.getPath());
		userService.update(u);		
		Gson gson = new Gson();				
		String str = gson.toJson("success");				
		return  str;		
	}
	/*	功能：	实现图片的下载功能，为下一步开发预留用
	 * 	传入参数：	无
	 * 	返回值：	无*/
	@RequestMapping(value="/reserveDown.do",method=RequestMethod.POST)
	public @ResponseBody String reserveDown(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Gson gson = new Gson();		
		 //模拟文件，user-11-fronPhoto.jpg为需要下载的文件  
        String fileName = request.getSession().getServletContext().getRealPath("UserRealNamePicDir")+"/user-11-fronPhoto.jpg";  
        //获取输入流  
        InputStream bis = new BufferedInputStream(new FileInputStream(new File(fileName)));  
        //假如以中文名下载的话  
        String filename = "下载文件.jpg";  
        //转码，免得文件名中文乱码  
        filename = URLEncoder.encode(filename,"UTF-8");  
        //设置文件下载头  
        response.addHeader("Content-Disposition", "attachment;filename=" + filename);    
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型    
        response.setContentType("multipart/form-data");   
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());  
        int len = 0;  
        while((len = bis.read()) != -1){  
            out.write(len);  
            out.flush();  
        }  
        out.close();  
				
		String str = gson.toJson("success");				
		return  str;		
	}
}

