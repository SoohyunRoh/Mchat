package com.project.myapp.serviceImplement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.myapp.service.SocketService;

@Repository
public class SocketServiceImplement implements SocketService{
	@Autowired
	private SqlSession query;
	//private static final Logger logger = LoggerFactory.getLogger(SocketServiceImplement.class);
	
	public int updateUserStatus(HashMap param) throws SQLException {
		int ret = this.query.update("socketSupport.updateUserStatus", param);
		return ret;
	}
	
	public int updateUserStatusMessage(HashMap param) throws SQLException {
		int ret = this.query.update("socketSupport.updateUserStatusMessage", param);
		return ret;
	}
	
	public int insertPortrait(HashMap param) throws SQLException {
		int ret = this.query.insert("socketSupport.insertPortrait", param);
		return ret;
	}
	
	public int updateFriendRelation(HashMap param) throws SQLException {
		int ret = this.query.update("socketSupport.updateFriendRelation", param);
		return ret;
	}
	
	public ArrayList getSimpleFriendList(String param) throws SQLException {
		ArrayList<HashMap> result = new ArrayList();
		result = (ArrayList) this.query.selectList("socketSupport.getSimpleFriendList", param);
		return result;
	}
	
	public HashMap getSingleFriendInfo(HashMap param) throws SQLException{
		HashMap result = new HashMap();
		result = this.query.selectOne("socketSupport.getSingleFriendInfo", param);
		return result;
	}
	
	public int addFriendRequest(HashMap param) throws SQLException {
		String chkNew = this.query.selectOne("socketSupport.getFriendRequestStatus",param);
		int result = 0;
		param.put("status", "1");	//Request friend

		if(chkNew != null && chkNew.equals("0")){
			result = this.query.insert("socketSupport.insertFriendRequest", param);
		} else if(chkNew != null && chkNew.equals("1")){
			result = this.query.update("socketSupport.updateFriendRequest", param);
		}
		return result;
	}
	public int cancelFriendRequest(HashMap param) throws SQLException {
		String chkNew = this.query.selectOne("socketSupport.getFriendRequestStatus",param);
		int result = 0;
		param.put("status", "9");	//Request friend
		if(chkNew != null && chkNew.equals("0")){
			result = this.query.insert("socketSupport.insertFriendRequest", param);
		} else if(chkNew != null && chkNew.equals("1")){
			result = this.query.update("socketSupport.updateFriendRequest", param);
		}
		return result;
	}
	public HashMap getSimpleMyInfo(String param) throws SQLException{
		HashMap result = new HashMap();
		result = this.query.selectOne("socketSupport.getSimpleMyInfo", param);
		return result;
	}
	public int acceptFriendRequest(HashMap param) throws SQLException {
		int result = 0;
		param.put("status", "2");
		result = this.query.update("socketSupport.updateFriendRequest", param);
		
		HashMap tempParam = new HashMap();
		tempParam.put("pid", param.get("fpid"));
		tempParam.put("fpid", param.get("pid"));
		tempParam.put("status", "2");
		result = this.query.update("socketSupport.updateFriendRequest", tempParam);
		
		//Add friend (2 rows)
		HashMap addFriendParam = new HashMap();
		HashMap f1 = new HashMap();
		HashMap f2 = new HashMap();
		ArrayList <HashMap> friends = new ArrayList<HashMap>();
		
		f1.put("pid", param.get("pid"));
		f1.put("fpid", param.get("fpid"));
		f2.put("pid", param.get("fpid"));
		f2.put("fpid", param.get("pid"));
		friends.add(f1);
		friends.add(f2);
		addFriendParam.put("friendArray",friends);
		
		result = this.query.insert("socketSupport.insertFriend", addFriendParam);
		return result;
	}
	
	public int declineFriendRequest(HashMap param) throws SQLException{
		int result = 0;
		param.put("status", "3");
		result = this.query.update("socketSupport.updateFriendRequest", param);
		return result;
	}
	
	public String insertChatMessage(HashMap param) throws SQLException{
		String result = "";
		this.query.insert("socketSupport.insertChatMessage", param);
		result = (String) param.get("chatSeq");
		return result;
	}
	
	public int updateNewMessageStatus(String param) throws SQLException {
		int result = 0;
		result = this.query.update("socketSupport.updateNewMessageStatus", param);
		return result;
	}
}
