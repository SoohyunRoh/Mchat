package com.project.myapp.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract interface SearchService {
	
	/**
	 * getSearchResult
	 * @param param (PID, name, fdigit, email, keyword)
	 * @return ArrayList<HashMap> (resultMap:search.resultgetSearchResult)
	 * @throws SQLException
	 */
	public abstract ArrayList getSearchResult(HashMap param) throws SQLException;
	
	/**
	 * getSearchResultCount
	 * @param param (PID, name, fdigit, email, keyword)
	 * @return String (number of rows)
	 * @throws SQLException
	 */
	public abstract String getSearchResultCount(HashMap param) throws SQLException;
}
