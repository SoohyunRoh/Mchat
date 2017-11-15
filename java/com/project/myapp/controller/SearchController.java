package com.project.myapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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

import com.project.myapp.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	SearchService searchService;
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@RequestMapping(value={"searchFriend"}, method={org.springframework.web.bind.annotation.RequestMethod.POST, RequestMethod.HEAD})
	public void searchFriend(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, SQLException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();		//which is json type
	    String str = null;
	    while ((str = br.readLine()) != null) {
            sb.append(str);
        }
	    
	    JSONObject jsonRequest = new JSONObject(sb.toString());
	    String pid = jsonRequest.getString("pid");
	    String keyword = jsonRequest.getString("keyword");
	    String startPagenumber = jsonRequest.getString("pageNumber");
	    String targetnumber = jsonRequest.getString("targetNumber");
	   
	    int iStartPageNumber = Integer.parseInt(startPagenumber);
	    int startRowNumber = (Integer.parseInt(targetnumber) - 1) * 5;
	    
	    HashMap query = new HashMap();
	    query.put("pid", pid);
	    query.put("keyword", keyword );
	    
	    // Keyword classification
	    // test@aaa.bbb -> email:Y, name:N, fdigit:N	: Finding in email only
	    // soohyun		-> email:N, name:Y, fdigit:N	: Finding in name only
	    // soohyun#1234	-> email:N, name:Y, fdigit:Y	: Finding in name+'#'+fdigit 
	    query.put("email",  keyword.indexOf('@') == -1 ? "0" : "1" );	//It's email string or not
	    query.put("name", keyword.indexOf('@') == -1 ? "1" : "0");		//It's name string or not
	    query.put("fdigit", keyword.indexOf('#') == -1 ? "0" : "1");	//It's digit or not
	    query.put("start", startRowNumber);
	    
	    String totalRow = searchService.getSearchResultCount(query);
	    ArrayList searchResult = searchService.getSearchResult(query);
	    
	    int iTotalRow = Integer.parseInt(totalRow);
	    JSONArray pages = new JSONArray();
	    JSONObject number = null;
	    JSONObject result = new JSONObject();
	    for(int i = iStartPageNumber - 1; i < iStartPageNumber +4; i++){
	    	if(i * 5 < iTotalRow){
	    		number = new JSONObject();
	    		number.put("number", i + 1);
	    		pages.put(number);
	    	}
	    }
	    
	    result.put("pageNumbers", pages);
	    result.put("pagesOfResult", Math.ceil(iTotalRow / 5.0));
	    result.put("viewPage", targetnumber);
	    result.put("searchResult", searchResult);

	    logger.info("QUERY Parameter - {}",query);
	    logger.info("TOTAL - {}", iTotalRow);
	    logger.info("Result - {}", result.toString());
    
	    response.setContentType("application/json");
	    response.getWriter().print(result);
	}
}
