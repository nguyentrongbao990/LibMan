# Kết quả nghiệm thu thực chạy — 16/11/2026 (UTC+7)

## Preflight repository độc lập — 22:25 trở đi, 16/11/2026

Đã kiểm tra tái triển khai từ source xuất bằng Git index sạch: Maven clean package PASS (29 Java source); schema/seed trong schema PostgreSQL thử riêng PASS (19 bảng, categoryName NOT NULL, 320/245/180, PN000137 14350000.00, reader trống), sau đó rollback toàn bộ. WAR build từ bản xuất đã deploy Tomcat base riêng trên 127.0.0.1:18081, smoke HTTP login/auth guard/statistics/list/detail PASS rồi dừng Tomcat thử. Không ghi dữ liệu nghiệp vụ, không reset DB hiện hữu. Không thay code Java/JSP/DDL/pom/tools. Dùng JDK/Maven/Tomcat và Maven cache trên máy; không khẳng định đã test máy khác/cold cache. Output tại evidence/repository-preflight.txt; phạm vi/manifest/ignore xem GIT_RELEASE_CHECK.md và COMMITTED_FILES.txt.

## Đồng bộ categoryName NOT NULL theo ERD cuối

Bằng chứng: `evidence/category-name-not-null.txt`. Thực chạy trên PostgreSQL 18.2, database libman hiện có, qua JDBC bằng JShell và credential cục bộ ngoài Git. Không sửa Java/JSP, không reset/seed lại. Không build hay chạy lại toàn bộ hồi quy ứng dụng trong bước DDL này; các kết quả hồi quy phía dưới là lịch sử các bản trước.

| Kiểm tra/thao tác thực chạy | Kết quả |
|---|---|
| SELECT đọc-only count(*) và count(*) FILTER (WHERE categoryName IS NULL) | PASS: tblCategory có 0 dòng, 0 NULL; trước ALTER is_nullable=YES, character varying(150). |
| Khóa tblCategory trong transaction với lock_timeout=5s rồi kiểm tra NULL lại | PASS: 0 NULL; không xử lý/xóa/điền dữ liệu. |
| Áp dụng database/migrations/20261116_category_name_not_null.sql | PASS: ALTER TABLE public.tblCategory ALTER COLUMN categoryName SET NOT NULL; đã commit. |
| Thử INSERT id=-1, categoryName=NULL sau ALTER, savepoint rollback | PASS: SQLState 23502 (not_null_violation); không tạo dòng và không dùng sequence sinh id. |
| So sánh toàn bộ catalog constraints của schema public, trừ NOT NULL đúng cột được sửa | PASS: mọi ràng buộc khác giữ nguyên. |
| So sánh định nghĩa cột khác (type/nullability/default/identity/generated/collation) và tất cả index public | PASS: không thay đổi. |
| So sánh số dòng/fingerprint tblCategory trước/sau | PASS: dữ liệu không đổi. |
| Kết nối JDBC mới sau commit | PASS: is_nullable=NO, type=character varying, length=150; metadata khác và dữ liệu vẫn khớp snapshot trước. |

Lệnh kiểm chứng: `jshell --class-path target/LibMan/WEB-INF/lib/postgresql-42.7.10.jar .local/category-precheck.jsh` rồi `.local/category-migrate.jsh`. Script thao tác cục bộ ở .local bị ignore; migration SQL được lưu trong database để review/chạy lại có kiểm soát trên môi trường khác. Chỉ kiểm tra NOT NULL, không thêm CHECK cho chuỗi rỗng hay thay đổi ràng buộc khác.

Nghiệm thu ban đầu: WAR build thành công lúc 17:45 trên Tomcat 11.0.22/JDK 24/PostgreSQL 18.2, database libman riêng. Bản sửa sau review lúc 19:52 và lần hai lúc 20:05 được build/deploy và kiểm thử lại, xem các mục cập nhật dưới đây. Chỉ dữ liệu demo. Không chạy DROP/TRUNCATE/reset; database khác không sửa.

## Hồi quy review lần hai — 20:05, 16/11/2026

Nguồn bằng chứng mới: `evidence/review2-regression.txt`. Các file bằng chứng cũ giữ nguyên lịch sử. Hai JSP được sửa: form đăng ký báo lỗi cụ thể và chọn loại thống kê xóa ngữ cảnh chỉ khi bắt đầu lượt NCC mới. Không thay Entity, DAO, schema, component HTML hoặc kiến trúc.

