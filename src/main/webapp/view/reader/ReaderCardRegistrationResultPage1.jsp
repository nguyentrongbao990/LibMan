<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %>
<% Reader1 reader=(Reader1)request.getAttribute("reader");
if(reader==null || reader.getReaderCard()==null) { response.sendRedirect(ctx+"/view/reader/ReaderCardRegistrationPage1.jsp"); return; } %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Kết quả đăng ký</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body><main class="narrow"><section class="panel"><h1>Kết quả đăng ký thẻ bạn đọc</h1><div class="body">
<div id="outSuccessMessage" class="ok">Đăng ký thành công</div><h2>Thông tin bạn đọc</h2><dl>
<dt>CCCD</dt><dd><%=h(reader.getCccd())%></dd><dt>Họ và tên</dt><dd><%=h(reader.getFullName())%></dd><dt>Ngày sinh</dt><dd><%=date(reader.getDateOfBirth())%></dd><dt>Địa chỉ</dt><dd><%=h(reader.getAddress())%></dd><dt>Số điện thoại</dt><dd><%=h(reader.getPhone())%></dd><dt>Email</dt><dd><%=h(reader.getEmail())%></dd></dl>
<h2>Thông tin thẻ bạn đọc</h2><dl><dt>Mã thẻ</dt><dd><%=h(reader.getReaderCard().getCardCode())%></dd><dt>Ngày cấp</dt><dd><%=date(reader.getReaderCard().getIssueDate())%></dd><dt>Trạng thái</dt><dd><%=h(reader.getReaderCard().getStatus())%></dd></dl>
</div></section></main></body></html>
