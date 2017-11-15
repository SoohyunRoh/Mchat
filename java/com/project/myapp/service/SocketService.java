package com.project.myapp.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract interface SocketService {
	
	/**
	 * updateUserStatus
	 * @param param (PID, status
	 * @return int (update result)
	 * @throws SQLException
	 */
	public abstract int updateUserStatus(HashMap param) throws SQLException;
	
	/**
	 * updateUserStatusMessage
	 * @param param (PID, message)
	 * @return int (update result)
	 * @throws SQLException
	 */
	public abstract int updateUserStatusMessage(HashMap param) throws SQLException;
	
	/**
	 * insertPortrait
	 * @param param (PID, utime, exe)
	 * @return int (insert result)
	 * @throws SQLException
	 */
	public abstract int insertPortrait(HashMap param) throws SQLException;
	
	/**
	 * updateFriendRelation (PID ---(relation)---> FPID)
	 * 		relation:1-Friend, 2-Block, 9-Delete
	 * @param param (pid, fpid, relation)
	 * @return int (insert result)
	 * @throws SQLException
	 */
	public abstract int updateFriendRelation(HashMap param) throws SQLException;
	
	/**
	 * getSimpleFriendList - Get friend list exclude deleted friend
	 * @param param (PID)
	 * @return ArrayList<HashMap> (resultMap:socketSupport.resultSimpleFriendList)
	 * @throws SQLException
	 */
	public abstract ArrayList getSimpleFriendList(String param) throws SQLException;
	
	/**
	 * getSingleFriendInfo - Get friend info with relation status
	 * @param param (PID, fpid)
	 * @return HashMap (resultMap:socketSupport.resultSingleFriendInfo)
	 * @throws SQLException
	 */
	public abstract HashMap getSingleFriendInfo(HashMap param) throws SQLException;
	
	/**
	 * addFriendRequest
	 * @param param (PID, FPID)
	 * @return int (insert or update result)
	 * @throws SQLException
	 */
	public abstract int addFriendRequest(HashMap param) throws SQLException;
	
	/**
	 * cancelFriendRequest
	 * @param param (PID, FPID)
	 * @return int (insert or update result)
	 * @throws SQLException
	 */
	public abstract int cancelFriendRequest(HashMap param) throws SQLException;
	
	/**
	 * getSimpleMyInfo
	 * @param param (PID)
	 * @return HashMap (resultMap:socketSupport.resultGetSimpleMyInfo)
	 * @throws SQLException
	 */
	public abstract HashMap getSimpleMyInfo(String param) throws SQLException;
	
	/**
	 * acceptFriendRequest - Change friend request status and insert 2 row into FRIEND table
	 * @param param (PID, FID)
	 * @return int (chatId)
	 * @throws SQLException
	 */
	public abstract int acceptFriendRequest(HashMap param) throws SQLException;
	
	/**
	 * declineFriendRequest
	 * @param param (PID, FID)
	 * @return int (update result)
	 * @throws SQLException
	 */
	public abstract int declineFriendRequest(HashMap param) throws SQLException;
	
	/**
	 * insertChatMessage
	 * @param param (CHAT_ID, PID, MESSAGE)
	 * @return String (chatSeq)
	 * @throws SQLException
	 */
	public abstract String insertChatMessage(HashMap param) throws SQLException;
	
	/**
	 * updateNewMessageStatus
	 * @param param (CHAT_SEQ)
	 * @return int (update result)
	 * @throws SQLException
	 */
	public abstract int updateNewMessageStatus(String param) throws SQLException;
}
