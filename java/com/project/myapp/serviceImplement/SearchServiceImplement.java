package com.project.myapp.serviceImplement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.myapp.service.SearchService;

@Repository
public class SearchServiceImplement implements SearchService {
	@Autowired
	private SqlSession query;
	//private static final Logger logger = LoggerFactory.getLogger(SearchServiceImplement.class);
	
	public ArrayList getSearchResult(HashMap param) throws SQLException{
		ArrayList result = new ArrayList();
		result = (ArrayList) this.query.selectList("search.getSearchResult",param);
		return result;
	}
	public String getSearchResultCount(HashMap param) throws SQLException{
		String result = this.query.selectOne("search.getSearchResultCount", param);
		return result;
	}
}
