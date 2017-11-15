package com.project.myapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



import com.project.myapp.service.LoginService;

@Controller
public class LoginController {
	@Autowired
	private LoginService loginService;

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@RequestMapping(value={"logoutRequest"}, method={org.springframework.web.bind.annotation.RequestMethod.POST, RequestMethod.HEAD})
	public void logoutRequest(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws JSONException, IOException{

		session.invalidate();
		
		JSONObject jsonResponse = new JSONObject();
		JSONObject jsonResult = new JSONObject();

		jsonResponse.put("result", "1");
		response.setContentType("application/json");
	    response.getWriter().print(jsonResponse); 
	}
	
	@RequestMapping(value={"loginRequest"}, method={org.springframework.web.bind.annotation.RequestMethod.POST, RequestMethod.HEAD})
	public void loginRequest(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws JSONException, IOException, SQLException{

		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();
	    String str = null;
	    while ((str = br.readLine()) != null) {
            sb.append(str);
        }
	    
	    JSONObject jsonRequest = new JSONObject(sb.toString());
	    String email = jsonRequest.getString("email");
	    String password = jsonRequest.getString("password");
	    
	    HashMap query = new HashMap();
	    query.put("email", email);
	    query.put("password", password);
	   
	    String result = null;
	    result = this.loginService.loginRequest(query);
	    
	    JSONObject jsonResponse = new JSONObject();
	    if(result != null && !result.equals("")){
	    	jsonResponse.put("result", result);
	    	session.setAttribute("pid", result);
	    	
	    } else {
	    	jsonResponse.put("result", -1);		//login fail
	    }

	    logger.info("LOGIN STATUS- [pid:{}, session:{} ]", result, session.getId());
	    response.setContentType("application/json");
	    response.getWriter().print(jsonResponse); 
	}
	
	@RequestMapping(value={"checkVertifyNumber"}, method={org.springframework.web.bind.annotation.RequestMethod.POST, RequestMethod.HEAD})
	public void checkVertifyNumber(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws JSONException, IOException, SQLException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();
	    String str = null;
	    while ((str = br.readLine()) != null) {
            sb.append(str);
        }
	    
	    JSONObject jsonRequest = new JSONObject(sb.toString());
	    String pid = jsonRequest.getString("pid");
	    String vnm = jsonRequest.getString("vnm");
	    HashMap query = new HashMap();
	    query.put("pid", pid);
	    query.put("vnm", vnm);
	    
	    String result = null;
	    int updResult = 0;
	    
	    JSONObject jsonResponse = new JSONObject();
	    
	    result = this.loginService.checkVertifyNumber(query);
	    if(result != null && result.equals("1")){
	    	updResult = this.loginService.updateVertifyNumber(pid);
	    	jsonResponse.put("result", 1);
	    } else {
	    	jsonResponse.put("result", 0);
	    }
	    
	    response.setContentType("application/json");
	    response.getWriter().print(jsonResponse); 
	}
		
	@RequestMapping(value={"checkDuplicateEmail"}, method={org.springframework.web.bind.annotation.RequestMethod.POST, RequestMethod.HEAD})
	public void checkDuplicateEmail(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws JSONException, IOException, AddressException, MessagingException, SQLException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();		//which is json type
	    String str = null;
	    while ((str = br.readLine()) != null) {
            sb.append(str);
        }
	    
	    JSONObject jsonRequest = new JSONObject(sb.toString());
	    String email = jsonRequest.getString("email");
	    String password = jsonRequest.getString("password");
	    String nickname = jsonRequest.getString("nickname");
	    
	    HashMap query = new HashMap();
	    query.put("email", email);
	    query.put("password", password);
	    query.put("nickname", nickname);
	    
	    String result = null;
	    int newPID = 0;
	    
	    JSONObject jsonResponse = new JSONObject();
    	result = this.loginService.checkDuplicateEmail(email);
    	
    	if(result != null && result.equals("0")){
    		//CREATE NEW PID
    		Random rand = new Random();
    		String fDigit = String.format("%04d", rand.nextInt(9999) + 1);
    		query.put("fdigit", fDigit);
    		newPID = this.loginService.getNewPID(query);
    		
    		query.put("pid", newPID);
    		
    		//CREATE RANDOM NUMBER -> VERTIFY NUMBER
    		String vertifyString = String.format("%04d", rand.nextInt(9999) + 1);
    		query.put("vnm", vertifyString);

    		//INSERT VERTIFY NUMBER
    		int insertResult = this.loginService.insertVertifyNumber(query);
    		
    		//Send email
    		mailSender(email, vertifyString);

    		jsonResponse.put("result", newPID);
    	} else {
    		jsonResponse.put("result", 0);	
    	}

	    response.setContentType("application/json");
	    response.getWriter().print(jsonResponse); 
	}
	
	public boolean mailSender(String mailAddress, String digit) throws AddressException, MessagingException{
		
		String subject = "[Mchat]Vertification Number";
		StringBuilder body = new StringBuilder();
		body.append("Welcome to Mchat.<br>This is personal vertification mail.<br> Please input below 4 digit number to Mchat vertification.<Br>");
		body.append("<br><H1>").append(digit).append("</h1><br>");

		//Gmail account
		final String username = "sroh79@gmail.com";		//Private Information
		final String password = "**********";			//Private Information

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		//Create session for mail authenticator
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailAddress));
			message.setSubject(subject);
			message.setContent(body.toString(), "text/html");
			
			//Send email
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	@RequestMapping(value={"getUserInfo"}, method={org.springframework.web.bind.annotation.RequestMethod.POST, RequestMethod.HEAD})
	public void getUserInfo(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws JSONException, IOException, SQLException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();
	    String str = null;
	    while ((str = br.readLine()) != null) {
            sb.append(str);
        }
	    
	    JSONObject jsonRequest = new JSONObject(sb.toString());
	    String pid = jsonRequest.getString("pid");
	    
	    HashMap personalInfo = null;
	    ArrayList<HashMap> friendList = null;
	    ArrayList<HashMap> friendReqList = null;
	    ArrayList<HashMap> friendChatList = null;
	    ArrayList<HashMap> deletedFriendList = null;
	    
	    JSONObject jsonMain = new JSONObject();
    	JSONArray JSONFriendRequestListArray = new JSONArray();
    	JSONArray JSONDeletedFriendListArray = new JSONArray();
    	JSONArray JSONFriendListArray = new JSONArray();
    	JSONObject JSONFriend = null;
    	
    	personalInfo = this.loginService.getPersonalInfo(pid);
    	friendReqList = this.loginService.getFriendReqList(pid);
    	deletedFriendList = this.loginService.getDeletedFriendList(pid);
    	friendList = this.loginService.getFriendList(pid);
    	
    	JSONFriendRequestListArray.put(friendReqList);
    	JSONDeletedFriendListArray.put(deletedFriendList);
    	
    	String chatId = null;
    	for (HashMap friend : friendList) {
    		friendChatList = null;
    		chatId = (String)friend.get("chatId");
    		friendChatList = this.loginService.getFriendChatList(chatId, pid);
    		
    		JSONFriend = new JSONObject();
    		JSONFriend.put("pid", friend.get("pid"));
    		JSONFriend.put("name", friend.get("name"));
    		JSONFriend.put("fDigit", friend.get("fDigit"));
    		JSONFriend.put("status", friend.get("status"));
    		JSONFriend.put("statusMessage", friend.get("statusMessage"));
    		JSONFriend.put("online", false);
    		JSONFriend.put("block", friend.get("block") != null && friend.get("block").equals("1") ? false : true );
    		JSONFriend.put("newFriend", friend.get("newFriend"));
    		JSONFriend.put("chatId", chatId);
    		JSONFriend.put("portraitFile", friend.get("portraitFile"));
    		JSONFriend.put("message", friendChatList);
    		JSONFriendListArray.put(JSONFriend);
    	}

    	jsonMain.put("myStatus", personalInfo);
    	jsonMain.put("friendRequestList", friendReqList);
    	jsonMain.put("deletedFriendList", deletedFriendList);
    	jsonMain.put("friendList", JSONFriendListArray);

	    response.setContentType("application/json");
	    response.getWriter().print(jsonMain); 
	}
}
