<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %><%@ include file="/WEB-INF/jspf/auth.jspf" %>
<% if("supplier".equals(request.getParameter("inStatisticType"))) {
    for(String attribute:new String[]{"supplierStatisticList","startDate","endDate","supplier","importInvoiceList","statContext","contextError"})
        session.removeAttribute(attribute);
    response.sendRedirect(ctx+"/view/managementStaff/SupplierStatisticPage1.jsp"); return;
} %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Chọn loại thống kê</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body><main class="narrow"><section class="panel"><h1>Chọn loại thống kê</h1><div class="body">
<form method="get" action="<%=ctx%>/view/managementStaff/StatisticSelectionPage1.jsp"><label for="inStatisticType">Loại thống kê</label><select id="inStatisticType" name="inStatisticType"><option disabled>Thống kê tài liệu theo số lượt mượn (chưa hiện thực)</option><option disabled>Thống kê bạn đọc theo số lần mượn (chưa hiện thực)</option><option value="supplier" selected>Thống kê nhà cung cấp theo số lượng tài liệu nhập</option></select><div class="buttons"><a id="subBack" class="btn" href="<%=ctx%>/view/managementStaff/ManagementStaffHomePage1.jsp">Quay lại</a><button id="subView" class="primary" type="submit">Xem</button></div></form>
</div></section></main></body></html>
