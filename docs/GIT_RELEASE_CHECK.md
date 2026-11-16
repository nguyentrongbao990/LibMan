# Kiểm tra initial release Git — 16/11/2026

Repository độc lập: `C:\Users\Admin\Libman`, Git metadata tại `.git` bên trong project, nhánh `main`. Trước bước này Git root là `C:\Users\Admin`; không sửa/xóa repository cha. Fingerprint HEAD/config/index/packed-refs/refs của Git cha được so sánh trước/sau. Chưa cấu hình remote và chưa push.

## Phạm vi commit

Giữ đầy đủ src, database/schema.sql, seed.sql, migration NOT NULL, pom.xml, README, AGENTS, START_CODEX, tools, config/db.properties.example, tài liệu Markdown, tài liệu tham chiếu HTML/PNG/PDF và bằng chứng nghiệm thu. Danh sách chính xác trong `docs/COMMITTED_FILES.txt`.

`.gitignore` đã bổ sung chặn db.properties ở mọi thư mục, .env và biến thể, *.local.properties, private-key files, .local/.idea/target, cache Python/node và binary build/dependency. Chỉ cấu hình mẫu đã rà soát chứa placeholder được giữ. Không stage config DB thật hoặc cấu hình IDE. Các file *.txt dưới docs/evidence là bằng chứng kiểm thử cần thiết; không đưa log runtime/cache vào commit.

Đã rà soát toàn bộ đường dẫn và nội dung staged: chỉ file trong project, không symlink ra ngoài, không token/private key/credential URL theo mẫu phổ biến; đối chiếu literal mật khẩu PostgreSQL thật đọc cục bộ không thấy trong các file dự kiến commit, không in giá trị. Ảnh UI/evidence chỉ dùng dữ liệu giả lập và không chứa credential PostgreSQL. Tài khoản manager_demo/library_demo trong seed/README/tests là dữ liệu demo công khai đã được user chấp thuận trước đó, không phải tài khoản thật.

## Kiểm chứng tái triển khai từ repository

1. Dùng `git checkout-index --all --prefix=.../.local/repository-checkout/` xuất đúng Git index ra thư mục sạch; không copy src, dependency, target hay credential từ worktree gốc.
2. Maven 3.9.15 `-B -f .local/repository-checkout/pom.xml clean package`: **PASS**, 29 Java source, WAR sinh mới. Dùng Maven/JDK và cache Maven có sẵn trên máy; không khẳng định đã kiểm thử môi trường máy khác hoặc cache Maven trống.
3. Chạy schema.sql và seed.sql của bản xuất sạch trong schema PostgreSQL thử mới, transaction rollback toàn bộ: **PASS**, 19 bảng, categoryName NOT NULL, SUM quantity 320/245/180, PN000137 total 14350000.00 và tblReader trống. Không chạy DatabaseSetup1 lên libman có dữ liệu, không reset/drop database hiện hữu.
4. Cấu hình DB riêng chỉ trong .local của bản xuất, dùng Tomcat 11.0.22 đã cài và base riêng, HTTP loopback **18081**: **PASS** home UTF-8, auth guard, login quản lý demo, chọn kỳ mới, thống kê, ba phiếu Minh Triết và chi tiết PN000137. Chỉ auth/SELECT, không thêm dữ liệu nghiệp vụ.
5. Dừng đúng process Tomcat thử. Demo 18080 được giữ nguyên. Không sửa Java/JSP/SQL/pom/tools trong bước chuẩn bị Git. README chỉ cập nhật thông tin repo độc lập và hướng dẫn copy config/tùy chỉnh đường dẫn khi clone.

Bằng chứng lệnh/output: `docs/evidence/repository-preflight.txt`. `mvn package` không chạy JUnit vì project không có JUnit; kiểm tra runtime và SQL tách riêng như trên. Hồi quy hai module đã chạy trước và được giữ nguyên trong docs/evidence.

## Commit và tình trạng kiểm tra

Initial commit dùng đúng thông điệp **Initial release of LibMan** và Git author đã cấu hình trên máy. Kiểm tra lại Git root, tree/list file, ignore, nội dung staged và working tree sau commit. Không push cho đến khi user cung cấp URL repository.

`git diff --cached --check` phát hiện trailing spaces có sẵn trong SPEC và output Maven lưu làm bằng chứng. Giữ nguyên các file lịch sử này; không thay byte output để làm sạch cảnh báo. Kiểm tra whitespace riêng src/database/tools/pom không có lỗi. Chứng cứ/metadata xuất ra .local chỉ phục vụ kiểm tra và bị ignore, không được đưa vào commit.
