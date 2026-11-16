# LibMan — Subject No. 06

Đã hiện thực RegisterReaderCard1, ViewSupplierStatistics1 và đăng nhập/đăng xuất quản lý tối thiểu. Java/JSP, HTML/CSS, JDBC, PostgreSQL, Maven WAR, Tomcat; JSP gọi DAO trực tiếp. Không triển khai CRUD, mượn/trả, nhập hay hai loại thống kê ngoài phạm vi.

## Chạy ngay trên máy hiện tại

Demo đã triển khai tại **http://127.0.0.1:18080/LibMan/**. Chỉ bind 127.0.0.1.

- Quản lý demo: `manager_demo` / `LibManDemo1!`.
- `library_demo` / `LibraryDemo1!` là nhân viên thư viện dùng kiểm thử từ chối sai vai trò; không có module đăng nhập thư viện.
- Tài khoản trên hoàn toàn giả lập. User đã chọn mật khẩu rõ, chỉ dùng demo localhost. Mật khẩu PostgreSQL thật chỉ nằm trong `.local/db.properties`, bị loại khỏi Git.
- PostgreSQL database **libman** đã được tạo riêng và đã chạy schema/seed; **không chạy seed lần nữa trên database này**.
- CCCD mẫu `001203012345` đã đăng ký qua trình duyệt để nghiệm thu; thử lại sẽ báo trùng. Muốn demo đăng ký mới, dùng một CCCD demo 12 chữ số khác. Không xóa/reset dữ liệu để chạy lại ví dụ.

Luồng bạn đọc: Trang chủ → Đăng ký thẻ → nhập đủ 6 trường → Lưu đăng ký. Luồng quản lý: Đăng nhập → Xem thống kê → chọn NCC → ngày 01/01/2026–31/03/2026 → Xem → Minh Triết → PN000137. Kết quả 320/245/180 và tổng 14.350.000 được truy vấn/tính từ dòng dữ liệu thật.

## Môi trường đã kiểm tra

| Công cụ | Phiên bản / đường dẫn |
|---|---|
| JDK | Oracle 24.0.2, `C:\Program Files\Java\jdk-24` |
| Maven | 3.9.15, `C:\Users\Admin\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd` |
| Tomcat | Apache 11.0.22, `C:\Users\Admin\apache-tomcat-11.0.22` |
| PostgreSQL | Server/client 18.2, `C:\Program Files\PostgreSQL\18\bin`, service postgresql-x64-18 |
| IntelliJ | IDEA Ultimate 2025.2.4; project đã mở và kiểm tra cấu trúc |

Tomcat 11 dùng Jakarta Servlet 6.1/JSP; Maven biên dịch bytecode release 17 trên JDK 24. Không cần thêm servlet-api vào WAR vì chỉ dùng JSP; API do Tomcat cung cấp. `mvn` và `psql` chưa có trong PATH phiên làm việc đã kiểm tra, nên dùng đường dẫn đầy đủ; không đổi PATH toàn máy. Cổng Tomcat cài sẵn 8080 không mở socket được trong lần thử, nên demo riêng sử dụng 18080 đã xác minh HTTP.

## Build, start, stop bằng PowerShell

Chạy trong `C:\Users\Admin\Libman`:

```powershell
$libmanMvn = 'C:\Users\Admin\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd'
& $libmanMvn clean package
& .\tools\start-demo.ps1
# Khi cần dừng đúng Tomcat riêng của dự án:
& .\tools\stop-demo.ps1
```

Nếu demo đang chạy, stop trước khi build/deploy lại. `start-demo.ps1` dùng Tomcat đã cài nhưng tạo CATALINA_BASE riêng tại `.local\tomcat`, copy WAR vào đó, không sửa cấu hình Tomcat gốc. Có thể truyền `-TomcatHome 'đường dẫn Tomcat 11' -Port 18080`. Script kiểm tra cổng trước khi start. PID nằm trong `.local\tomcat\process.id`; stop kiểm tra tên process, base path và Bootstrap trước khi dừng. Log ở `.local\tomcat\logs`; không đưa thư mục này lên Git.

## Mở, chạy, debug trong IntelliJ IDEA Ultimate

