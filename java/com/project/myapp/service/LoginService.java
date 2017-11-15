package com.project.myapp.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract interface LoginService {
	
	/**
	 * loginRequest
	 * @param param (email, password)
	 * @return String (PID)
	 * @throws SQLException
	 */
	public abstract String loginRequest(HashMap param) throws SQLException;
	
	/**
	 * checkDuplicateEmail
	 * @param param (email)
	 * @return String (number of same email account)
	 * @throws SQLException
	 */
	public abstract String checkDuplicateEmail(String param) throws SQLException;
	
	/**
	 * getNewPID
	 * @param param (email, fdigit, nickname, password)
	 * @return int (insert result)
	 * @throws SQLException
	 */
	public abstract int getNewPID(HashMap param) throws SQLException;
	
	/**
	 * insertVertifyNumber
	 * @param param (PID, vnm(vertify nunmber))
	 * @return int (insert result)
	 * @throws SQLException
	 */
	public abstract int insertVertifyNumber(HashMap param) throws SQLException;
	
	/**
	 * checkVertifyNumber
	 * @param param (PID, vnm(vertify nunmber))
	 * @return String (number of matched rows)
	 * @throws SQLException
	 */
	public abstract String checkVertifyNumber(HashMap param) throws SQLException;
	
	/**
	 * updateVertifyNumber
	 * @param param (PID)
	 * @return int (update result)
	 * @throws SQLException
	 */
	public abstract int updateVertifyNumber(String param) throws SQLException;
	
	/**
	 * getPersonalInfo
	 * @param pid
	 * @return HashMap(resultMap:login.resultGetPersonalInfo)
	 * @throws SQLException
	 */
	public abstract HashMap getPersonalInfo(String pid) throws SQLException;
	
	/**
	 * getFriendList
	 * @param pid
	 * @return ArrayList<HashMap> (resultMap:login.resultGetFriendList)
	 * @throws SQLException
	 */
	public abstract ArrayList<HashMap> getFriendList(String pid) throws SQLException;
	
	/**
	 * getFriendReqList
	 * @param pid
	 * @return ArrayList<HashMap> (resultMap:login.resultGetFriendReqList)
	 * @throws SQLException
	 */
	public abstract ArrayList<HashMap> getFriendReqList(String pid) throws SQLException;
	
	/**
	 * getFriendChatList
	 * @param chatId
	 * @param pid
	 * @return ArrayList<HashMap> (resultMap:login.resultGetFriendChatList)
	 * @throws SQLException
	 */
	public abstract ArrayList<HashMap> getFriendChatList(String chatId, String pid) throws SQLException;
	
	/**
	 * getDeletedFriendList
	 * @param pid
	 * @return ArrayList<HashMap> (resultMap:login.resultGetDeletedFriendList)
	 * @throws SQLException
	 */
	public abstract ArrayList<HashMap> getDeletedFriendList(String pid) throws SQLException;
}

