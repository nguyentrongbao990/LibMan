<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jspf" %>
<%
String[] names = {"inCccd","inFullName","inDateOfBirth","inAddress","inPhone","inEmail"};
String[] labels = {"CCCD","Họ và tên","Ngày sinh","Địa chỉ","Số điện thoại","Email"};
String[] types = {"text","text","date","text","tel","email"};
int[] lengths = {12,150,10,255,20,254};
String[] values = new String[6];
Set<String> invalid = new HashSet<>(); String error = ""; LocalDate birth = null;
for (int i=0;i<6;i++) values[i] = param(request,names[i]);
if ("POST".equals(request.getMethod())) {
    ArrayList<String> validationErrors = new ArrayList<>();
    ArrayList<String> missingFields = new ArrayList<>();
    for(int i=0;i<6;i++) {
        if(values[i].isBlank()) { invalid.add(names[i]); missingFields.add(labels[i]); }
        else if(values[i].length()>lengths[i]) {
            invalid.add(names[i]); validationErrors.add(labels[i]+" không được vượt quá "+lengths[i]+" ký tự.");
        }
    }
    if(!missingFields.isEmpty()) validationErrors.add(0,"Vui lòng nhập đầy đủ các trường bắt buộc: "+String.join(", ",missingFields)+".");
    if(!values[0].isBlank() && !values[0].matches("[0-9]{12}")) {
        invalid.add(names[0]); validationErrors.add("CCCD không hợp lệ. CCCD phải gồm đúng 12 chữ số.");
    }
    if(!values[2].isBlank()) {
        try { birth = LocalDate.parse(values[2]); if(birth.isAfter(LocalDate.now())) throw new DateTimeException("future"); }
        catch(DateTimeException e) { invalid.add(names[2]); validationErrors.add("Ngày sinh không hợp lệ. Vui lòng nhập ngày có thật và không lớn hơn ngày hiện tại."); }
    }
    if(!values[4].isBlank() && !values[4].matches("0[0-9]{9,10}")) {
        invalid.add(names[4]); validationErrors.add("Số điện thoại không hợp lệ. Vui lòng nhập 10–11 chữ số, bắt đầu bằng 0.");
    }
    if(!values[5].isBlank() && !values[5].matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
        invalid.add(names[5]); validationErrors.add("Email không hợp lệ. Vui lòng nhập địa chỉ email đúng định dạng.");
    }
    if(!invalid.isEmpty()) error=String.join(" ",validationErrors);
    else {
        try {
            boolean exists;
            try(ReaderDAO1 readerDAO = new ReaderDAO1()) { exists=readerDAO.findReaderByCccd(values[0])!=null; }
            if(exists) error="CCCD đã đăng ký thẻ bạn đọc.";
            else {
                Reader1 reader = new Reader1(); reader.setCccd(values[0]); reader.setFullName(values[1]);
                reader.setDateOfBirth(birth); reader.setAddress(values[3]); reader.setPhone(values[4]); reader.setEmail(values[5]);
                try(ReaderCardDAO1 cardDAO = new ReaderCardDAO1()) { reader=cardDAO.registerReaderCard(reader); }
                request.setAttribute("reader",reader);
                request.getRequestDispatcher("/view/reader/ReaderCardRegistrationResultPage1.jsp").forward(request,response); return;
            }
        } catch(IllegalArgumentException e) { error="CCCD đã đăng ký thẻ bạn đọc."; }
        catch(IllegalStateException e) { error="Đăng ký không thành công, vui lòng thử lại. Nếu lỗi tiếp diễn, liên hệ nhân viên thư viện."; }
    }
}
%>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Đăng ký thẻ bạn đọc</title><link rel="stylesheet" href="<%=ctx%>/assets/style.css"></head><body><main class="narrow"><section class="panel"><h1>Đăng ký thẻ bạn đọc</h1><div class="body">
<form method="post" action="<%=ctx%>/view/reader/ReaderCardRegistrationPage1.jsp">
<% for(int i=0;i<6;i++) { %><div class="field"><label for="<%=names[i]%>"><%=labels[i]%></label><input id="<%=names[i]%>" name="<%=names[i]%>" type="<%=types[i]%>" value="<%=h(values[i])%>" maxlength="<%=lengths[i]%>" required aria-invalid="<%=invalid.contains(names[i])%>" <%=i==0?"pattern=\"[0-9]{12}\" inputmode=\"numeric\"":i==4?"pattern=\"0[0-9]{9,10}\"":i==2?"max=\""+LocalDate.now()+"\"":""%>></div><% } %>
<div class="buttons"><a id="subCancelRegistration" class="btn" href="<%=ctx%>/view/reader/LibManHomePage1.jsp">Hủy</a><button id="subSaveRegistration" name="subSaveRegistration" class="primary" type="submit">Lưu đăng ký</button></div></form>
<% if(!error.isEmpty()) { %><div id="outErrorMessage" class="error" role="alert"><%=h(error)%></div><% } %>
</div></section></main></body></html>
