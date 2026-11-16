<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %><%@ include file="/WEB-INF/jspf/auth.jspf" %>
<%
Supplier1 supplier=null; ImportInvoice1[] importInvoiceList=null;
LocalDate startDate=(LocalDate)session.getAttribute("startDate"), endDate=(LocalDate)session.getAttribute("endDate");
SupplierStatistic1[] contextList=(SupplierStatistic1[])session.getAttribute("supplierStatisticList");
String context=(String)session.getAttribute("statContext"); String error="";
try {
    if(context==null || !context.equals(param(request,"context")) || contextList==null || startDate==null || endDate==null) throw new IllegalArgumentException();
    long id=Long.parseLong(param(request,"id"));
    for(SupplierStatistic1 item:contextList) if(item.getId()==id) supplier=item;
    if(supplier==null) throw new IllegalArgumentException();
    session.removeAttribute("importInvoiceList"); session.removeAttribute("supplier");
    try(ImportInvoiceDAO1 dao=new ImportInvoiceDAO1()) { importInvoiceList=dao.getImportInvoicesBySupplier(supplier,startDate,endDate); }
    session.setAttribute("supplier",supplier); session.setAttribute("importInvoiceList",importInvoiceList);
} catch(IllegalArgumentException e) { session.setAttribute("contextError",true); response.sendRedirect(ctx+"/view/managementStaff/SupplierStatisticPage1.jsp"); return; }
catch(IllegalStateException e) { error="Không lấy được danh sách phiếu nhập. Vui lòng thử lại."; }
%>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Danh sách phiếu nhập</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body><main><section class="panel"><h1>Danh sách phiếu nhập</h1><div class="body">
<h2>Thông tin nhà cung cấp</h2><dl><dt>Tên nhà cung cấp</dt><dd><%=h(supplier.getSupplierName())%></dd><dt>Mã số thuế</dt><dd><%=h(supplier.getTaxCode())%></dd><dt>Địa chỉ</dt><dd><%=h(supplier.getAddress())%></dd><dt>Số điện thoại</dt><dd><%=h(supplier.getPhone())%></dd><dt>Email</dt><dd><%=h(supplier.getEmail())%></dd></dl>
<h2>Khoảng thời gian thống kê</h2><dl><dt>Ngày bắt đầu</dt><dd id="outStartDate"><%=date(startDate)%></dd><dt>Ngày kết thúc</dt><dd id="outEndDate"><%=date(endDate)%></dd></dl>
<h2>Danh sách phiếu nhập</h2><table id="outImportInvoiceTable"><thead><tr><th>TT</th><th>Mã phiếu nhập</th><th>Ngày nhập</th></tr></thead><tbody>
<% if(importInvoiceList!=null) for(int i=0;i<importInvoiceList.length;i++) { ImportInvoice1 invoice=importInvoiceList[i]; %><tr><td><%=i+1%></td><td><a data-component="subSelectImportInvoice" href="<%=ctx%>/view/managementStaff/ImportInvoiceDetailPage1.jsp?id=<%=invoice.getId()%>&amp;context=<%=h(context)%>"><%=h(invoice.getInvoiceCode())%></a></td><td><%=date(invoice.getImportDate())%></td></tr><% } %>
</tbody></table><% if(!error.isEmpty()) { %><p class="error" role="alert"><%=h(error)%></p><% } %><div class="buttons"><a id="subBack" class="btn" href="<%=ctx%>/view/managementStaff/SupplierStatisticPage1.jsp">Quay lại</a></div>
</div></section></main></body></html>
