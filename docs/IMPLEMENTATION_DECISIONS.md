# Nhật ký quyết định triển khai (Codex cập nhật)

| Vấn đề | Phương án | Lý do | Ảnh hưởng | Trạng thái |
|---|---|---|---|---|
| Phạm vi | Hai module + auth hỗ trợ | Đề bài và deadline | Không hiện thực các chức năng ngoài đề | Đã thống nhất |
| Kiến trúc | JSP gọi DAO trực tiếp, PostgreSQL, Maven WAR | Khớp Class/Sequence Diagram | Không service/controller riêng | Đã thống nhất |
| UNIQUE | 4 UNIQUE ghép; ReturnDetail bỏ UNIQUE trên FK receipt | Đúng nghiệp vụ | SQL phải khớp | Đã thống nhất |
| Kiểu ô nhập | text trong UML; date/tel/email HTML nếu phù hợp | Giống case_study | Không đổi Entity | Đã thống nhất |
| Chi tiết phiếu | Thành tiền tính BigDecimal, tổng lấy totalAmount | Giúp người dùng không tự cộng | Không đổi schema | Đã thống nhất |
| ID, mã thẻ | Identity, RD + id tối thiểu 6 chữ số | Đơn giản, unique, tránh MAX+1 | Mã có thể không liên tục | Đề xuất trong SPEC |
| Định danh SQL | Quoted để giữ chữ hoa/thường | Soi thiết kế ↔ code | DAO phải quote nhất quán | Đề xuất trong SPEC |
| NXB nullable | FK nullable theo đặc tả | Cho phép chưa xác định NXB | ERD cuối cần đồng bộ nếu chưa sửa | Cần xác nhận nếu khác sơ đồ cuối |
| Ngữ cảnh TK | Một context session mỗi phiên | Giữ 3 DAO đúng thiết kế | Hạn chế nhiều tab song song | Đề xuất trong SPEC |
| Auth | JSP phụ trợ, StaffDAO1 nếu cần; băm BCrypt đề xuất, plaintext chỉ demo local nếu user chọn | Đơn giản, an toàn tương xứng | Không nằm trong sơ đồ 2 module | Cần xác nhận phương án mật khẩu |
| Deployment | Chỉ ghi cấu hình được kiểm chứng | Không khẳng định HTTPS/máy chủ không có | Vẽ sau khi chạy | Đã thống nhất |

Bổ sung các hàng khi phát sinh quyết định; không xóa lịch sử. Cập nhật trạng thái theo xác nhận thực tế. Không ghi bí mật vào file này.

## Chi tiết triển khai 16/11/2026

