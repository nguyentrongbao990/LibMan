<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %><%@ include file="/WEB-INF/jspf/auth.jspf" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Trang chủ quản lý</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body>
<div class="support"><form method="post" action="<%=ctx%>/view/staff/LogoutPage1.jsp"><button type="submit">Đăng xuất</button></form></div>
<main class="narrow"><section class="panel"><h1>Trang chủ quản lý</h1><div class="body center"><a id="subViewStatistics" class="btn primary" href="<%=ctx%>/view/managementStaff/StatisticSelectionPage1.jsp">Xem thống kê</a></div></section></main></body></html>