| Lệnh thực chạy | Kết quả |
|---|---|
| Maven 3.9.15 `mvn.cmd -B clean package` | PASS; BUILD SUCCESS 20:05:13; 29 Java production source. Maven không có JUnit, chỉ compile/package. |
| `python tools/check_design.py` | PASS; 22 entity/chữ ký DAO/19 bảng/10 JSP đúng thiết kế. |
| javac UTF-8, classpath target/classes;target/LibMan/WEB-INF/lib/* → target/checks, cho 3 Java harness hiện có | PASS. |
| `stop-demo.ps1`, `start-demo.ps1` | PASS, WAR mới deploy localhost:18080/LibMan. Checksum build/deploy khớp, log Tomcat 0 SEVERE/Exception. |
| `python tools/http_checks.py` với PYTHONIOENCODING=utf-8 | PASS 63 assertion; gồm các ca mới bên dưới và hồi quy auth/register/statistics/back. |
| `java -cp 'target/checks;target/classes;target/LibMan/WEB-INF/lib/*' InvoiceListReloadChecks1` | PASS 9 assertion; query mới khi chọn lại NCC và Back từ chi tiết vẫn hoạt động. |
| Java cùng classpath, IntegrationChecks1 | PASS 32 assertion JDBC, SQL SUM độc lập, 320/245/180, tổng PN000137 14.350.000, rollback, duplicate và auth. |
| Java cùng classpath, ConstraintChecks1 | PASS 6 dòng PASS; fixture rollback. |

Các ca mới thực chạy:

- Thiếu tên, CCCD sai, ngày sinh tương lai/ngày 30/02, email sai và phone sai: đúng thông báo cụ thể, trường lỗi aria-invalid=true; cả 6 giá trị POST giữ lại trong HTML (đã trim và escape).
- Blank/whitespace ở từng trường còn lại chỉ báo thiếu trường, không báo sai định dạng; bỏ trống tất cả liệt kê đủ 6 tên và đánh dấu cả 6.
- CCCD/phone/email cùng sai: đủ cả ba thông báo trong đúng một outErrorMessage; không thêm component. Tên dài 151 ký tự báo giới hạn 150, giữ giá trị nhập.
- Back detail → list → statistics giữ supplier, cả hai ngày và bảng đủ 320/245/180.
- Chỉ mở trang chọn loại chưa xóa kết quả. Chọn NCC + Xem tạo lượt mới với 2 ngày trống, không có link NCC/kết quả/lỗi ngữ cảnh cũ.
- Sau khi bắt đầu lượt mới, link supplier và invoice của lượt cũ đều bị từ chối. Session ID không đổi, trang quản lý vẫn truy cập được; truy vấn lượt mới thành công mà không cần login lại. Chọn mới lần nữa xóa được notice do link cũ sinh ra.

Không reset/xóa dữ liệu. HTTP và JDBC harness hiện có vẫn thêm reader hoàn toàn demo để test commit/race; harness reload không ghi dòng dữ liệu, constraint fixtures rollback. Không chụp lại ảnh/Run Debug IDE trong lần này; giao diện/component giữ nguyên, kiểm chứng hành vi qua HTTP trên JSP đã biên dịch thực tế.

## Hồi quy sau sửa tải lại danh sách phiếu — 19:52, 16/11/2026

Đã bỏ nhánh cache cùng NCC trong SupplierImportInvoiceListPage1.jsp. Bản test ban đầu chạy trên WAR cũ thất bại ở điều kiện SELECT mới khi chọn lại cùng NCC; điều này cho thấy bộ test cũ chưa bao phủ sai lệch thiết kế động. Harness cuối dùng pg_locks thay vì snapshot pg_stat_activity để quan sát lock trực tiếp.

Lệnh thực chạy và output được lưu chung tại `evidence/invoice-list-reload-regression.txt`:

| Kiểm tra | Lệnh | Kết quả |
|---|---|---|
| Build | Maven 3.9.15 `mvn.cmd -B clean package` | PASS, BUILD SUCCESS lúc 19:52:31, 29 Java production source; Maven không có JUnit |
| Thiết kế | `python tools/check_design.py` | PASS, không đổi 22 entity, chữ ký DAO, 19 bảng, 10 JSP |
| Biên dịch harness | javac UTF-8 với target/classes;target/LibMan/WEB-INF/lib/*, output target/checks | PASS; gồm InvoiceListReloadChecks1, IntegrationChecks1, ConstraintChecks1 |
| Deploy | stop-demo.ps1 rồi start-demo.ps1 | PASS, localhost 18080; checksum WAR deploy = WAR build; log 0 SEVERE/Exception |
| Tải lại danh sách | `java -cp 'target/checks;target/classes;target/LibMan/WEB-INF/lib/*' InvoiceListReloadChecks1` | PASS 9 assertion: query mới cho cùng NCC và back từ chi tiết, list/ngày giữ đúng, detail đọc mảng mới, kết quả thống kê giữ nguyên |
| JDBC nghiệp vụ | Java cùng classpath, IntegrationChecks1 | PASS 32 assertion, gồm SQL SUM độc lập, tổng tiền, auth, Hủy, rollback và duplicate |
| Constraints | Java cùng classpath, ConstraintChecks1 | PASS 6 dòng PASS; fixture rollback |
| HTTP | `python tools/http_checks.py` (PYTHONIOENCODING=utf-8) | PASS 35 assertion, gồm auth/invalid id/kỳ cũ/back/ngày/đăng ký song song |

Harness mới chỉ giữ ACCESS EXCLUSIVE lock trên bảng phiếu nhập trong transaction demo, tối đa chờ quan sát 5 giây; SELECT thật phải xin AccessShareLock và xuất hiện trong pg_locks, còn mảng cache không tạo yêu cầu lock. Rollback trong finally luôn giải phóng lock. Không thêm/sửa/xóa dòng dữ liệu trong harness này. Các harness cũ vẫn thêm bạn đọc giả phục vụ kiểm thử như đã ghi; không reset DB.

Giao diện HTML của trang được đối chiếu với bản trước và không đổi. Không thực hiện lại chụp UI/IDE vì không sửa giao diện; ảnh cũ là bằng chứng giao diện ban đầu. Các output/bảng bên dưới giữ lịch sử nghiệm thu 17:45; bằng chứng runtime mới nằm trong file hồi quy nói trên.

## Lệnh và bằng chứng

| Kiểm tra | Lệnh thực chạy (tại project root) | Kết quả | Bằng chứng |
|---|---|---|---|
| Build cuối | Maven 3.9.15 mvn.cmd -B clean package | PASS, 29 source Java, BUILD SUCCESS, WAR | evidence/build.txt |
| Đối chiếu thiết kế | python tools/check_design.py | PASS, 22 model exact fields/inheritance, 5 chữ ký DAO, 19 DDL tables, 10 JSP | evidence/design-check.txt |
| DB setup | javac ... DatabaseSetup1.java; java -cp target/checks;target/classes;target/LibMan/WEB-INF/lib/* DatabaseSetup1 | PASS, Created database libman, applied schema.sql/seed.sql, SELECT version() 18.2 | Output tool trong phiên; database hiện có; script không được chạy lại vào DB có dữ liệu |
| JDBC thật | java -cp 'target/checks;target/classes;target/LibMan/WEB-INF/lib/*' IntegrationChecks1 | PASS, 32 assertion (gồm SQL SUM(quantity) độc lập) | evidence/jdbc-checks.txt |
| Constraint INSERT thật | java cùng classpath ConstraintChecks1 | PASS, 6 dòng PASS và rollback toàn bộ fixture | evidence/constraint-checks.txt |
| HTTP thật | python tools/http_checks.py | PASS, 35 assertion | evidence/http-checks.txt |
| Browser | Đăng ký mẫu, login, chọn kỳ, NCC, PN000137, back | PASS | evidence/01–09 PNG; chi tiết phiếu gồm ảnh top/bottom |
| IntelliJ project | Quan sát cấu trúc trong IDEA Ultimate 2025.2.4 | PASS | evidence/10-intellij-project.png |
| Tomcat Run/Debug từ IntelliJ | Chưa chạy | NOT RUN | README có hướng dẫn, runtime nghiệm thu chạy qua start-demo.ps1 |

Không có JUnit/surefire tests: Maven log ghi “No tests to run”. PASS build chỉ là biên dịch/đóng gói; các assertion nghiệp vụ nằm ở các harness thực thi riêng nêu trên. Lệnh javac đầy đủ nằm trong README.

## Đối chiếu ACCEPTANCE

| Mục | Trạng thái | Kết quả quan sát |
|---|---|---|
| A môi trường | PASS | java 24.0.2, Maven 3.9.15, PG 18.2, Tomcat 11.0.22; đường dẫn/API/port ghi DEPLOYMENT_ACTUAL |
| A build/deploy | PASS | LibMan.war, /LibMan, HTTP 127.0.0.1:18080; checksum build/deploy khớp |
| A 22 entity/19 bảng/DAO/JSP | PASS | check_design đối chiếu từng declaration với SPEC; catalog đếm 19 bảng; không bảng statistic/service/controller |
| A constraints | PASS | 4 UNIQUE ghép đúng catalog; INSERT trùng cặp bị 23505; lặp từng FK riêng được chấp nhận; ReturnDetail receipt chứa nhiều dòng; readerId unique |
| B home/form/Hủy | PASS | HTTP/browser mở form; navigation Hủy không tăng count tblReader |
| B đăng ký mẫu lần đầu | PASS | Browser CCCD 001203012345 giữ số 0, Nguyễn Văn An và đủ 6 trường; reader+card thành công, RD000006, 16/11/2026, ACTIVE; ảnh 03 |
| B duplicate | PASS | JDBC và HTTP từ chối, không thêm reader/card thứ hai |
| B validation | PASS | Thiếu tên, CCCD sai, birth tương lai/ngày 30/02, email/phone sai thử độc lập; form giữ/escape dữ liệu, đánh dấu aria-invalid |
| B rollback | PASS | Lỗi SQLException được gây trước INSERT thẻ; truy vấn lại không có reader mồ côi, object không id/card success |
| B result/refresh/race | PASS | Direct result redirect; submit lại báo trùng; 2 HTTP POST đồng thời cho đúng 1 success và 1 duplicate |
| C auth | PASS | Quản lý đúng mật khẩu được vào; sai mật khẩu và library staff bị chặn; đủ 5 JSP redirect login; session đổi sau login; POST logout invalidate, GET 405 |
| C loại thống kê | PASS | 3 option, 2 ngoài phạm vi disabled/chưa hiện thực; form đúng 2 ngày |
| C 320/245/180 | PASS | SUM(quantity) thật, đúng MST và thứ tự; kỳ khác 01/04 cho 9; SQL tiền độc lập; không hardcode kết quả trong DAO/JSP |
| C ngày/empty | PASS | Thiếu/ngày sai/start>end không giữ kết quả cũ; kỳ 2024 rỗng; ngày 01/01 cho 100 và 31/03 cho 145, ngoài kỳ không làm tăng tổng quý |
| C Minh Triết | PASS | Header 5 thông tin, khoảng 01/01–31/03/2026, đúng 3 phiếu theo ngày |
| C PN000137 | PASS | LS001–Trần Thị Bình; DOC001 40×120000=4800000; DOC002 30×135000=4050000; DOC003 50×110000=5500000; BigDecimal và stored total 14350000 khớp SQL |
| C back/context/read-only | PASS | Back đúng supplier/ngày/results; id giả và link context cũ sau đổi kỳ bị từ chối; DAO thống kê chỉ SELECT |
| D README/decisions/traceability/deployment | PASS | File đã hoàn thiện; trạng thái xác nhận/hạn chế nêu rõ |
| D ảnh UI/IntelliJ/bằng chứng | PASS | 8 JSP nghiệp vụ, Login, IntelliJ project structure và output kiểm thử ở evidence |

## Sự cố đã phát hiện và sửa trong phiên

- Cổng 8080 không mở socket được; dùng base Tomcat riêng bind 127.0.0.1:18080, đã kiểm chứng HTTP.
- JSP ban đầu hiển thị mojibake vì pageEncoding chỉ nằm trong include; thêm UTF-8 trên từng JSP và cấu hình JSP group. Browser và HTTP cuối hiển thị tiếng Việt đúng.
- Tool đối chiếu Python ban đầu đọc DAO bằng default encoding Windows; đã sửa đọc UTF-8, chạy lại PASS. Không phải thay đổi Entity/schema.
- Chụp fullPage một số lần không thành công; chi tiết phiếu được lưu thành hai ảnh viewport top/bottom thực tế. Không ghép/fake màn hình.

## Giới hạn còn lại

- Chưa thực Run/Debug Tomcat từ IntelliJ; đã xác nhận project mở được và cấu trúc hiện đúng. Hướng dẫn thiết lập được cung cấp.
- Mật khẩu rõ chỉ tài khoản giả lập localhost theo lựa chọn của user. Một context thống kê/session; nhiều tab có thể mất ngữ cảnh. Refresh POST đăng ký báo trùng thay vì PRG.
- Test reader được giữ lại, không xóa. Ví dụ CCCD chuẩn đã dùng nên demo lần sau phải chọn số demo khác. Mã thẻ không liên tục do identity/rollback, được SPEC cho phép.
- Input date hiển thị theo locale browser; giá trị gửi là ISO, ngày kết quả/nhãn server hiển thị dd/MM/yyyy.