1. File → Open → chọn `C:\Users\Admin\Libman\pom.xml`, mở như Maven project. Nếu đã mở thư mục, nhấp phải `pom.xml` → Add as Maven Project nếu chưa được nhận diện. Chọn Project SDK JDK 24; language level theo Maven release 17.
2. Settings → Build, Execution, Deployment → Build Tools → Maven: chọn Maven đã tìm thấy ở bảng trên hoặc Bundled Maven. Maven runner JRE chọn Project JDK; Reload Maven Projects.
3. Build WAR bằng Maven `clean package` (Maven tool window hoặc lệnh trên). Artifact `target\LibMan.war`.
4. Run → Edit Configurations → `+` → Tomcat Server → Local. Application server trỏ `C:\Users\Admin\apache-tomcat-11.0.22`, JRE JDK 24. HTTP port 18080. Dừng Tomcat demo bằng stop script trước để tránh trùng cổng.
5. Deployment → `+` → Artifact → `LibMan:war exploded` (hoặc External Source chọn `target\LibMan.war` nếu artifact IDE chưa tạo); Application context `/LibMan`.
6. VM options: `-Dlibman.db.config=C:\Users\Admin\Libman\.local\db.properties -Duser.timezone=Asia/Ho_Chi_Minh`. Không đưa mật khẩu thật vào cấu hình chia sẻ. URL mở `http://127.0.0.1:18080/LibMan/`.
7. Để giữ localhost, dùng cấu hình CATALINA_BASE riêng đã sinh ở `.local\tomcat` (Startup/Connection → CATALINA_BASE nếu IDE hỗ trợ), hoặc kiểm tra `conf\server.xml` của base IDE tạo: Connector HTTP phải có `address="127.0.0.1"`. Không sửa Tomcat gốc. Không chạy đồng thời script và IDE cùng base/port.
8. Debug cấu hình Tomcat, đặt breakpoint trong `ReaderCardDAO1.registerReaderCard`, `SupplierStatisticDAO1.getSupplierStatistics`, hoặc scriptlet JSP. Source `model` và `dao` là Java packages; `view` là thư mục JSP dưới webapp.

Đã kiểm tra project mở trong IntelliJ và chụp cấu trúc. **Chưa thực hiện Run/Debug bằng cấu hình Tomcat của IntelliJ**; bản chạy nghiệm thu dùng Bootstrap từ script. Các bước IDE trên là hướng dẫn thiết lập, không phải tuyên bố đã debug thành công trong IDE.

## PostgreSQL và cấu hình cục bộ

