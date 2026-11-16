<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %><%@ include file="/WEB-INF/jspf/auth.jspf" %>
<%
String start=param(request,"inStartDate"), end=param(request,"inEndDate"), error="";
SupplierStatistic1[] supplierStatisticList=(SupplierStatistic1[])session.getAttribute("supplierStatisticList");
LocalDate startDate=(LocalDate)session.getAttribute("startDate"), endDate=(LocalDate)session.getAttribute("endDate");
if(request.getParameter("subViewStatistics")!=null || request.getParameter("inStartDate")!=null || request.getParameter("inEndDate")!=null) {
    session.removeAttribute("supplierStatisticList"); session.removeAttribute("startDate"); session.removeAttribute("endDate");
    session.removeAttribute("supplier"); session.removeAttribute("importInvoiceList"); session.removeAttribute("statContext");
    supplierStatisticList=null;
    try {
        startDate=LocalDate.parse(start); endDate=LocalDate.parse(end);
        if(startDate.isAfter(endDate)) throw new DateTimeException("range");
        try(SupplierStatisticDAO1 dao=new SupplierStatisticDAO1()) { supplierStatisticList=dao.getSupplierStatistics(startDate,endDate); }
        session.setAttribute("supplierStatisticList",supplierStatisticList); session.setAttribute("startDate",startDate); session.setAttribute("endDate",endDate);
        session.setAttribute("statContext",UUID.randomUUID().toString());
    } catch(DateTimeException e) { error="Cần chọn đầy đủ ngày hợp lệ; ngày bắt đầu không được lớn hơn ngày kết thúc."; }
    catch(IllegalStateException e) { error="Không lấy được thống kê. Vui lòng thử lại."; }
} else {
    start=startDate==null?"":startDate.toString(); end=endDate==null?"":endDate.toString();
    if(session.getAttribute("contextError")!=null) { error="Ngữ cảnh thống kê đã hết hạn hoặc không khớp. Vui lòng chọn lại."; session.removeAttribute("contextError"); }
}
if(supplierStatisticList!=null && supplierStatisticList.length==0) error="Không có dữ liệu nhập tài liệu trong khoảng thời gian đã chọn";
%>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Thống kê nhà cung cấp</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body><main><section class="panel"><h1>Thống kê nhà cung cấp</h1><div class="body">
<form method="get" action="<%=ctx%>/view/managementStaff/SupplierStatisticPage1.jsp"><div class="pair"><div><label for="inStartDate">Ngày bắt đầu</label><input id="inStartDate" name="inStartDate" type="date" value="<%=h(start)%>" required></div><div><label for="inEndDate">Ngày kết thúc</label><input id="inEndDate" name="inEndDate" type="date" value="<%=h(end)%>" required></div></div><div class="buttons"><button id="subViewStatistics" name="subViewStatistics" value="1" type="submit" class="primary">Xem thống kê</button></div></form>
<div class="scroll"><table id="outSupplierStatisticTable"><thead><tr><th>TT</th><th>Tên nhà cung cấp</th><th>Mã số thuế</th><th>Tổng số lượng tài liệu nhập</th></tr></thead><tbody>
<% if(supplierStatisticList!=null) for(int i=0;i<supplierStatisticList.length;i++) { SupplierStatistic1 s=supplierStatisticList[i]; %><tr><td><%=i+1%></td><td><a data-component="subSelectSupplier" href="<%=ctx%>/view/managementStaff/SupplierImportInvoiceListPage1.jsp?id=<%=s.getId()%>&amp;context=<%=h(session.getAttribute("statContext"))%>"><%=h(s.getSupplierName())%></a></td><td><%=h(s.getTaxCode())%></td><td class="num"><%=s.getImportedVolume()%></td></tr><% } %>
</tbody></table></div><div class="buttons"><a id="subBack" class="btn" href="<%=ctx%>/view/managementStaff/StatisticSelectionPage1.jsp">Quay lại</a></div><% if(!error.isEmpty()) { %><div id="outErrorMessage" class="error" role="alert"><%=h(error)%></div><% } %>
</div></section></main></body></html>
