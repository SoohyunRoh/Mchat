package com.project.myapp.websocket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.project.myapp.service.LoginService;
import com.project.myapp.service.SocketService;

public class SocketHandler extends TextWebSocketHandler implements InitializingBean {
	@Autowired
	private LoginService loginService;
	@Autowired
	private SocketService socketService;
	
	private final Logger logger = LogManager.getLogger(getClass());
	
	private HashMap<WebSocketSession, String> sessionSet = new HashMap<WebSocketSession, String>();
	
	//For search user by PID
	private NavigableMap<String, WebSocketSession> pidSet = new TreeMap<String, WebSocketSession>();
	
	public SocketHandler(){
		super();
		this.logger.info("Create SocketHandler instance.");
	}
	
	//Call when WebSocket is closed
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		sessionSet.remove(session);
		
		this.logger.info("remove session:"+session.getId());
		if(session.isOpen())session.close();
	}
	
	//Call when WebSocket is open and ready to use
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		sessionSet.put(session, null);
		//this.logger.info("add session:"+session.getId());
	}
	
	//Call when arrived message from client
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		super.handleMessage(session, message);

		String param = "";
		if(message != null && message.getPayload() != null){
			param = message.getPayload().toString();
		}
		
		JSONObject jsonMain = null;
		String code = null;
		String pid = null;
		String param1 = null;
		String param2 = null;
		
		try {
			if(param != null && param.length() > 4){	//Check contains 'CODE' or not
				jsonMain = new JSONObject(param);
				code = (String) jsonMain.get("CODE");
			}
		} catch(JSONException je) {
			this.logger.info("Exception:" + je.getMessage());
		}
		
		if(code != null){
			if(code.equals("A01")){						//A01: LOGIN
				pid = (String) jsonMain.get("PID");
				if(pidSet.containsKey(pid)){
					sessionSet.remove(pidSet.get(pid));
					pidSet.remove(pid);					//Remove duplicated session
				}
				sessionSet.remove(session);
				sessionSet.put(session, pid);
				pidSet.put(pid, session);				//Regist to sessionTree
				jsonMain = getAllLogonFriend(pid);		//INVOKE B01
				sendJSONMessageToSession(pid, jsonMain);
			} else if(code.equals("A02")){				//Logout
				pid = (String) jsonMain.get("PID");
				this.logger.info("code:"+code+"|pid:"+pid);
				sendFriendLogOff(pid);					//INVOKE B02
				WebSocketSession iSession = pidSet.get(pid);
				sessionSet.remove(iSession);
				pidSet.remove(pid);
				if(iSession.isOpen())iSession.close();
			} else if(code.equals("A04")){				//change user status
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("STATUS");
				changeUserStatus(pid, param1);
			} else if(code.equals("A05")){				//change user status message
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("MESSAGE");
				changeUserStatusMessage(pid, param1);
			} else if(code.equals("A06")){				//change portrait file
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FILE");
				changeUserPortrait(pid, param1);
			} else if(code.equals("A07")){				//block a friend
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				changeFriendRelation(pid, param1, "2");
			} else if(code.equals("A08")){				//unblock a friend
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				changeFriendRelation(pid, param1, "1");
			} else if(code.equals("A09")){				//delete a friend
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				changeFriendRelation(pid, param1, "9");
			} else if(code.equals("A10")){				//restore delete
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				changeFriendRelation(pid, param1, "2");
				sendSingleFriendInfo(pid, param1);
			} else if(code.equals("A11")){				//send friend request
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				addFriendRequest(pid, param1);
			} else if(code.equals("A12")){				//cancel friend request
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				cancelFriendRequest(pid, param1);
			} else if(code.equals("A13")){				//accept friend request
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				acceptFriendRequest(pid, param1);
				sendSingleFriendInfo(pid, param1);
				if(pidSet.containsKey(param1)){
					sendSingleFriendInfo(param1, pid);
				}
			} else if(code.equals("A14")){				//decline friend request
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FID");
				declineFriendRequest(pid, param1);
			} else if(code.equals("A20")){				//send message to friend
				pid = (String) jsonMain.get("PID");
				param1 = (String) jsonMain.get("FPID");
				param2 = (String) jsonMain.get("MESSAGE");
				String param3 = (String) jsonMain.get("CHATID");
				sendChatMessage(pid, param1, param2, param3);
			}
		}
		//this.logger.info("SESSION SET STRUCTURE:" + sessionSet.toString());
		//this.logger.info("PIDSET STRUCTURE:"+pidSet.toString());
	}
	
	public void sendChatMessage(String pid, String fpid, String message, String chatId) throws SQLException, JSONException {
		HashMap query = new HashMap();
		query.put("pid", pid);
		query.put("fpid", fpid);
		query.put("message", message);
		query.put("chatId", chatId);
		
		JSONObject tempSend = new JSONObject();
		
		if(pidSet.containsKey(fpid)){
			String chatSeq = this.socketService.insertChatMessage(query);
			tempSend.put("CODE", "B20");	//Send chat message to friend
			tempSend.put("FPID", pid);
			tempSend.put("MESSAGE", message);
			tempSend.put("CHATSEQ", chatSeq);
			sendJSONMessageToSession(fpid, tempSend);
		} else {
			tempSend.put("CODE", "E01");	//Friend has been log off
			tempSend.put("FPID", fpid);
			sendJSONMessageToSession(pid, tempSend);
		}
	}
	
	public void acceptFriendRequest(String pid, String fPid) throws SQLException {
		HashMap query = new HashMap();
		query.put("pid", fPid);
		query.put("fpid", pid);	//Which mean is 'I(fpid) accept your(pid) request'
		int queryResult = this.socketService.acceptFriendRequest(query);
	}
	
	public void declineFriendRequest(String pid, String fPid) throws SQLException{
		HashMap query = new HashMap();
		query.put("pid", fPid);
		query.put("fpid", pid);	//Which mean is 'I(fpid) decline your(pid) request'
		int queryResult = this.socketService.declineFriendRequest(query);
	}
	
	public void addFriendRequest(String pid, String fPid) throws SQLException, JSONException{
		HashMap query = new HashMap();
		query.put("pid", pid);
		query.put("fpid", fPid);
		int queryResult = this.socketService.addFriendRequest(query);
		
		JSONObject tempSend = new JSONObject();
		JSONObject friendRequest = new JSONObject();
		if(pidSet.containsKey(fPid)){
			HashMap myInfo = this.socketService.getSimpleMyInfo(pid);
			friendRequest.put("pid", pid);
			friendRequest.put("name", (String) myInfo.get("name"));
			friendRequest.put("fDigit", (String) myInfo.get("fDigit"));
			friendRequest.put("read", "N");
			tempSend.put("CODE", "B11");
			tempSend.put("REQUEST", friendRequest);
			
			sendJSONMessageToSession(fPid, tempSend);
		}
	}
	
	public void cancelFriendRequest(String pid, String fPid) throws SQLException, JSONException{
		HashMap query = new HashMap();
		query.put("pid", pid);
		query.put("fpid", fPid);
		int queryResult = this.socketService.cancelFriendRequest(query);
		
		JSONObject tempSend = new JSONObject();
		if(pidSet.containsKey(fPid)){
			tempSend.put("CODE", "B12");
			tempSend.put("PID", pid);
			
			sendJSONMessageToSession(fPid, tempSend);
		}
	}
	
	public void sendSingleFriendInfo(String pid, String fPid) throws JSONException, SQLException {
		HashMap query = new HashMap();
		HashMap friendMap = new HashMap();
		ArrayList<HashMap> friendChatList = null;
		query.put("pid", pid);
		query.put("fpid", fPid);
		
		JSONObject tempSend = new JSONObject();
		JSONObject friend = new JSONObject();

		friendMap = this.socketService.getSingleFriendInfo(query);
		String chatId = (String)friendMap.get("chatId");
		
		friend.put("pid", friendMap.get("pid"));
		friend.put("name", friendMap.get("name"));
		friend.put("fDigit", friendMap.get("fDigit"));
		friend.put("status", friendMap.get("status"));
		friend.put("statusMessage", friendMap.get("statusMessage"));
		friend.put("online", pidSet.containsKey(fPid));
		friend.put("block", friendMap.get("block") != null && friendMap.get("block").equals("1") ? false : true);
		friend.put("newFriend", friendMap.get("newFriend"));
		friend.put("portraitFile", friendMap.get("portraitFile"));
		friend.put("chatId", (String)friendMap.get("chatId"));
		
		friendChatList = this.loginService.getFriendChatList(chatId, fPid);
		friend.put("message", friendChatList);
		
		tempSend.put("CODE", "C02");
		tempSend.put("FRIEND", friend);
		sendJSONMessageToSession(pid, tempSend);
	}
	
	
	public void changeFriendRelation(String pid, String fPid, String relation) throws SQLException, JSONException{
		HashMap query = new HashMap();
		query.put("pid", pid);
		query.put("fpid", fPid);
		query.put("relation", relation);
		this.socketService.updateFriendRelation(query);
		
		if(pidSet.containsKey(fPid)){
			JSONObject tempSend = new JSONObject();
			if(relation.equals("1")){					//unblock
				tempSend.put("CODE", "B01");			//B01: friend logon
			} else if(relation.equals("2") || relation.equals("9")){	//block or delete
				tempSend.put("CODE", "B02");			//B02: friend logoff
			}
			tempSend.put("PID", pid);
			sendJSONMessageToSession(fPid, tempSend);
		}
	}
	
	public void changeUserPortrait(String pid, String file) throws SQLException, JSONException{
		ArrayList<HashMap> friendList = this.socketService.getSimpleFriendList(pid);
		String fPid = null;
		String relation = null;
		JSONObject JSONFriend = null;
		JSONArray JSONFriendListArray = new JSONArray();
		
		for(HashMap<?, ?> iFriend : friendList){
			fPid = (String) iFriend.get("fpid");
			relation = (String) iFriend.get("relation");
			if(pidSet.containsKey(fPid) && relation.equals("1")){
				JSONFriend = new JSONObject();
				JSONFriend.put("PID", fPid);
				JSONFriendListArray.put(JSONFriend);
				JSONObject tempSend = new JSONObject();
				tempSend.put("CODE", "B06");			//B04: friend change his status
				tempSend.put("PID", pid);
				tempSend.put("FILE", file);
				sendJSONMessageToSession(fPid, tempSend);
			}
		}
	}
	
	public void changeUserStatusMessage(String pid, String message) throws SQLException, JSONException {
		HashMap<String, String> query = new HashMap<String, String>();
		query.put("pid", pid);
		query.put("message", message);
		int queryResult = this.socketService.updateUserStatusMessage(query);
		
		if(queryResult == 1){
			ArrayList<HashMap> friendList = this.socketService.getSimpleFriendList(pid);
			String fPid = null;
			String relation = null;
			JSONObject JSONFriend = null;
			JSONArray JSONFriendListArray = new JSONArray();
			
			for(HashMap<?, ?> iFriend : friendList){
				fPid = (String) iFriend.get("fpid");
				relation = (String) iFriend.get("relation");
				if(pidSet.containsKey(fPid) && relation.equals("1")){
					JSONFriend = new JSONObject();
					JSONFriend.put("PID", fPid);
					JSONFriendListArray.put(JSONFriend);
					JSONObject tempSend = new JSONObject();
					tempSend.put("CODE", "B05");			//B04: friend change his status
					tempSend.put("PID", pid);
					tempSend.put("MESSAGE", message);
					sendJSONMessageToSession(fPid, tempSend);
				}
			}
		}
	}
	
	public void changeUserStatus(String pid, String status) throws SQLException, JSONException {
		HashMap<String, String> query = new HashMap<String, String>();
		query.put("pid", pid);
		query.put("status", status);
		int queryResult = this.socketService.updateUserStatus(query);
		
		if(queryResult == 1){
			ArrayList<HashMap> friendList = this.socketService.getSimpleFriendList(pid);
			String fPid = null;
			String relation = null;
			JSONObject JSONFriend = null;
			JSONArray JSONFriendListArray = new JSONArray();
			
			for(HashMap<?, ?> iFriend : friendList){
				fPid = (String) iFriend.get("fpid");
				relation = (String) iFriend.get("relation");
				
				if(pidSet.containsKey(fPid) && relation.equals("1")){
					JSONFriend = new JSONObject();
					JSONFriend.put("PID", fPid);
					JSONFriendListArray.put(JSONFriend);
					JSONObject tempSend = new JSONObject();
					tempSend.put("CODE", "B04");			//B04: friend change his status
					tempSend.put("PID", pid);
					tempSend.put("STATUS", status);
					sendJSONMessageToSession(fPid, tempSend);
				}
			}
		}
	}

	public void sendFriendLogOff(String pid) throws SQLException, JSONException{
		ArrayList<HashMap> friendList = this.socketService.getSimpleFriendList(pid);
		String fPid = null;
		String relation = null;
		
		JSONObject JSONFriend = null;
		JSONArray JSONFriendListArray = new JSONArray();
		
		for(HashMap<?, ?> iFriend : friendList){
			fPid = (String) iFriend.get("fpid");
			relation = (String) iFriend.get("relation");
			
			if(pidSet.containsKey(fPid) && relation.equals("1")){
				JSONFriend = new JSONObject();
				JSONFriend.put("PID", fPid);
				JSONFriendListArray.put(JSONFriend);
				JSONObject tempSend = new JSONObject();
				tempSend.put("CODE", "B02");			//B02: friend logoff
				tempSend.put("PID", pid);
				sendJSONMessageToSession(fPid, tempSend);
			}
		}
	}
	
	public JSONObject getAllLogonFriend(String pid) throws SQLException, JSONException{
		ArrayList<HashMap> friendList = this.socketService.getSimpleFriendList(pid);
		String fPid = null;
		String relation = null;
		JSONObject jsonMain = new JSONObject();
		JSONObject JSONFriend = null;
		JSONArray JSONFriendListArray = new JSONArray();
		JSONObject tempSend = null;
		
		for(HashMap ifriend : friendList){
			fPid = (String) ifriend.get("fpid");
			relation = (String) ifriend.get("relation");
			
			if(pidSet.containsKey(fPid) && !relation.equals("9")){
				JSONFriend = new JSONObject();
				JSONFriend.put("PID", fPid);
				JSONFriendListArray.put(JSONFriend);
				if(relation.equals("1")){
					tempSend = new JSONObject();
					tempSend.put("CODE", "B01");			//B01: friend logon
					tempSend.put("PID", pid);
					sendJSONMessageToSession(fPid, tempSend);
				}
			}
		}

		jsonMain.put("CODE","C01");
		jsonMain.put("LIST", JSONFriendListArray);
		this.logger.info("jsonMain:" + jsonMain.toString());
		return jsonMain;
	}
	
	
	//Call when occurred error of transport message 
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		//this.logger.info("EXCEPTIONAL DISCONNECT:"+session.getId());
		String pid = sessionSet.get(session);
		//this.logger.info("pid:"+pid);
		if(pid != null){
			sendFriendLogOff(pid);
		}
	}
	
	//Call when WebSocketHandler does handling partial message
	@Override
	public boolean supportsPartialMessages() {
		this.logger.info("call method");
		return super.supportsPartialMessages();
	}

	//Broadcast message
	public void sendMessage(String message) {
		Set entrySet = sessionSet.entrySet();
		Iterator it = entrySet.iterator();
		
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			WebSocketSession session = (WebSocketSession) me.getValue();
			if(session.isOpen()){
				try{
					session.sendMessage(new TextMessage(message));
				}catch(Exception e){
					this.logger.error("fail to send message.", e);
				}
			}
		}
	}

	//send message to session what requester want.
	public void sendJSONMessageToSession(String pid, JSONObject message) {
		WebSocketSession session = pidSet.get(pid);
		
		if(session != null && session.isOpen()){
			try{
				session.sendMessage(new TextMessage(message.toString()));
				//this.logger.info("Send to client("+pid+"): "+message);
			} catch (Exception ignored) {
				//this.logger.error("fail to send message.", ignored);
			}
		} else {
			if(session != null){
				//this.logger.info("Target not found for message : " + message);
			} else if(!session.isOpen()){
				//this.logger.info("Session is closed : " + message);
			}
			
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.logger.info("Websocket PropertiesSet completed");
		Thread thread = new Thread(){
//			int i = 0;
			@Override
			public void run(){

//				while (true){
//					try {
//						logger.info("send message index");
//						sendMessage ("send message index " + i++);
//						Thread.sleep(1000);
//					} catch (InterruptedException e){
//						e.printStackTrace();
//						break;
//					}
//				}
			}
		};
		thread.start();
	}
}