Máy hiện tại đã cấu hình `.local\db.properties`. Khi clone sang thư mục/máy mới, tạo `.local`, copy `config\db.properties.example` vào `.local\db.properties`, điền thông tin thật tại máy đó. Các khóa: `url`, `user`, `password`; URL yêu cầu database demo riêng `libman`. Java Properties cần escape `\` thành `\\` nếu giá trị chứa backslash. Thay thế khác: biến môi trường `LIBMAN_DB_URL`, `LIBMAN_DB_USER`, `LIBMAN_DB_PASSWORD` (ưu tiên hơn file); hoặc `LIBMAN_DB_CONFIG` trỏ file. Không commit credential. Trên máy khác, dùng Maven đã cài/Bundled Maven và truyền `-TomcatHome` đúng đường dẫn Tomcat 11 thay cho các đường dẫn máy hiện tại trong ví dụ.

Thiết lập **lần đầu trên database mới** bằng công cụ có kiểm tra:

```powershell
& $libmanMvn package
javac -encoding UTF-8 -cp 'target/classes;target/LibMan/WEB-INF/lib/*' -d target/checks tools/DatabaseSetup1.java
java -cp 'target/checks;target/classes;target/LibMan/WEB-INF/lib/*' DatabaseSetup1
```

Tool kết nối database `postgres` chỉ để kiểm tra/tạo `libman`; không sửa database khác. Nếu libman có bất kỳ table/view/sequence người dùng, tool dừng trước DDL/seed. Quyền DB user phải cho phép CREATE DATABASE; nếu không, tự tạo database libman trong pgAdmin rồi chạy khi nó còn trống. Schema/seed đều có transaction riêng. Nếu schema thành công nhưng seed thất bại, kiểm tra nguyên nhân và chỉ chạy seed sau khi xác nhận dữ liệu trống; không chạy lại schema hay DROP/RESET.

Cách thủ công trên DB trống đã tạo: dùng pgAdmin Query Tool database libman, lần lượt chạy `database/schema.sql`, `database/seed.sql`; hoặc psql `-X -v ON_ERROR_STOP=1 -h localhost -p 5432 -U <user> -d libman -f database/schema.sql` rồi seed. Nhập mật khẩu qua prompt cục bộ. Seed **một lần**, không idempotent và không xóa gì. Không seed bạn đọc mẫu.

Đồng bộ ERD cuối 16/11/2026: `tblCategory.categoryName` là `varchar(150) NOT NULL`. Database libman trên máy hiện tại đã được cập nhật sau khi kiểm tra 0 dòng/0 NULL. Khi cập nhật **database đã có trên máy khác**, kiểm tra dữ liệu NULL trước, rồi chạy `database/migrations/20261116_category_name_not_null.sql` trong pgAdmin Query Tool đúng database libman hoặc psql `-X -v ON_ERROR_STOP=1 -h localhost -p 5432 -U <user> -d libman -f database/migrations/20261116_category_name_not_null.sql`. Migration khóa bảng, kiểm tra NULL lại và chỉ ALTER cột này; nếu có NULL hoặc lỗi thì không commit, không tự sửa/xóa dữ liệu. Trong pgAdmin nếu transaction bị lỗi, thực hiện ROLLBACK rồi xử lý theo quyết định của chủ dữ liệu; không chạy lại schema/seed và không reset. DB mới tạo bằng schema.sql đã có NOT NULL, không cần migration. Không đổi ràng buộc nullable của edition/email/publisher.

## Kiểm thử

```powershell
python tools/check_design.py
javac -encoding UTF-8 -cp 'target/classes;target/LibMan/WEB-INF/lib/*' -d target/checks tools/IntegrationChecks1.java tools/ConstraintChecks1.java tools/InvoiceListReloadChecks1.java
java -cp 'target/checks;target/classes;target/LibMan/WEB-INF/lib/*' IntegrationChecks1
java -cp 'target/checks;target/classes;target/LibMan/WEB-INF/lib/*' ConstraintChecks1
java -cp 'target/checks;target/classes;target/LibMan/WEB-INF/lib/*' InvoiceListReloadChecks1
$env:PYTHONIOENCODING='utf-8'
python tools/http_checks.py
```

Tomcat phải đang chạy cho HTTP checks và kiểm tra Hủy trong IntegrationChecks1. Chỉ chạy trên database demo libman. Test registration/HTTP thêm bạn đọc giả, không xóa dữ liệu; chọn CCCD demo ngẫu nhiên mỗi lần. ConstraintChecks1 rollback toàn bộ fixture. Rollback test dùng proxy Connection trong subclass test để gây SQLException ngay trước INSERT thẻ, không thêm hook vào production. InvoiceListReloadChecks1 đăng nhập qua HTTP, nạp danh sách rồi giữ lock đọc bảng phiếu nhập tạm thời (chờ tối đa 5 giây); quan sát pg_locks/pg_blocking_pids để chứng minh có SELECT mới khi chọn lại cùng NCC và Quay lại từ chi tiết. Lock được rollback trong finally, không thêm/sửa/xóa dữ liệu; cần quyền LOCK bảng trên DB demo. Bộ Maven mặc định không có JUnit: `mvn package` kiểm chứng compile/packaging, **không thay thế** các integration checks trên.

Kết quả, lệnh thực chạy, giới hạn và bằng chứng: `docs/TEST_RESULTS.md`, `docs/TRACEABILITY.md`, `docs/DEPLOYMENT_ACTUAL.md`, `docs/evidence/`.

## Cấu trúc và giới hạn đã chọn

- 22 entity trong `src/main/java/model`; DAO1 + 5 DAO nghiệp vụ + StaffDAO1 trong `dao`; 8 JSP nghiệp vụ và 2 JSP auth trong `src/main/webapp/view`.
- Component UI đúng SPEC; đối tượng Reader/Supplier/Invoice và các mảng chỉ giữ server-side, không chuyển thành hidden HTML.
- Đăng ký forward request `reader`; refresh POST báo CCCD trùng. Mã thẻ dùng RD + reader.id, tối thiểu 6 số, có thể nhảy số do rollback.
- Thống kê một ngữ cảnh đang hoạt động mỗi session; kỳ mới hoặc chọn NCC khác làm link/tab cũ không còn hợp lệ. Back bằng nút trong app giữ ngữ cảnh.
- Chọn NCC và nhấn Xem ở StatisticSelectionPage1.jsp bắt đầu lượt mới: xóa ngày/kết quả/NCC/danh sách phiếu/token/lỗi ngữ cảnh cũ, mở form ngày trống; giữ auth và Session đăng nhập. Quay lại từ danh sách phiếu tới SupplierStatisticPage1.jsp giữ cả hai ngày và bảng kết quả.
- Mỗi lần truy cập hợp lệ SupplierImportInvoiceListPage1.jsp đều gọi `ImportInvoiceDAO1.getImportInvoicesBySupplier(supplier,startDate,endDate)`, kể cả chọn lại cùng NCC hoặc quay lại từ chi tiết. Session giữ ngữ cảnh/ngày/NCC và mảng mới cho trang chi tiết; không dùng mảng cũ để bỏ qua truy vấn. Trước khi gọi DAO, bỏ supplier/list cũ; lỗi truy vấn không để trang chi tiết sử dụng danh sách cũ.
- Nhãn/ngày kết quả dd/MM/yyyy; input date có value ISO, cách hiển thị bộ chọn ngày theo locale trình duyệt/Windows.
- Điện thoại demo chấp nhận 10–11 chữ số nội địa bắt đầu 0; không áp giới hạn tuổi. Hai loại thống kê khác hiện disabled và ghi chưa hiện thực.
- Validation đăng ký hiển thị lỗi cụ thể cho thiếu trường bắt buộc (liệt kê tên trường), CCCD, ngày sinh, điện thoại và email trong outErrorMessage hiện có. Nhiều lỗi được gom cùng thông báo; trường trống không bị báo thêm lỗi định dạng. Giữ giá trị đã trim/escape, đánh dấu aria-invalid; giới hạn độ dài theo DB vẫn được kiểm tra.
- Mọi quyết định bổ sung được ghi trong `docs/IMPLEMENTATION_DECISIONS.md`. Repository độc lập có Git root tại `C:\Users\Admin\Libman`, không quản lý thư mục user cha. `.local`, `.idea`, `target`, cache, binary build và credential bị ignore; config mẫu và bằng chứng kiểm thử được giữ trong repository. Chưa push GitHub; danh sách file initial commit xem `docs/COMMITTED_FILES.txt`.