| Vấn đề | Phương án | Lý do | Ảnh hưởng | Trạng thái |
|---|---|---|---|---|
| Runtime | JDK 24.0.2; Maven 3.9.15 có sẵn; Tomcat 11.0.22 đã xác minh bằng ServerInfo; bytecode release 17 | Tomcat 11 dùng Jakarta; không đổi cài đặt toàn máy | JSP dùng jakarta.servlet, web.xml 6.1; không thêm servlet/controller | Đã kiểm chứng môi trường, chi tiết kỹ thuật trong phạm vi |
| Entity | Constructor rỗng và getter/setter cho đúng private field SPEC §3 | JSP/DAO đóng gói dữ liệu | Không thêm field, nghiệp vụ, CRUD; generator chỉ đọc bảng SPEC | Đã triển khai theo quyền cho phép trong SPEC |
| Vòng đời JDBC | DAO1 AutoCloseable; mỗi instance mở Connection; JSP try-with-resources | Không chia sẻ connection, đóng tài nguyên | IllegalStateException cho lỗi DB; null chỉ khi không tìm thấy | Đã triển khai theo SPEC |
| Cấu hình | LIBMAN_DB_URL/USER/PASSWORD hoặc file Properties qua LIBMAN_DB_CONFIG/-Dlibman.db.config | Secret ở máy cục bộ, ngoài Git | .local/ và config/db.properties bị ignore; không ghi credential vào nguồn | Đã triển khai, chưa có credential DB |
| Mã thẻ | Identity + RD định dạng tối thiểu 6 chữ số từ reader.id; ngày LocalDate.now(), ACTIVE | Đúng phương án SPEC, chống MAX+1/race | Có khoảng trống mã; chỉ gắn id/card vào reader sau commit | Đã triển khai phương án kỹ thuật SPEC; chưa thay đổi thiết kế |
| Validation điện thoại | 0 + 9–10 chữ số (10–11 số nội địa) | Đơn giản, chấp nhận cả số cố định trong mẫu; SPEC cho phép đề xuất 10–11 | Không kiểm tra đầu số nhà mạng, không giới hạn tuổi | Chi tiết bổ sung; đã triển khai, báo user |
| Trả kết quả đăng ký | Request attribute reader và forward | Giữ PII khỏi URL, khớp 18 bước | Refresh POST bị kiểm tra CCCD/UNIQUE từ chối; chưa dùng PRG/flash | Đã chọn phương án SPEC cho phép |
| Lỗi duplicate | PostgreSQL SQLState 23505 + đúng constraint tblReader_cccd_key | Không đánh đồng lỗi lưu khác với CCCD trùng | Runtime exception, rollback cả hai INSERT | Đã triển khai |
| Thống kê | SUM(quantity), ngày inclusive; tie id ASC; phiếu ngày/id ASC | Theo SPEC, kết quả ổn định | Không hardcode thống kê, không sum tiền | Đã triển khai phương án SPEC |
| Session thống kê | Mảng kết quả, LocalDate, supplier/list phiếu trong session; token context UUID trên link | Đúng chữ ký DAO, chặn id ngoài kỳ và link kỳ cũ | Một kỳ đang hoạt động mỗi session; đa tab có thể hết ngữ cảnh; token không phải đối tượng HTML ẩn | Đã triển khai phương án SPEC |
| Dữ liệu hiển thị | Escape HTML trong mọi output động; money NumberFormat vi-VN tối đa 2 số lẻ | Chặn XSS, không mất phần lẻ numeric(18,2) | Thành tiền tính BigDecimal ở JSP, không thêm Entity field/cột | Đã triển khai |
| Quan hệ Document | Chi tiết load scalar document và publisher nếu có, không load tác giả/thể loại | SPEC cho phép không tải quan hệ không dùng | Các array không dùng giữ null, không giả dữ liệu | Đã triển khai |
| Seed bổ sung | PN000101=90 (50 DOC001+40 DOC002), PN000118=110 (60 DOC001+50 DOC003); Tri Thức 100 ngày 01/01 +145 ngày 31/03; Sao Việt 180; ngoài kỳ 7 và 9 | Đủ biên và tổng 320/245/180; tổng tiền tính từ detail | Seed một lần DB trống, không seed Reader mẫu, không xóa dữ liệu | Đã triển khai; auth seed đang chờ quyết định |
| Git | Thư mục đang kế thừa Git ở C:/Users/Admin | Không stage nhầm dữ liệu ngoài dự án | Chưa init/commit/push; chỉ chỉnh file trong workspace | Đã phát hiện; không thao tác repo cha |
| Auth | Câu hỏi BCrypt + StaffDAO1.login và 2 JSP hỗ trợ đã gửi | SPEC §9 yêu cầu user chọn | Chưa cài dependency hay hiện thực phương án mật khẩu khi chưa xác nhận | Đang chờ user |

## Cập nhật xác nhận và kết quả cuối

Các hàng lịch sử “đang chờ” ở trên phản ánh thời điểm trước khi nhận trả lời; trạng thái hiện hành nằm trong bảng sau.

