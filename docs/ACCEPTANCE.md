# Nghiệm thu và đối chiếu thiết kế → mã nguồn

Mỗi mục ghi PASS/FAIL/NOT RUN, kèm lệnh, URL, output hoặc bằng chứng. Không tự tuyên bố PASS chỉ vì đã đọc code. Dùng DB demo riêng và dữ liệu giả lập. Không chạy test phá hủy DB hiện hữu.

## A. Môi trường & cấu trúc
- [ ] Ghi `java -version`, `mvn -version`, PostgreSQL server version, Tomcat version; tệp config/port thực tế, API javax/jakarta tương thích.
- [ ] `mvn clean package` thành công, WAR deploy và HTTP response thành công. Chỉ nêu tên WAR/context/path/protocol đã kiểm tra.
- [ ] 22 Entity, 19 bảng, 5 DAO nghiệp vụ + DAO1; đúng tên, field, type, inheritance; không có bảng statistic.
- [ ] SQL constraint kiểm tra từ PostgreSQL catalog: 4 UNIQUE ghép, không UNIQUE từng FK; ReturnDetail chỉ loanDetail unique; card readerId unique. Thử nhiều detail cùng invoice và document xuất hiện ở nhiều invoice khác nhau.
- [ ] 8 JSP và HTML tương ứng SPEC; không có controller/service tự phát; quyền truy cập con protected và abstract base classes.

## B. RegisterReaderCard1
- [ ] Từ home mở form; Hủy về home, DB không tăng dòng.
- [ ] CCCD `001203012345` đăng ký lần đầu với 6 trường mẫu. Một reader và một readerCard có liên kết đúng, cardCode unique, issueDate hôm nay, ACTIVE. Result hiện đủ 9 thông tin và success; CCCD giữ số 0.
- [ ] Thử lại CCCD đó: không thêm bản ghi, báo lỗi đúng; không trả success.
- [ ] Thiếu trường, CCCD không hợp lệ, ngày sinh tương lai, email/điện thoại sai: không lưu, giữ form và báo lỗi.
- [ ] Kiểm tra transaction rollback (test riêng an toàn, lỗi mô phỏng giữa 2 INSERT hoặc integration test): không có reader mồ côi; nếu chưa thực hiện test này đánh NOT RUN và nêu vì sao.
- [ ] Truy cập result trực tiếp, refresh, 2 submit nhanh: không tạo dữ liệu trùng.

## C. ViewSupplierStatistics1
- [ ] Login đúng quản lý; sai mật khẩu bị từ chối; staff không đúng vai trò bị từ chối; mọi JSP quản lý truy cập khi chưa login bị chặn. Logout invalidate session. Không dùng thông tin đăng nhập thực tế.
- [ ] Chọn thống kê NCC; đúng 2 input ngày và nút Xem. 2 lựa chọn khác hiển thị nhưng không giả làm đã code.
- [ ] 01/01/2026–31/03/2026 cho 3 NCC đúng thứ tự 320,245,180 dưới đúng MST, không hardcode. Thử khoảng khác để chứng tỏ query thật. Đối chiếu `SUM(quantity)` từ SQL độc lập.
- [ ] Thiếu ngày, start>end, kỳ rỗng; báo lỗi/không dữ liệu, không trả kết quả cũ là mới; ngày biên inclusive, ngoài kỳ không tính.
- [ ] Click Minh Triết -> đúng header 5 trường, khoảng ngày, 3 phiếu PN000101, PN000118, PN000137 theo ngày.
- [ ] Click PN000137 -> mã và ngày phiếu, LS001–Trần Thị Bình, NCC đúng; DOC001 40×120000, DOC002 30×135000, DOC003 50×110000. Thành tiền 4.800.000, 4.050.000, 5.500.000; tổng 14.350.000 VNĐ. So sánh totalAmount với SQL sum. Không dùng trị tiền để xếp hạng NCC.
- [ ] Quay lại giữ đúng ngữ cảnh từ detail -> list -> statistics; chọn kỳ mới không mang nhầm phiếu/kết quả cũ; id giả/ngoài ngữ cảnh bị từ chối. Truy vấn thống kê chỉ đọc, không sửa dữ liệu.

## D. Bàn giao phục vụ báo cáo
- [ ] README đủ lệnh cấu hình/build/start/deploy PostgreSQL/Tomcat/IntelliJ, seed demo, tài khoản demo, giới hạn bản demo; không ghi secret thật.
- [ ] `docs/IMPLEMENTATION_DECISIONS.md`: quyết định kỹ thuật và đơn giản hóa, lý do, ảnh hưởng, trạng thái xác nhận. `docs/TRACEABILITY.md`: tên sơ đồ/kịch bản, file, class/method, mức test; ghi rõ những gì chưa thực hiện.
- [ ] `docs/DEPLOYMENT_ACTUAL.md`: thiết bị chạy thực tế, browser, Tomcat, JVM, PostgreSQL, file triển khai, địa chỉ, port, HTTP hay HTTPS đã kiểm chứng, không đoán. Nếu trên một máy ghi một máy, không bịa 3 máy.
- [ ] Chụp 8 JSP thực tế và UI login hỗ trợ, cấu trúc file trong IntelliJ, bằng chứng dữ liệu/test (ẩn bí mật); mã nguồn sạch sẵn sàng đưa GitHub. Đây là việc cần làm sau khi ứng dụng thực sự chạy.
