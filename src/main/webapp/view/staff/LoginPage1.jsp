<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %>
<%
String error="", username=param(request,"username");
if("POST".equals(request.getMethod())) {
    String password=request.getParameter("password");
    if(username.isEmpty() || username.length()>100 || password==null || password.isEmpty() || password.length()>255)
        error="Vui lòng nhập tên đăng nhập và mật khẩu hợp lệ.";
    else try(StaffDAO1 dao=new StaffDAO1()) {
        ManagementStaff1 staff=dao.login(username,password);
        if(staff==null) error="Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản không có quyền quản lý.";
        else {
            session.invalidate(); session=request.getSession(true); session.setAttribute("auth",staff);
            response.sendRedirect(ctx+"/view/managementStaff/ManagementStaffHomePage1.jsp"); return;
        }
    } catch(IllegalStateException e) { error="Không thể đăng nhập lúc này. Vui lòng thử lại."; }
}
%>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Đăng nhập quản lý</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body><main class="narrow"><section class="panel"><h1>Đăng nhập nhân viên quản lý</h1><div class="body">
<form method="post" action="<%=ctx%>/view/staff/LoginPage1.jsp"><div class="field"><label for="username">Tên đăng nhập</label><input id="username" name="username" maxlength="100" value="<%=h(username)%>" autocomplete="username" required></div><div class="field"><label for="password">Mật khẩu</label><input id="password" name="password" type="password" maxlength="255" autocomplete="current-password" required></div><div class="buttons"><a class="btn" href="<%=ctx%>/view/reader/LibManHomePage1.jsp">Trang chủ</a><button type="submit" class="primary">Đăng nhập</button></div></form>
<% if(!error.isEmpty()) { %><p class="error" role="alert"><%=h(error)%></p><% } %></div></section></main></body></html>