| Vấn đề | Phương án hiện hành | Lý do | Ảnh hưởng | Trạng thái |
|---|---|---|---|---|
| Auth/mật khẩu | Plaintext chỉ tài khoản demo localhost; StaffDAO1.login(String username,String password):ManagementStaff1; LoginPage1.jsp/LogoutPage1.jsp | User trả lời chọn “Mật khẩu rõ, chỉ tài khoản demo localhost” | Không BCrypt/dependency auth; JOIN tblManagementStaff; object trả về không mang password; không dùng credential thật | Đã xác nhận và triển khai, JDBC/HTTP PASS |
| Session auth | invalidate session cũ rồi tạo session mới; role qua kiểu ManagementStaff1 từ JOIN DB; logout POST invalidate | Đơn giản theo SPEC; không thêm bảng role/JWT/framework | Mọi JSP quản lý include auth; GET logout 405 | Đã triển khai theo SPEC, HTTP PASS |
| Database | libman riêng trên PostgreSQL đã cài; setup chỉ CREATE DATABASE nếu chưa có và chỉ chạy schema/seed khi không có user objects | User yêu cầu tên libman, không sửa/xóa DB khác | DatabaseSetup1 ở tools, không production layer; credential được user điền .local/db.properties | Đã xác nhận; tạo DB/schema/seed thành công trên PostgreSQL 18.2 |
| Seed auth | manager_demo/LibManDemo1! (MS001); library_demo/LibraryDemo1! (LS001) | Hoàn toàn demo; tài khoản thư viện để test sai vai trò | Chỉ quản lý đăng nhập được; seed một lần, không reset/update dữ liệu hiện có | Đã triển khai theo phương án plaintext user chọn |
| Tomcat demo | CATALINA_BASE .local/tomcat; HTTP address 127.0.0.1, port 18080; dùng Tomcat 11 có sẵn | Cổng 8080 thử bind bị từ chối; 18080 đã probe và test HTTP | Không sửa Tomcat gốc/PATH/PostgreSQL service; script start/stop kiểm tra process/base | Đã kiểm chứng deployment |
| Encoding JSP | pageEncoding UTF-8 trực tiếp trên từng JSP và jsp-property-group; request/response UTF-8 | Browser phát hiện mojibake khi chỉ đặt directive trong include | Không thay thiết kế; sau sửa browser/HTTP đúng tiếng Việt | Đã sửa và kiểm chứng |
| Kỹ thuật hiển thị | Các link lặp dùng data-component để giữ tên subSelectSupplier/subSelectImportInvoice mà không lặp HTML id; CSS dùng panel từng trang | Khớp component wireframe; không dùng ảnh ghép làm web | Input date ISO; locale của browser quyết định UI date picker, nhãn kết quả dd/MM/yyyy | Đã triển khai/kiểm tra ảnh |
| Helper JSP | common.jspf: h/param/date/money; auth.jspf: session guard; static include | Không lặp escape/format/auth giữa JSP | Không tạo package view Java hay service/controller; đối tượng nghiệp vụ vẫn server-side | Được SPEC cho phép helper kỹ thuật, đã ghi nhận |
| Kiểm thử rollback | Proxy Connection trong subclass FailCardDAO1 ở tools/IntegrationChecks1.java; SQLException trước INSERT thẻ | Chứng minh rollback thật mà không thêm hook/trigger vào production | Test reader ngẫu nhiên hoàn toàn demo; constraint fixtures transaction rollback | Đã chạy PASS |
| Kiểm thử và dữ liệu demo | Test registration/HTTP thêm reader demo, không xóa; mẫu 001203012345 được đăng ký bằng browser | Chứng minh chạy thật/đủ 9 trường | CCCD mẫu hiện đã tồn tại; RD000006 do ID đã được dùng trong test/rollback; hướng dẫn dùng CCCD demo khác khi demo lại | Đã thực hiện; không reset database |
| Run/Debug IDE | Kiểm tra project đang mở trong IntelliJ, chụp cấu trúc; hướng dẫn cấu hình Tomcat trong README | Tách bằng chứng runtime từ script với hướng dẫn IDE | Không tuyên bố đã debug Tomcat từ IDE; không đổi settings hệ thống | Project inspection PASS; IDE Run/Debug NOT RUN |
| Phiên bản source và triển khai | WAR build cuối deploy riêng; checksum build/deploy được so sánh | Đảm bảo test đúng bản build bàn giao | Không push/commit/submit | Build/JDBC/constraint/HTTP PASS |

Không có thay đổi field/type/chữ ký nghiệp vụ, schema hoặc kiến trúc đã chốt. NXB nullable giữ nguyên DDL/SPEC; không phục hồi NOT NULL từ ảnh cũ. Phạm vi HTTP localhost và các giới hạn refresh POST/một kỳ session/quan hệ Document không tải sâu đã được ghi rõ trong README và TRACEABILITY.

## Sửa sau review độc lập — 16/11/2026, 19:52 (UTC+7)

