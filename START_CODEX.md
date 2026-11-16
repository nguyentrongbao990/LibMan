# Prompt khởi động — dán vào Codex khi đã mở thư mục LibMan

Đọc AGENTS.md, docs/SPEC.md, docs/ACCEPTANCE.md và database/schema.sql. Đối chiếu docs/reference; PDF là bản cũ trước review, SPEC là bản tổng hợp quyết định mới nhất. Tôi dùng Windows, IntelliJ IDEA Ultimate trial, đã cài JDK, Maven, PostgreSQL, Git; không chắc Tomcat. Project chưa có code. Deadline 23:59 hôm nay, ưu tiên chạy thật hai module đúng thiết kế.

Hãy thực hiện theo từng mốc:
1. Kiểm tra môi trường hiện có, phát hiện JDK/Tomcat/API tương thích và PostgreSQL. Nêu blocker thật, không tự cài/đổi cấu hình toàn máy. Hỏi tôi khi cần đường dẫn, mật khẩu DB hoặc có xung đột thiết kế; không đưa secret vào git. Với việc không phụ thuộc blocker, cứ triển khai.
2. Khởi tạo Maven WAR, model 22 lớp, DAO theo đúng chữ ký, SQL schema 19 bảng và seed demo. Giữ tên chính xác, dùng schema tham chiếu và test các constraint. Build sớm. Tạo các file quyết định/traceability ngay từ đầu. Đề xuất phương án auth đơn giản (đặc biệt mật khẩu) cho tôi xác nhận, không tự thêm công nghệ.
3. Hiện thực module RegisterReaderCard1 theo 18 bước, đúng 3 JSP, JDBC transaction, trường hợp ngoại lệ, test build và DB. Không thêm service/controller, không đổi tên, không fake dữ liệu.
4. Hiện thực auth quản lý tối thiểu đã được xác nhận và ViewSupplierStatistics1 theo 32 bước, đủ 5 JSP, seed đạt 320/245/180, phiếu PN000137 tổng 14.350.000, back giữ ngữ cảnh. Test theo ACCEPTANCE.md.
5. Chốt README, traceability, quyết định triển khai, ghi thông tin deployment thực tế. Báo file đã tạo, test thực hiện/kết quả, việc còn thiếu, hướng dẫn tôi chạy IntelliJ. Không tự push hoặc submit.

Nguyên tắc: cái đã thiết kế thì code đúng chính xác; cái chưa thiết kế thì đơn giản có kiểm soát, dễ vấn đáp, mọi giản lược phải báo và ghi rõ. Không chỉnh sơ đồ hay đặc tả để hợp thức hóa code khác thiết kế. Không truy vấn CSDL bằng dữ liệu hardcode. Hãy bắt đầu ngay từ bước 1 rồi tiếp tục các phần độc lập với môi trường; chỉ dừng tại blocker thực sự hoặc quyết định làm thay đổi thiết kế. Nếu có yêu cầu bổ sung phức tạp, ghi đề xuất và hỏi tôi trước.
