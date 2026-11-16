<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %>
<% if(!"POST".equals(request.getMethod())) { response.setHeader("Allow","POST"); response.sendError(405); return; }
session.invalidate(); response.sendRedirect(ctx+"/view/staff/LoginPage1.jsp"); %>