| Vấn đề | Phương án | Lý do | Ảnh hưởng | Trạng thái |
|---|---|---|---|---|
| Tái sử dụng importInvoiceList khi chọn lại cùng NCC | Bỏ biến previous và nhánh đọc lại list từ Session. Sau kiểm tra auth/context/id, luôn gọi ImportInvoiceDAO1.getImportInvoicesBySupplier(supplier,startDate,endDate). | Bản trước bỏ qua lời gọi DAO khi cùng NCC, không khớp Activity/Sequence Diagram module 2, bước 17–23. User yêu cầu sửa trực tiếp. | Mỗi truy cập hợp lệ, kể cả Quay lại từ chi tiết, truy vấn lại DB. Session vẫn giữ kỳ thống kê, supplier và list mới cho trang chi tiết; không sửa UI/DAO/Entity/DDL/kiến trúc. | Đã được user yêu cầu/xác nhận; triển khai và hồi quy PASS |
| Danh sách cũ khi truy vấn lỗi | Xóa supplier/importInvoiceList cũ trước lời gọi DAO, chỉ lưu lại sau khi query thành công | Không cho trang chi tiết dùng dữ liệu cũ khi lần tải mới thất bại | Giữ nguyên xử lý lỗi và giao diện hiện có; context thống kê và ngày không bị xóa | Đã triển khai trong bản sửa |
| Chứng minh lời gọi query mỗi lần | Harness tools/InvoiceListReloadChecks1.java dùng HTTP session thật và PostgreSQL lock tạm thời; kiểm tra SELECT bị chặn bằng pg_locks/pg_blocking_pids, giải phóng bằng rollback | Test giao diện/back thông thường trước đây không phân biệt dữ liệu cache và truy vấn mới | Test-only, không thêm hook production; 2 tình huống chọn lại NCC/back chi tiết; không ghi dữ liệu DB | 9 assertion PASS; bản test ban đầu phát hiện lỗi trên WAR cũ |

Bản ZIP LibMan_REVIEW.zip đã bàn giao trước bản sửa được giữ nguyên như mốc review, chưa tự đóng gói lại hoặc ghi đè.

## Hoàn thiện sau review độc lập lần hai — 16/11/2026, 20:05 (UTC+7)

| Vấn đề | Phương án | Lý do | Ảnh hưởng | Trạng thái |
|---|---|---|---|---|
| Validation đăng ký chỉ có thông báo chung | Gom thông báo cụ thể vào outErrorMessage: liệt kê trường bắt buộc thiếu, CCCD đúng 12 số, ngày sinh có thật/không tương lai, phone 10–11 số bắt đầu 0, email đúng định dạng. Độ dài vẫn kiểm tra và báo tên trường/giới hạn. | User yêu cầu bám sát ngoại lệ SPEC; cần biết lỗi để sửa. | Chỉ thêm ArrayList String cục bộ validationErrors/missingFields trong JSP, không thêm component/Entity/API/tầng mới. Giữ trim, escape dữ liệu và aria-invalid. Nhiều lỗi hiển thị cùng nhãn; blank chỉ báo thiếu trường, không báo thêm sai định dạng. | Đã yêu cầu/xác nhận; HTTP nội dung thông báo/giữ dữ liệu/đánh dấu lỗi PASS |
| Bắt đầu lượt NCC mới còn hiện kết quả lượt trước | Chỉ khi StatisticSelectionPage1.jsp nhận inStatisticType=supplier, bỏ supplierStatisticList, startDate, endDate, supplier, importInvoiceList, statContext, contextError rồi redirect form thống kê. | Phân biệt bước bắt đầu lượt thống kê với đường Quay lại trong thiết kế. | Không invalidate Session và không xóa auth. SupplierStatisticPage1.jsp và đường Back từ list giữ nguyên; không sửa DAO/schema/giao diện. | Đã yêu cầu/xác nhận; HTTP reset form/token/notice và giữ login/session ID PASS |
| Hồi quy không kiểm tra nội dung lỗi hoặc phân biệt hai đường vào | Mở rộng tools/http_checks.py: từng thông báo và aria-invalid, giữ đủ 6 giá trị, blank mỗi trường/nhiều lỗi/độ dài; chọn mới so với Back, link kỳ cũ, auth. | Test phải phát hiện các lỗi vừa sửa và bảo vệ quyết định giữ context khi quay lại. | Không thêm dependency/test hook; test dùng DB demo hiện có; harness cũ thêm reader demo như trước, không reset dữ liệu. | 63 HTTP + 9 reload + 32 JDBC + 6 constraint PASS; build/design PASS |

