package com.project.myapp.serviceImplement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.myapp.controller.LoginController;
import com.project.myapp.service.LoginService;

@Repository
public class LoginServiceImplement implements LoginService {
	@Autowired
	private SqlSession query;
	//private static final Logger logger = LoggerFactory.getLogger(LoginServiceImplement.class);
	
	public String loginRequest(HashMap in) throws SQLException {
		String result = (String)this.query.selectOne("login.loginRequest", in);
		return result;
	}
	
	public String checkDuplicateEmail(String in) throws SQLException {
		String result = (String)this.query.selectOne("login.checkDuplicateEmail", in);
		return result;
	}
	
	public int getNewPID(HashMap in) throws SQLException {
		int result = this.query.insert("login.getNewPID", in);
		return (Integer)in.get("PID");
	}
	
	public int insertVertifyNumber(HashMap in) throws SQLException {
		int result = this.query.insert("login.insertVertifyNumber", in);
		return result;
	}
	
	public String checkVertifyNumber(HashMap in) throws SQLException {
		String result = (String)this.query.selectOne("login.checkVertifyNumber", in);
		return result;
	}
	
	public int updateVertifyNumber(String in) throws SQLException {
		int result = this.query.update("login.updateVertifyNumber", in);
		return result;
	}
	
	public HashMap getPersonalInfo(String pid) throws SQLException {
		HashMap result = null;
		result = (HashMap) this.query.selectOne("login.getPersonalInfo", pid);
		return result;
	}
	
	public ArrayList<HashMap> getFriendList(String pid) throws SQLException {
		ArrayList<HashMap> result = new ArrayList();
		result = (ArrayList) this.query.selectList("login.getFriendList", pid);
		return result;
	}
	
	public ArrayList<HashMap> getFriendReqList(String pid) throws SQLException {
		ArrayList<HashMap> result = new ArrayList();
		result = (ArrayList) this.query.selectList("login.getFriendReqList", pid);
		return result;
	}
	
	public ArrayList<HashMap> getFriendChatList(String chatId, String pid) throws SQLException {
		ArrayList<HashMap> result = new ArrayList();
		HashMap query = new HashMap();
		query.put("chatId", chatId);
		query.put("pid", pid);
		result = (ArrayList) this.query.selectList("login.getFriendChatList", query);
		return result;
	}
	
	public ArrayList<HashMap> getDeletedFriendList(String pid) throws SQLException {
		ArrayList<HashMap> result = new ArrayList();
		result = (ArrayList) this.query.selectList("login.getDeletedFriendList", pid);
		return result;
	}
}