package com.project.myapp.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.project.myapp.service.SocketService;

@Controller
public class UploadController {
	
	@Autowired
	private SocketService socketService;	
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	

	@RequestMapping(value={"upload"}, method={org.springframework.web.bind.annotation.RequestMethod.POST, RequestMethod.HEAD})
	public void upload(
			HttpSession session, 
			HttpServletResponse response, 
			@RequestParam("file") MultipartFile file, 
			@RequestParam("pid") String pid ) throws JSONException, IOException, FormatException {

		String uploadPath = session.getServletContext().getRealPath("/WEB-INF/views/").concat("images").concat(File.separator);  
		String originalName = file.getOriginalFilename();
		String exec = originalName.substring(originalName.lastIndexOf(".") + 1, originalName.length());
		String now = new SimpleDateFormat("yyyyMMddHmsS").format(new Date());
		String filename = pid.concat(now).concat(".").concat(exec);
		List<String> acceptExec = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");
		
		if(exec == null)throw new FormatException("Not found a File extension");
		if(exec.equals(""))throw new FormatException("Not found a File extension");
		if(!acceptExec.contains(exec.toLowerCase()))throw new FormatException("Found unacceptable file extension");
		if(file.getSize() > 10000000)throw new FormatException("Exceed maximum file size");
		
		CommonsMultipartFile cmf = (CommonsMultipartFile) file;
		HashMap query = new HashMap();
		query.put("pid", pid);
		query.put("utime", now);
		query.put("exe", exec);
		
		File uploadFile = new File(uploadPath);
		if (!uploadFile.exists()) {
			uploadFile.mkdirs();
	    }
		
		File realFile = new File(uploadPath + filename);
		JSONObject ret = new JSONObject();
		
		try {
			cmf.transferTo(realFile);
			socketService.insertPortrait(query);
			ret.put("result", filename);
		} catch (IllegalStateException e) {
			logger.info(e.getMessage());
			ret.put("result", -1);
		} catch (IOException e) {
			logger.info(e.getMessage());
			ret.put("result", -1);
		} catch(SQLException e) {
			logger.info(e.getMessage());
			ret.put("result", -1);
		}

		response.setContentType("application/json");
	    response.getWriter().print(ret); 
	}
}
