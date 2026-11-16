<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %><%@ include file="/WEB-INF/jspf/auth.jspf" %>
<%
ImportInvoice1 importInvoice=null; String context=(String)session.getAttribute("statContext"); String error="";
try {
    if(context==null || !context.equals(param(request,"context"))) throw new IllegalArgumentException();
    long id=Long.parseLong(param(request,"id"));
    ImportInvoice1[] invoices=(ImportInvoice1[])session.getAttribute("importInvoiceList");
    Supplier1 selected=(Supplier1)session.getAttribute("supplier");
    if(invoices==null || selected==null || session.getAttribute("startDate")==null || session.getAttribute("endDate")==null) throw new IllegalArgumentException();
    for(ImportInvoice1 item:invoices) if(item.getId()==id && item.getSupplier().getId()==selected.getId()) importInvoice=item;
    if(importInvoice==null) throw new IllegalArgumentException();
    try(ImportInvoiceDetailDAO1 dao=new ImportInvoiceDetailDAO1()) { importInvoice.setImportInvoiceDetails(dao.getImportInvoiceDetails(importInvoice)); }
} catch(IllegalArgumentException e) { session.setAttribute("contextError",true); response.sendRedirect(ctx+"/view/managementStaff/SupplierStatisticPage1.jsp"); return; }
catch(IllegalStateException e) { error="Không lấy được chi tiết phiếu nhập. Vui lòng thử lại."; importInvoice.setImportInvoiceDetails(null); }
%>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Chi tiết phiếu nhập</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body><main><section class="panel"><h1>Chi tiết phiếu nhập</h1><div class="body">
<h2>Thông tin phiếu nhập</h2><dl><dt>Mã phiếu nhập</dt><dd><%=h(importInvoice.getInvoiceCode())%></dd><dt>Ngày nhập</dt><dd><%=date(importInvoice.getImportDate())%></dd><dt>Nhân viên lập phiếu</dt><dd><%=h(importInvoice.getLibraryStaff().getStaffCode())%> – <%=h(importInvoice.getLibraryStaff().getFullName())%></dd></dl>
<h2>Thông tin nhà cung cấp</h2><dl><dt>Tên nhà cung cấp</dt><dd><%=h(importInvoice.getSupplier().getSupplierName())%></dd><dt>Mã số thuế</dt><dd><%=h(importInvoice.getSupplier().getTaxCode())%></dd><dt>Địa chỉ</dt><dd><%=h(importInvoice.getSupplier().getAddress())%></dd><dt>Số điện thoại</dt><dd><%=h(importInvoice.getSupplier().getPhone())%></dd><dt>Email</dt><dd><%=h(importInvoice.getSupplier().getEmail())%></dd></dl>
<h2>Danh sách tài liệu nhập</h2><div class="scroll"><table id="outImportInvoiceDetailTable"><thead><tr><th>TT</th><th>Mã tài liệu</th><th>Tên tài liệu</th><th>Số lượng</th><th>Đơn giá (VNĐ)</th><th>Thành tiền (VNĐ)</th></tr></thead><tbody>
<% if(importInvoice.getImportInvoiceDetails()!=null) for(int i=0;i<importInvoice.getImportInvoiceDetails().length;i++) { ImportInvoiceDetail1 d=importInvoice.getImportInvoiceDetails()[i]; %><tr><td><%=i+1%></td><td><%=h(d.getDocument().getDocumentCode())%></td><td><%=h(d.getDocument().getTitle())%></td><td class="num"><%=d.getQuantity()%></td><td class="num"><%=money(d.getUnitPrice())%></td><td class="num"><%=money(d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))%></td></tr><% } %>
</tbody></table></div><div class="total"><span>Tổng tiền</span><span><%=money(importInvoice.getTotalAmount())%> VNĐ</span></div>
<% if(!error.isEmpty()) { %><p class="error" role="alert"><%=h(error)%></p><% } %><div class="buttons"><a id="subBack" class="btn" href="<%=ctx%>/view/managementStaff/SupplierImportInvoiceListPage1.jsp?id=<%=importInvoice.getSupplier().getId()%>&amp;context=<%=h(context)%>">Quay lại</a></div>
</div></section></main></body></html>
