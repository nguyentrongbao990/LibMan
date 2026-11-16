# Bằng chứng chạy thực tế

Ngày 16/11/2026, dữ liệu demo, PostgreSQL libman và Tomcat localhost 18080.

| File | Nội dung |
|---|---|
| 01-home.png | LibManHomePage1.jsp |
| 02-registration.png | ReaderCardRegistrationPage1.jsp đã nhập mẫu, trước khi lưu |
| 03-registration-result.png | ReaderCardRegistrationResultPage1.jsp sau transaction commit; forward nên URL trình duyệt vẫn là form |
| 04-management-home.png | ManagementStaffHomePage1.jsp |
| 05-statistic-selection.png | StatisticSelectionPage1.jsp |
| 06-supplier-statistics.png | SupplierStatisticPage1.jsp, 320/245/180 |
| 07-supplier-invoices.png | SupplierImportInvoiceListPage1.jsp, 3 phiếu Minh Triết |
| 08-invoice-detail-top.png | ImportInvoiceDetailPage1.jsp phần header |
| 08-invoice-detail-bottom.png | Cùng trang chi tiết, bảng 3 dòng và tổng tiền |
| 09-login.png | LoginPage1.jsp hỗ trợ |
| 10-intellij-project.png | Cấu trúc src/main/java/dao, model và webapp/view trong IntelliJ |
| build.txt | Maven clean package cuối |
| design-check.txt | Đối chiếu declaration với SPEC |
| jdbc-checks.txt | 32 assertion JDBC/SQL độc lập/HTTP Hủy |
| constraint-checks.txt | Constraint thật, fixture rollback |
| http-checks.txt | 35 assertion HTTP |
| invoice-list-reload-regression.txt | Build/deploy và hồi quy sau review 19:52: 9 assertion query mới, 32 JDBC, 6 constraint và 35 HTTP; các file txt khác giữ lịch sử ban đầu |
| review2-regression.txt | Build/deploy review lần hai 20:05: 63 HTTP (thông báo lỗi/giữ giá trị/đánh dấu lỗi, lượt NCC mới so với Back, auth), 9 reload, 32 JDBC, 6 constraint; design check/checksum/log Tomcat |
| category-name-not-null.txt | Precheck 0 dòng/0 NULL, ALTER categoryName NOT NULL đã commit; NULL INSERT 23502/rollback, đối chiếu constraints/cột khác/index/data và xác minh bằng kết nối mới |
| repository-preflight.txt | Git release: build source export sạch từ index, schema/seed thử rollback, deploy Tomcat base riêng18081 và smoke HTTP auth/SELECT; không reset libman |

Ảnh chụp từ trình duyệt/native UI, không dùng wireframe để giả làm ứng dụng. Input date trên máy browser có locale US; nhãn ngày và kết quả do server format dd/MM/yyyy. Ảnh chỉ chứa tài khoản/dữ liệu demo, không có credential PostgreSQL.
