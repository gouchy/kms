    <%@ page language="java" contentType="text/html; charset=utf-8"
        pageEncoding="utf-8"%>
    <%@ page import="passion.app.kms.manager.util.Uploader" %>

    <%
    request.setCharacterEncoding("utf-8");
	response.setCharacterEncoding("utf-8");
	response.setContentType("application/json");
	
	//int length = request.getContentLength();
	String contentType = request.getContentType();
	
	//TODO: 检查请求用户是否登录
	
    Uploader up = new Uploader(request);
    //up.setSavePath("upload");
    String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
    up.setAllowFiles(fileType);
    //up.setSize(length);
    up.setContentType(contentType);
    up.setMaxSize(5000); //单位KB
    up.upload();
    response.getWriter().print("{\"state\": \""+ up.getState() + "\", \"url\": \"" + up.getUrl() + "\"}");
    %>
