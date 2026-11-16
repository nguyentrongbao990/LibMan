# LibMan — chỉ dẫn bắt buộc cho Codex

## Mục tiêu và phạm vi
Subject No. 06, Java Web LibMan. Thiết kế toàn hệ thống, hiện thực đúng hai module RegisterReaderCard1 và ViewSupplierStatistics1 cùng đăng nhập/đăng xuất nhân viên quản lý tối thiểu. Deadline 23:59 ngày 16/11/2026 (UTC+7). Không triển khai các module CRUD, mượn/trả, nhập, thống kê tài liệu/bạn đọc ngoài phạm vi. Không giả lập nghiệp vụ bằng dữ liệu hardcode.

## Nguồn chuẩn (thứ tự ưu tiên)
1. `docs/SPEC.md`, `docs/ACCEPTANCE.md`, `database/schema.sql`: tổng hợp các quyết định chốt mới nhất tại cuộc trò chuyện, ưu tiên hơn ảnh cũ. Schema là DDL theo thiết kế, chưa chạy thử, cần xác minh trên PostgreSQL.
2. `docs/reference/01_DangKyThe.html`, `02_ThongKeNCC.html` và PNG tương ứng: wireframe đã sửa; chỉ dùng làm mẫu từng trang, không dùng nguyên ảnh ghép làm web.
3. `docs/reference/report_before_revisions.pdf`: báo cáo 42 trang cũ, dùng tham khảo nghiệp vụ và sơ đồ trước chỉnh sửa. Không khôi phục các thuộc tính JSP, ký hiệu dẫn xuất hoặc UNIQUE đã sửa. Nếu người dùng bổ sung ảnh sơ đồ cuối, đối chiếu và báo khác biệt, không tự chọn khi có xung đột.

## Bất biến
- Java/JSP + HTML/CSS/JS cơ bản + JDBC + PostgreSQL + Maven WAR + Tomcat. Không tự thêm Spring, ORM, React, Docker, tầng service/controller riêng. Dùng IntelliJ IDEA Ultimate. Kiểm tra Java, Maven, Tomcat và PostgreSQL thực tế trên Windows trước khi chọn phiên bản API servlet tương thích. Không tự cài hoặc đổi cấu hình toàn hệ thống khi chưa được phép.
- Tên tất cả các lớp Java và JSP được thiết kế có hậu tố `1`; thuộc tính/phương thức không thêm `1`. Giữ tên, type, đối số, return type, inheritance, các tham chiếu như SPEC. Array `[]` trong chữ ký và field; có thể dùng ArrayList cục bộ rồi chuyển sang mảng. Không tạo lại DAO CRUD tại Entity. Getter/setter, constructor, close/helper cục bộ cần thiết được phép và phải ghi nhận.
- Tầng JSP gọi DAO trực tiếp theo thiết kế. Không hiện thực self-message UML thành lời gọi đệ quy hoặc tạo đối tượng hai lần. Dùng `model`, `dao`, `view` đúng tổ chức; view là thư mục JSP, không phải Java package chứa lớp JSP viết tay.
- Không sửa cấu trúc bảng đã chốt; dùng đúng UNIQUE ghép. DDL dùng định danh có double quotes để bảo toàn chữ hoa/thường; SQL trong DAO cũng phải quote nhất quán. Không DROP/TRUNCATE dữ liệu người dùng hoặc reset DB khi không có đồng ý.
- Thiết kế 8 JSP phải khớp các thành phần nhìn thấy và đối tượng dữ liệu. Dữ liệu đối tượng server-side không biến thành ô ẩn HTML; UI kiểu `text` trong UML có thể hiện thực bằng input date/tel/email. Không tạo thêm tính năng tùy tiện. Tách ba nhóm: component HTML, dữ liệu server-side, biến cục bộ.
- Không thêm chức năng bảo mật/phần mềm phức tạp để 'chuyên nghiệp'. Đăng nhập tối thiểu demo; không dùng thông tin thật. Không commit mật khẩu DB, secret, file cấu hình riêng, CCCD người thật, log nhạy cảm. Chỉ localhost cho bản demo. Xác thực session phía server trên tất cả JSP quản lý; PreparedStatement, bảo vệ HTML khỏi XSS và kiểm tra đầu vào server-side. Mật khẩu demo: xem quyết định đề xuất trong SPEC; không tự thay đổi phương án.
- Không hardcode URL, port hoặc HTTP/HTTPS khi chưa kiểm chứng; dùng context path; ghi nhận URL/port/server thực tế để làm Deployment Diagram. Không tuyên bố đã chạy test khi chưa chạy.

## Quy trình
1. Kiểm tra môi trường, đọc SPEC, liệt kê các blocker và quyết định cần xác nhận. Nếu có xung đột ảnh hưởng thiết kế/DB/API, hỏi user trước khi đổi. Với phần không ảnh hưởng, tiếp tục công việc có thể thực hiện an toàn.
2. Khởi tạo Maven WAR, schema và seed demo, model. Build sớm. Hiện thực module 1, test; rồi auth tối thiểu và module 2, test.
3. Tự đối chiếu test và ma trận thiết kế → file → phương thức → kết quả. Ghi mọi quyết định giản lược/chi tiết triển khai phát sinh vào `docs/IMPLEMENTATION_DECISIONS.md`: vấn đề, phương án, lý do, ảnh hưởng, trạng thái (đề xuất/đã xác nhận). Không âm thầm giản lược chức năng. Không sửa thiết kế thay user.
4. README hướng dẫn thiết lập Windows/IntelliJ và chạy; ghi lại build, test thực chạy/thất bại/chưa chạy; ghi cấu hình triển khai thực tế. Không push GitHub hoặc thao tác nộp bài nếu chưa được yêu cầu. Chỉ dùng dữ liệu demo.
