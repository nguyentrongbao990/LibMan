# Đối chiếu thiết kế → mã nguồn → kiểm chứng

Đối chiếu ngày 16/11/2026 (UTC+7). Nguồn chuẩn: SPEC/ACCEPTANCE/schema. Đã đọc văn bản PDF cũ 42 trang, render/xem thiết kế trang 35–42 và tất cả ảnh rời; không phục hồi thuộc tính JSP cũ, CRUD Entity, dấu dẫn xuất hay UNIQUE riêng từ ảnh cũ. Chưa có sơ đồ cuối khác được cung cấp để đối chiếu thêm.

| Nguồn | File | Phương thức / dữ liệu | Kiểm chứng |
|---|---|---|---|
| SPEC §3 | src/main/java/model/*1.java | 22 entity, private fields, arrays, inheritance đúng từng dòng SPEC | check_design.py PASS |
| SPEC §4 và xác nhận ERD cuối 16/11 | database/schema.sql; database/migrations/20261116_category_name_not_null.sql | 19 bảng; categoryName varchar(150) NOT NULL, các ràng buộc khác giữ nguyên | Precheck 0 dòng/0 NULL; ALTER đã commit; kết nối mới is_nullable=NO; NULL INSERT 23502/rollback; so sánh catalog/index/data PASS (evidence/category-name-not-null.txt) |
| SPEC §5 | src/main/java/dao/DAO1.java | abstract; protected Connection con; public DAO1(); close helper | Design check + compile PASS |
| RegisterReaderCard1 bước 5–7 | src/main/java/dao/ReaderDAO1.java | public Reader1 findReaderByCccd(String cccd) | JDBC/browser PASS |
| RegisterReaderCard1 bước 11–16 | src/main/java/dao/ReaderCardDAO1.java | public Reader1 registerReaderCard(Reader1 reader) | Commit/rollback/duplicate/race PASS |
| ViewSupplierStatistics1 | src/main/java/dao/SupplierStatisticDAO1.java | public SupplierStatistic1[] getSupplierStatistics(LocalDate startDate, LocalDate endDate) | SUM, 320/245/180, biên/kỳ khác PASS |
| ViewSupplierStatistics1 | src/main/java/dao/ImportInvoiceDAO1.java | public ImportInvoice1[] getImportInvoicesBySupplier(Supplier1 supplier, LocalDate startDate, LocalDate endDate) | 3 phiếu, supplier/staff PASS |
| ViewSupplierStatistics1 | src/main/java/dao/ImportInvoiceDetailDAO1.java | public ImportInvoiceDetail1[] getImportInvoiceDetails(ImportInvoice1 importInvoice) | 3 Document1, quantity/unitPrice/BigDecimal PASS |

## Đủ 22 entity

Các file dưới src/main/java/model mang đúng tên lớp tương ứng:

| Nhóm | Lớp | Đối chiếu |
|---|---|---|
| Nhân viên | Staff1, ManagementStaff1, LibraryStaff1 | Staff1 abstract, lớp con không field riêng/id lặp |
| Bạn đọc | Reader1, ReaderCard1 | readerCard:ReaderCard1, đủ trường SPEC |
| Tài liệu | Document1, DocumentCopy1 | documentAuthors[], documentCategories[], publisher; copy.document |
| Danh mục/quan hệ | Author1, Publisher1, Category1, DocumentAuthor1, DocumentCategory1 | Đúng tham chiếu, không CRUD |
| Mượn/trả | Loan1, LoanDetail1, ReturnReceipt1, ReturnDetail1 | Mảng detail, BigDecimal fine; chỉ model, không code nghiệp vụ ngoài đề |
| Nhập | Supplier1, ImportInvoice1, ImportInvoiceDetail1 | supplier/libraryStaff/details[]; không field thành tiền |
| Thống kê | ReaderStatistic1, DocumentStatistic1, SupplierStatistic1 | extends đúng; loanCount:int, importedVolume:long |

Getter/setter và constructor rỗng là phần kỹ thuật SPEC cho phép, không phải nghiệp vụ thêm. Check tự động so sánh tập lớp, mọi field/type/thứ tự và inheritance trực tiếp với bảng SPEC, không chỉ đếm file.

## RegisterReaderCard1 — 18 bước

Các JSP ở src/main/webapp/view/reader, DAO và model ở src/main/java.

| Bước | File / phương thức | Hành vi | Kiểm chứng |
|---|---|---|---|
| 1–2 | LibManHomePage1.jsp → ReaderCardRegistrationPage1.jsp | Link subRegisterReaderCard mở form | HTTP/browser ảnh 01–02 PASS |
| 3–4 | ReaderCardRegistrationPage1.jsp | Hiện 6 trường, POST; outErrorMessage gom lỗi cụ thể thiếu trường/CCCD/ngày sinh/phone/email/độ dài, giữ dữ liệu và đánh dấu lỗi | HTTP từng lỗi độc lập/giữ form/XSS PASS; review2-regression kiểm tra nội dung thông báo và aria-invalid PASS |
| 5–7 | JSP → ReaderDAO1.findReaderByCccd(cccd) | Khởi tạo DAO một lần; null chỉ khi không tìm thấy | JDBC + đăng ký mẫu lần đầu PASS |
| 8–10 | JSP → Reader1 | Tạo một Reader1, set 6 giá trị hợp lệ | Design check + result browser PASS |
| 11–12 | JSP → ReaderCardDAO1.registerReaderCard(reader) | Gọi trực tiếp đúng đối số/return type | JDBC integration PASS |
| 13–15 | ReaderCardDAO1 → ReaderCard1 | INSERT reader RETURNING id; một thẻ với RD+id, hôm nay, ACTIVE; INSERT card; commit | JDBC commit/rollback PASS |
| 16 | ReaderCardDAO1 | Sau commit gắn id/card vào reader rồi trả Reader1 | Lỗi giữa INSERT không có orphan/object success PASS |
| 17–18 | ReaderCardRegistrationResultPage1.jsp | Forward request reader; success + đủ 6 thông tin reader và 3 thẻ | Browser ảnh 03 PASS; direct result redirect PASS |
| Ngoại lệ | Form và 2 DAO | Hủy không lưu, lỗi giữ form, duplicate không thêm, UNIQUE phòng race | HTTP/JDBC Hủy/validation/2 POST/refresh PASS |

Self-message không trở thành đệ quy, không tạo đối tượng hai lần. Không PII trên URL. Forward giữ URL form; refresh POST báo trùng như hạn chế đã ghi.

## ViewSupplierStatistics1 — 32 bước

Các JSP ở src/main/webapp/view/managementStaff.

| Bước | File / phương thức | Hành vi | Kiểm chứng |
|---|---|---|---|
| 1–3 | ManagementStaffHomePage1.jsp → StatisticSelectionPage1.jsp | Xem thống kê; 3 lựa chọn | Browser ảnh 04–05, HTTP PASS |
| 4–7 | StatisticSelectionPage1.jsp → SupplierStatisticPage1.jsp | Chọn NCC, subView: bỏ ngữ cảnh thống kê cũ, mở form trống, không xóa auth; 2 ngày, subViewStatistics GET. Back từ list không qua thao tác reset này, giữ kỳ/kết quả | HTTP review2-regression PASS: lượt mới trống và vô hiệu link cũ, auth/session ID giữ nguyên; back giữ cả 2 ngày + 320/245/180 |
| 8–9 | SupplierStatisticPage1.jsp → SupplierStatisticDAO1.getSupplierStatistics(startDate,endDate) | JSP gọi DAO trực tiếp | JDBC/HTTP PASS |
| 10–12 | SupplierStatisticDAO1 → SupplierStatistic1 | Đóng gói đủ Supplier field + SUM quantity long | JDBC/MST/thứ tự/biên PASS |
| 13–14 | SupplierStatisticPage1.jsp | supplierStatisticList[] và bảng | 320/245/180; ảnh 06 PASS |
| 15–16 | SupplierStatisticPage1.jsp → SupplierImportInvoiceListPage1.jsp | Link id/context; chọn Supplier1 từ session, giữ ngày | HTTP fake id/kỳ cũ PASS |
| 17–18 | SupplierImportInvoiceListPage1.jsp → ImportInvoiceDAO1.getImportInvoicesBySupplier(supplier,startDate,endDate) | Mỗi truy cập hợp lệ đều gọi DAO đúng object/kỳ, không đọc lại mảng Session để bỏ qua truy vấn; ghi mảng mới vào Session sau thành công | JDBC/HTTP PASS; InvoiceListReloadChecks1 xác nhận SELECT mới khi chọn lại NCC và back từ chi tiết, PASS 16/11 lúc 19:52 |
| 19–21 | ImportInvoiceDAO1 → ImportInvoice1 | Đủ scalar + supplier + libraryStaff (không password) | JDBC staff LS001 PASS |
| 22–23 | SupplierImportInvoiceListPage1.jsp | Header 5 thông tin NCC, 2 ngày, 3 phiếu | HTTP/browser ảnh 07 PASS |
| 24–25 | List JSP → ImportInvoiceDetailPage1.jsp | Chọn ImportInvoice1 theo id/context trong mảng session | HTTP forged id PASS |
| 26–27 | Detail JSP → ImportInvoiceDetailDAO1.getImportInvoiceDetails(importInvoice) | Gọi trực tiếp | JDBC/HTTP PASS |
| 28–30 | ImportInvoiceDetailDAO1 → ImportInvoiceDetail1 | Mỗi detail có Document1 scalar/publisher | JDBC DOC001/2/3 PASS |
| 31–32 | ImportInvoiceDetailPage1.jsp | Gắn mảng vào importInvoice; thành tiền BigDecimal; tổng từ totalAmount | SQL độc lập 14.350.000; ảnh 08 top/bottom PASS |
| Ngoại lệ/back | 5 JSP + auth.jspf | Session auth; invalid/empty clears; back giữ kỳ/NCC | HTTP PASS |

## Ba nhóm thành phần JSP

| JSP | Component HTML | Dữ liệu server-side theo thiết kế | Biến cục bộ kỹ thuật |
|---|---|---|---|
| LibManHomePage1 | subRegisterReaderCard | Không có object nghiệp vụ | ctx |
| ReaderCardRegistrationPage1 | 6 input inCccd/inFullName/inDateOfBirth/inAddress/inPhone/inEmail; subSaveRegistration, subCancelRegistration, outErrorMessage | reader lúc đóng gói/forward | values, names, labels, types, lengths, invalid, validationErrors, missingFields, birth, error, exists, DAO |
| ReaderCardRegistrationResultPage1 | outSuccessMessage | reader:Reader1; đọc reader.readerCard | ctx |
| ManagementStaffHomePage1 | subViewStatistics | Auth trong session, không field managementStaff JSP | ctx |
| StatisticSelectionPage1 | inStatisticType, subView, subBack | Auth trong session; xóa chỉ ngữ cảnh thống kê khi chọn supplier | Giá trị lựa chọn, attribute trong vòng lặp reset |
| SupplierStatisticPage1 | inStartDate/inEndDate, subViewStatistics, outSupplierStatisticTable, subSelectSupplier, outErrorMessage, subBack | supplierStatisticList:SupplierStatistic1[]; LocalDate trong session | start/end, error, vòng lặp, DAO, context token |
| SupplierImportInvoiceListPage1 | outStartDate/outEndDate, outImportInvoiceTable, subSelectImportInvoice, subBack | supplier:Supplier1; importInvoiceList:ImportInvoice1[] từ DAO mỗi truy cập hợp lệ, lưu Session cho trang chi tiết | id, error, context, DAO; không còn previous/nhánh cache |
| ImportInvoiceDetailPage1 | outImportInvoiceDetailTable, subBack | importInvoice:ImportInvoice1; supplier/details lấy từ object đó | id, context, selected kiểm tra membership, error, d trong vòng lặp |

Link lặp từng dòng dùng data-component đúng tên để tránh trùng HTML id. Không có hidden object, field outTotalAmount, outReaderInfo/outReaderCardInfo hay field managementStaff cũ. JSPF common là helper escape/date/money/param; auth.jspf kiểm tra session, không thêm Java tầng mới. Login link/home và Logout form/home quản lý là UI hỗ trợ đã xác nhận.

## Auth và bàn giao

| Bổ sung | File | Kết quả |
|---|---|---|
| Plaintext demo localhost đã được user chọn | dao/StaffDAO1.java | login(String username,String password):ManagementStaff1, JOIN role/PreparedStatement; object không password; JDBC PASS |
| Login/logout | view/staff/LoginPage1.jsp, LogoutPage1.jsp | Tạo session mới sau invalidate, POST logout; HTTP PASS |
| Build/deploy | pom.xml; WEB-INF/web.xml; tools/start-demo.ps1, stop-demo.ps1 | WAR và Tomcat 11 HTTP 18080 PASS |
| DB setup | tools/DatabaseSetup1.java | Chỉ tạo libman riêng, kiểm tra trống trước schema/seed; PASS |
| Chứng cứ | docs/evidence/* | 8 JSP, Login, cấu trúc IntelliJ, build/design/JDBC/constraint/HTTP output |
| IDE Run/Debug | README.md | Project mở/cấu trúc PASS; thực Run/Debug Tomcat từ IntelliJ NOT RUN |