Mọi phần HTML tĩnh và tên component được giữ nguyên. Chỉ hai JSP thay đổi trong production: ReaderCardRegistrationPage1.jsp và StatisticSelectionPage1.jsp. Bản ZIP review cũ không được ghi đè; thông tin runtime/test mới ghi trong TEST_RESULTS và evidence/review2-regression.txt.

## Đồng bộ ERD cuối — categoryName NOT NULL, 16/11/2026

| Vấn đề | Phương án | Lý do | Ảnh hưởng | Trạng thái |
|---|---|---|---|---|
| tblCategory.categoryName đang nullable trong schema và DB | Đổi duy nhất cột này sang varchar(150) NOT NULL trong schema.sql; migration ALTER COLUMN SET NOT NULL cho database libman hiện tại | User xác nhận ràng buộc ERD cuối, thay thế thông tin nullable từ ERD cũ trong SPEC | Không đổi kiểu/độ dài, khóa/UNIQUE/FK khác, Java/JSP/Entity/DAO hoặc UI | Đã được user yêu cầu/xác nhận; đã commit trên PostgreSQL 18.2 |
| Kiểm tra dữ liệu trước khi siết NULL | Precheck đọc-only; sau đó khóa tblCategory và kiểm tra NULL lại trong cùng transaction trước ALTER; nếu có NULL thì dừng, không tự điền/xóa | Tránh sửa dữ liệu để hợp thức hóa ràng buộc, tránh race giữa kiểm tra và ALTER | Thực tế 0 dòng, 0 NULL; lock_timeout 5 giây; không reset/seed lại | Đã kiểm tra và thực hiện thành công |
| Xác minh phạm vi thay đổi | So sánh catalog constraints (loại trừ NOT NULL của đúng cột), cột khác/default/nullability và index; so sánh fingerprint dữ liệu tblCategory trước/sau | Chứng minh chỉ thay ràng buộc được yêu cầu | Mọi đối chiếu PASS; kết nối mới thấy is_nullable=NO; NULL INSERT bị 23502 và rollback về savepoint với id tường minh, không tăng identity sequence | Đã kiểm chứng; evidence/category-name-not-null.txt |

Migration có thể xem/chạy tại database/migrations/20261116_category_name_not_null.sql; không chạy lại schema.sql vào database hiện có. Không sửa Java/JSP, không build/deploy lại WAR vì thay đổi chỉ thuộc DDL/tài liệu. Các ZIP review đã tạo trước thay đổi này được giữ nguyên, không tự ghi đè.

## Chuẩn bị Git initial release — 16/11/2026

| Vấn đề | Phương án | Lý do | Ảnh hưởng | Trạng thái |
|---|---|---|---|---|
| Git root ban đầu ở C:/Users/Admin | Khởi tạo repo độc lập ngay C:/Users/Admin/Libman, main; không sửa/xóa Git cha | User yêu cầu repo chỉ quản lý project để nộp GitHub | Chỉ tạo .git trong project; không remote/push; đối chiếu metadata Git cha trước/sau | Đã được user yêu cầu; initial commit thông điệp Initial release of LibMan |
| Tránh đưa secret/cache lên Git | Bổ sung ignore credential/env/private keys/cache/binary; stage rõ phạm vi project, rà soát nội dung và filelist | Credential DB thực nằm ngoài Git; chỉ giữ cấu hình mẫu placeholder và tài khoản demo giả đã thống nhất | Không đổi code nghiệp vụ; giữ docs/reference và evidence cần thiết; manifest docs/COMMITTED_FILES.txt | Preflight PASS |
| Tái triển khai từ repo | Export Git index sạch; build WAR; schema/seed chạy trong schema thử rollback; deploy Tomcat base riêng 18081, smoke HTTP chỉ auth/SELECT rồi stop | Kiểm tra không phụ thuộc target/.local gốc và không reset libman | Dùng công cụ/dependency cache hiện có, credential copy chỉ vào .local bản thử; không claim cold-cache/máy khác | Build/SQL/runtime PASS; docs/GIT_RELEASE_CHECK.md và evidence/repository-preflight.txt |
