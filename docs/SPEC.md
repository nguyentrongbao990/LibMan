# Đặc tả triển khai LibMan — bản bàn giao 16/11/2026

## 1. Phạm vi và nguồn
Thiết kế toàn hệ thống: 22 Entity, 19 bảng PostgreSQL, package view/dao/model. Code đầy đủ 2 module được giao + đăng nhập/đăng xuất nhân viên quản lý tối thiểu. Bản báo cáo 42 trang trước sửa: đặc tả/ngoại lệ v.1 trang 17–22, thiết kế trang 35–42. Các quyết định sửa sau review của giảng viên được hợp nhất tại đây: bỏ dấu `/` của 3 thuộc tính thống kê, bỏ UNIQUE riêng ở `tblReturnReceiptid`, UNIQUE ghép 4 bảng, giữ tên JSP và trường mới, thêm thành tiền/tổng tiền chi tiết phiếu. Không tự phục hồi thiết kế cũ.

## 2. Cấu trúc project đề xuất
```
LibMan/
  AGENTS.md
  pom.xml                    # packaging war; finalName LibMan (đề xuất)
  database/schema.sql
  database/seed.sql
  src/main/java/model/       # 22 Entity
  src/main/java/dao/         # 6 lớp DAO đã thiết kế + auth hỗ trợ nếu cần
  src/main/webapp/
    view/staff/              # login/logout tối thiểu
    view/reader/             # 3 JSP module 1, trang chủ là entrypoint chung
    view/libraryStaff/       # dành cho phạm vi toàn hệ thống, không cần code module ngoài đề
    view/managementStaff/    # 5 JSP module 2
    assets/                  # CSS/JS tối thiểu
    WEB-INF/                 # nếu cần cấu hình web
  docs/
```
Dùng context path cho URL. Có thể đặt welcome-file trỏ trang LibManHomePage1.jsp; cần lối vào đăng nhập nhân viên (chức năng hỗ trợ ngoài 8 mockup: ghi quyết định và đặt gọn, không thêm nút nghiệp vụ tùy tiện). Hai lớp `Staff1` và `DAO1` abstract; các Entity còn lại concrete. Getter/setter cần cho JSP và DAO. Không Lombok. DAO ngắn gọn, JDBC PreparedStatement; `DAO1.con` protected, kết nối theo thao tác/request, không dùng Connection chia sẻ toàn app. Đóng Connection/Statement/ResultSet đúng cách. Có thể dùng AutoCloseable hoặc close đơn giản để giải phóng tài nguyên, ghi rõ phần bổ sung kỹ thuật.

## 3. Entity — đầy đủ 22 lớp
Khai báo private field, kiểu Java sau đây là chuẩn. Không chuyển array -> List trong field hoặc return, không lặp id ở lớp con. Quan hệ tham chiếu thể hiện theo các field; không tự đổi hướng hay thêm field. Constructors/getters/setters không cần vẽ thành nghiệp vụ. Không hiện thực CRUD ở Entity.

| Lớp | Fields / extends |
|---|---|
| Staff1 (abstract) | id:long; staffCode:String; fullName:String; username:String; password:String |
| ManagementStaff1 | extends Staff1; không thêm field |
| LibraryStaff1 | extends Staff1; không thêm field |
| Reader1 | id:long; cccd:String; fullName:String; dateOfBirth:LocalDate; address:String; phone:String; email:String; readerCard:ReaderCard1 |
| ReaderCard1 | id:long; cardCode:String; issueDate:LocalDate; status:String |
| Document1 | id:long; documentCode:String; title:String; publicationYear:int; edition:String; documentAuthors:DocumentAuthor1[]; documentCategories:DocumentCategory1[]; publisher:Publisher1 |
| DocumentCopy1 | id:long; copyCode:String; status:String; condition:String; document:Document1 |
| Author1 | id:long; authorName:String |
| Publisher1 | id:long; publisherName:String |
| Category1 | id:long; categoryName:String |
| DocumentAuthor1 | id:long; author:Author1 |
| DocumentCategory1 | id:long; category:Category1 |
| Loan1 | id:long; loanCode:String; loanDate:LocalDate; dueDate:LocalDate; reader:Reader1; loanDetails:LoanDetail1[] |
| LoanDetail1 | id:long; documentCopy:DocumentCopy1 |
| ReturnReceipt1 | id:long; returnCode:String; returnDate:LocalDate; reader:Reader1; libraryStaff:LibraryStaff1; returnDetails:ReturnDetail1[] |
| ReturnDetail1 | id:long; lateFine:BigDecimal; damageFine:BigDecimal; loanDetail:LoanDetail1 |
| Supplier1 | id:long; supplierName:String; taxCode:String; address:String; phone:String; email:String |
| ImportInvoice1 | id:long; invoiceCode:String; importDate:LocalDate; totalAmount:BigDecimal; libraryStaff:LibraryStaff1; supplier:Supplier1; importInvoiceDetails:ImportInvoiceDetail1[] |
| ImportInvoiceDetail1 | id:long; quantity:int; unitPrice:BigDecimal; document:Document1 |
| ReaderStatistic1 | extends Reader1; loanCount:int |
| DocumentStatistic1 | extends Document1; loanCount:int |
| SupplierStatistic1 | extends Supplier1; importedVolume:long |

Quan hệ chính: Reader1 hợp thành ReaderCard1; ReturnReceipt1 hợp thành ReturnDetail1; các tham chiếu còn lại theo diagram và fields, kể cả DocumentCopy1.document. Ba lớp thống kê chỉ dùng cho kết quả truy vấn, không tạo bảng. Không thêm thành tiền vào Entity/database; là giá trị hiển thị tính bằng BigDecimal.

## 4. CSDL
`database/schema.sql` là DDL tham chiếu 19 bảng, chuẩn tên cột theo ERD cũ và quyết định sửa. Dùng PostgreSQL, int8 = bigint, int4 = integer, numeric(18,2). Tên SQL quoted case-sensitive. Lưu ý ERD thể hiện `tblManagementStaff.tblStaffid` nhưng `tblLibraryStaff.id` (PK/FK cùng tham chiếu tblStaff.id); không tự đồng nhất tên. 4 UNIQUE kết hợp (không đặt UNIQUE cho từng FK): tblDocumentAuthor(document,author), tblDocumentCategory(document,category), tblLoanDetail(loan,copy), tblImportInvoiceDetail(invoice,document). tblReturnDetail UNIQUE chỉ trên tblLoanDetailid, không trên tblReturnReceiptid. tblReaderCard có readerId unique. Các mã nghiệp vụ, CCCD, username, taxCode theo DDL. FK publisher cho phép NULL theo đặc tả trang 12, có thể chưa xác định NXB; ảnh cũ chưa thể hiện N, cần ghi nếu diagram cuối chưa đồng bộ. Theo xác nhận ERD mới nhất của user ngày 16/11/2026, `tblCategory.categoryName` là `varchar(150) NOT NULL`, thay thế quyết định nullable từ ảnh ERD cũ. `edition` và `Supplier.email` vẫn nullable. Chiến lược sinh ID identity là chi tiết triển khai đề xuất; không dùng MAX(id)+1. Không thêm CHECK, trigger, bảng role, bảng thống kê hay thay đổi khóa mà không thống nhất. Dữ liệu sample không chứa thông tin thật.

## 5. DAO — chữ ký nghiệp vụ cố định
```java
// public class ... extends DAO1; DAO1 abstract, protected Connection con;
public DAO1();
public ReaderDAO1();
public Reader1 findReaderByCccd(String cccd);
public ReaderCardDAO1();
public Reader1 registerReaderCard(Reader1 reader);
public SupplierStatisticDAO1();
public SupplierStatistic1[] getSupplierStatistics(LocalDate startDate, LocalDate endDate);
public ImportInvoiceDAO1();
public ImportInvoice1[] getImportInvoicesBySupplier(Supplier1 supplier, LocalDate startDate, LocalDate endDate);
public ImportInvoiceDetailDAO1();
public ImportInvoiceDetail1[] getImportInvoiceDetails(ImportInvoice1 importInvoice);
```
Đây là danh mục chữ ký, không phải một file Java hoàn chỉnh. Không tự đổi tham số sang id hoặc kiểu trả về boolean/List. Constructor và các phương thức hỗ trợ kỹ thuật (close, map row, xử lý lỗi) được phép; khai báo exception theo lựa chọn đơn giản, không nuốt lỗi truy vấn. `findReaderByCccd` trả null CHỈ khi không tìm thấy; lỗi kết nối phải là lỗi, không được giả vờ CCCD chưa tồn tại.

## 6. Module 1 — RegisterReaderCard1
### 3 JSP (tên component đúng Class Diagram mới)
| JSP | Thuộc tính theo thiết kế |
|---|---|
| LibManHomePage1.jsp | subRegisterReaderCard:link |
| ReaderCardRegistrationPage1.jsp | inCccd:text; inFullName:text; inDateOfBirth:text; inAddress:text; inPhone:text; inEmail:text; subSaveRegistration:submit; subCancelRegistration:link; outErrorMessage:label |
| ReaderCardRegistrationResultPage1.jsp | outSuccessMessage:label; reader:Reader1 |
Dùng đúng tên HTML input/id/name phù hợp. `reader` là dữ liệu server-side để đọc `reader.readerCard`, không thêm outReaderInfo/outReaderCardInfo/hidden field dư thừa. Ngày hiển thị dd/MM/yyyy, input HTML date nhận ISO. CCCD text giữ số 0; phone không kiểu number. Dữ liệu ví dụ v.1: 001203012345, Nguyễn Văn An, 15/08/2003, Hà Nội, 0912345678, nguyenvanan@example.com (dữ liệu minh họa). Thẻ RD000001 là ví dụ, ngày cấp thực tế là ngày đăng ký, không hardcode ngày mockup. Không seed trước Reader CCCD ví dụ nếu dùng để demo lần đăng ký đầu.

### Trình tự đúng 18 bước (gộp nhóm, không sinh thêm method giả)
1–4: Homepage link -> Registration JSP hiển thị form -> submit POST.
5–7: Registration JSP tạo DAO và gọi `findReaderByCccd(cccd)` -> null trong happy path.
8–10: Registration JSP tạo và đóng gói Reader1 với 6 trường hợp lệ.
11–16: JSP gọi `registerReaderCard(reader)`; DAO tạo ReaderCard1, lưu Reader và ReaderCard trong một transaction, gắn vào reader, commit rồi trả reader. Nếu lỗi rollback cả hai, không cấp thẻ/mã thành công. Có UNIQUE CCCD phòng race; phân biệt duplicate và lỗi DB. 
17–18: chuyển server-side tới Result JSP và hiển thị thông báo + đủ 6 trường reader và 3 trường thẻ. Không đưa thông tin cá nhân vào URL. Forward request attribute `reader`; có thể dùng redirect + session flash nếu được chọn và ghi nhận, nhưng không được lặp lưu khi refresh. Với forward, nếu refresh POST xảy ra thì UNIQUE + kiểm tra trước bảo vệ khỏi đăng ký trùng; ghi hạn chế. Truy cập trực tiếp result không có reader: chuyển về trang đăng ký/an toàn.

### Cấp mã thẻ
Đề xuất đơn giản, không dùng MAX(): INSERT Reader RETURNING id (hoặc generated keys), mã `RD` + id định dạng tối thiểu 6 chữ số. Dùng id duy nhất của reader, cấp một thẻ/reader, `cardCode` UNIQUE. Tạo ReaderCard1 với ngày hiện tại `LocalDate.now()` và ACTIVE, lưu thẻ và nhận generated id. Có thể có khoảng trống mã do rollback; không yêu cầu mã liên tục. Phương án này là quyết định kỹ thuật đề xuất, ghi log quyết định; nếu user có quy tắc mã khác, dừng hỏi trước đổi.

### Ngoại lệ theo v.1
Hủy về home, không lưu. Cả 6 trường bắt buộc; CCCD đúng 12 chữ số, điện thoại kiểm tra định dạng (đề xuất 10 chữ số nội địa hoặc 10–11 chữ số nếu user chốt; không tự áp quy tắc giới hạn tuổi), email phù hợp, ngày sinh hợp lệ không tương lai; trim dữ liệu và kiểm tra độ dài theo DB. Lỗi đánh dấu/giữ dữ liệu nhập (escape HTML) + thông báo. Duplicated CCCD -> thông báo đăng ký rồi, không tạo mới. Lỗi lưu -> giữ form và báo thử lại; không hiển thị stacktrace/SQL. Không cần login bạn đọc.

## 7. Module 2 — ViewSupplierStatistics1
### 5 JSP đã sửa
| JSP | Thuộc tính theo thiết kế |
|---|---|
| ManagementStaffHomePage1.jsp | subViewStatistics:link |
| StatisticSelectionPage1.jsp | inStatisticType:Select; subView:submit; subBack:link |
| SupplierStatisticPage1.jsp | inStartDate:text; inEndDate:text; subViewStatistics:submit; outSupplierStatisticTable:Table; subSelectSupplier:link; outErrorMessage:label; subBack:link; supplierStatisticList:SupplierStatistic1[] |
| SupplierImportInvoiceListPage1.jsp | outStartDate:label; outEndDate:label; outImportInvoiceTable:Table; subSelectImportInvoice:link; subBack:link; supplier:Supplier1; importInvoiceList:ImportInvoice1[] |
| ImportInvoiceDetailPage1.jsp | outImportInvoiceDetailTable:Table; subBack:link; importInvoice:ImportInvoice1 |
Không field managementStaff trên 5 JSP; session giữ auth. Không thêm supplier riêng ở trang chi tiết (đọc importInvoice.supplier); không thêm list detail riêng (gắn vào importInvoice.importInvoiceDetails). Giữ supplier ở trang danh sách vì là đầu vào DAO và thông tin header. `Table` là HTML chứ không tạo Java class Table; các mảng là dữ liệu server-side. Select hiện 3 lựa chọn như mockup; 2 lựa chọn ngoài phạm vi không tính giả, báo rõ chưa hiện thực hoặc disable có nhãn giải thích. Không tự bỏ chúng khỏi giao diện.

### Trình tự 32 bước và dữ liệu
1–7: nhân viên quản lý đã login -> Trang chủ quản lý -> Chọn loại TK -> chọn NCC và Xem -> biểu mẫu ngày, nhấn Xem thống kê. Không tách self-submit riêng trên diagram.
8–14: SupplierStatisticPage gọi SupplierStatisticDAO1.getSupplierStatistics(startDate,endDate) -> tạo SupplierStatistic1 cho mỗi NCC trong kết quả, đủ các thuộc tính Supplier1 và importedVolume -> trả SupplierStatistic1[] -> bảng hiển thị.
15–16: click NCC (id) -> SupplierImportInvoiceListPage, truyền đúng Supplier1 và ngày bắt đầu/kết thúc.
17–23: JSP gọi ImportInvoiceDAO1.getImportInvoicesBySupplier(supplier,startDate,endDate) -> tạo ImportInvoice1 đầy đủ (id, invoiceCode, importDate, totalAmount, supplier và libraryStaff) -> trả ImportInvoice1[] -> hiển thị header NCC, hai ngày và danh sách phiếu.
24–25: click phiếu (id) -> ImportInvoiceDetailPage, đúng ImportInvoice1 và thời gian.
26–32: JSP gọi ImportInvoiceDetailDAO1.getImportInvoiceDetails(importInvoice) -> tạo ImportInvoiceDetail1 chứa Document1 tương ứng -> trả ImportInvoiceDetail1[] -> JSP gắn mảng vào importInvoice rồi hiển thị toàn bộ dữ liệu. Không tự thêm DAO mới cho supplier/staff/document ở luồng chính. Sequence Diagram tự gọi minh họa nội bộ, không recursive. Không cần tạo bảng SQL trung gian/statistics.

### Quy tắc truy vấn
- Ngày đầu/cuối inclusive (importDate kiểu DATE). `SUM(d.quantity)` GROUP BY supplier id và trường cần hiển thị, join invoice/detail/supplier, chỉ NCC có dữ liệu nhập trong kỳ. Không COUNT phiếu, không sum tiền, không join bảng bản sao/tác giả làm nhân dòng. Giảm dần importedVolume; nếu bằng nhau, dùng id tăng dần để ổn định (đề xuất). PostgreSQL SUM(integer) -> bigint, map long. Không hardcode 320/245/180.
- Danh sách phiếu theo supplier.id và cùng khoảng ngày; đề xuất ORDER BY importDate, id ASC để khớp ví dụ. JOIN tblLibraryStaff (id) -> tblStaff (id) lấy staffCode/fullName; JOIN tblSupplier lấy đủ header. Không load mật khẩu nhân viên lập phiếu ra UI. Giữ totalAmount đã lưu.
- Chi tiết theo importInvoice.id; JOIN tblDocument lấy documentCode/title (+ các trường scalar có sẵn theo model), không cần tải toàn bộ tác giả/thể loại không dùng. Đọc quantity, unitPrice BigDecimal. Thành tiền = unitPrice.multiply(BigDecimal.valueOf(quantity)); tổng tiền lấy importInvoice.totalAmount, không cập nhật DB trong module chỉ đọc. Dữ liệu seed phải thỏa tổng tiền bằng sum chi tiết. Không sử dụng double, không round sai. Format VNĐ dễ đọc, không che mất phần lẻ nếu dữ liệu có.
- Chọn đối tượng hợp lệ từ kết quả đã truy vấn: dùng session theo kỳ thống kê để giữ mảng và LocalDate, id trên link; xác thực id thuộc ngữ cảnh, không tin dữ liệu tổng/đối tượng gửi từ client. Giải pháp đơn giản đề xuất: 1 ngữ cảnh thống kê đang hoạt động mỗi phiên, ghi hạn chế đa tab; nút Quay lại giữ nguyên ngày, NCC, kết quả đúng. Nếu context hết hạn/mismatch, quay về form với thông báo. Không thêm getter API DAO ngoài thiết kế chỉ để xử lý id. Khi truy vấn kỳ mới, bỏ context cũ. Không truy cập trực tiếp trang quản lý khi chưa đăng nhập. Request GET cho đọc, POST cho đăng nhập/đăng ký/đăng xuất.
- Không nhập thiếu ngày; startDate <= endDate; ngày không hợp lệ/empty range notice, giữ trang và input. Nếu không có dữ liệu, hiển thị bảng rỗng và message như báo cáo. Thay ngày chạy query lại. Back ở màn chọn loại -> home; back thống kê -> chọn loại; back list -> thống kê giữ ngày và kết quả; back detail -> list đúng NCC và ngày.

### Thành phần hiển thị khớp wireframe mới
- Homepage quản lý: Xem thống kê.
- Chọn loại thống kê: 3 option, Xem, Quay lại.
- Thống kê: Ngày bắt đầu/kết thúc, Xem thống kê; TT, tên NCC (link), MST, tổng SL nhập; Quay lại; lỗi/rỗng khi cần.
- Danh sách: tên, MST, địa chỉ, điện thoại, email NCC; ngày bắt đầu/kết thúc; TT, mã phiếu (link), ngày nhập; Quay lại.
- Chi tiết: mã phiếu, ngày nhập, mã và tên nhân viên lập phiếu; 5 thông tin NCC; TT, mã TL, tên TL, SL, đơn giá, thành tiền; tổng tiền; Quay lại. Không khai báo thêm outTotalAmount vì hiển thị từ importInvoice.totalAmount. Không đặt trường ID nội bộ hoặc password lên UI.

## 8. Seed demo bắt buộc
Dữ liệu đã cho trong v.1 và wireframe: kỳ 01/01/2026–31/03/2026; nhà cung cấp theo thứ tự bảng:
- Công ty TNHH Sách Minh Triết | 0101234567 | Hà Nội | 02437654321 | minhtriet@example.com | tổng 320.
- Công ty CP Phát hành Tri Thức | 0102345678 | tổng 245.
- Công ty TNHH Văn hóa Sao Việt | 0103456789 | tổng 180.
- NCC Minh Triết có PN000101 ngày 10/01/2026, PN000118 ngày 15/02/2026, PN000137 ngày 20/03/2026.
- PN000137: DOC001 Lập trình hướng đối tượng 40 × 120000 = 4800000; DOC002 Cơ sở dữ liệu 30 × 135000 = 4050000; DOC003 Mạng máy tính 50 × 110000 = 5500000; tổng 120 tài liệu, 14.350.000 VNĐ. Nhân viên lập phiếu LS001 – Trần Thị Bình (dữ liệu mẫu đã thống nhất). Hai phiếu còn lại phải tổng 200. Đề xuất chia 90 và 110; dữ liệu chi tiết tự tạo hợp lý, ghi rõ là seed bổ sung, tính tổng tiền từ dòng chi tiết. Hai NCC khác có detail thật sao cho tổng 245 và 180. Có ít nhất một invoice ngoài kỳ + invoice ở ngày biên để kiểm thử (không làm thay đổi 3 kết quả mong đợi hay danh sách 3 phiếu Minh Triết; dữ liệu biên có thể thuộc NCC khác trong kỳ và tổng đã tính). Không lấy số thống kê từ cột cache hay seed thẳng thống kê.
- Demo quản lý: staff thuộc tblManagementStaff; thư viện LS001 thuộc tblLibraryStaff. ID tham chiếu đúng. Seed có thể chạy một lần trên DB trống hoặc idempotent không xóa dữ liệu; viết hướng dẫn rõ ràng. Không cần seed hết mỗi bảng không dùng, nhưng schema phải đủ 19.

## 9. Đăng nhập và các quyết định giản lược cần ghi nhận
Không được tự động thiết kế thêm toàn bộ nghiệp vụ auth. Đề xuất 2 JSP hỗ trợ tên `LoginPage1.jsp`, `LogoutPage1.jsp` trong view/staff; một `StaffDAO1` tối thiểu nếu cần để kiểm tra tài khoản thuộc tblManagementStaff bằng PreparedStatement. Tên/chữ ký auth chưa có trong diagram hai module, vì vậy đây là bổ sung triển khai chứ không phải phần đã thiết kế. Có thể chọn `login(String username, String password): ManagementStaff1` (đề xuất). Session chứa id/role hoặc đối tượng quản lý không mang mật khẩu; logout invalidate. Kiểm tra session và role trên tất cả 5 JSP quản lý. Đổi session ID sau login nếu API hỗ trợ. Không cung cấp app ngoài localhost.
- Mật khẩu: đề xuất mặc định băm mật khẩu bằng BCrypt với một dependency nhỏ, hai thao tác hash/verify; không gọi đây là mã hóa. Nếu người dùng chọn mật khẩu rõ cho đồ án, chỉ cho phép tài khoản demo giả lập, localhost, ghi rõ hạn chế và không dùng thật; không nhúng password thật hay công bố DB credentials. Hỏi user chọn nếu chưa rõ, không trì hoãn viết phần không phụ thuộc. Không tự thêm JWT, framework security, mã hóa dữ liệu.
- Cấu hình DB: biến môi trường hoặc file local ngoài git; không hardcode password PostgreSQL. PreparedStatement và escape output HTML; xử lý lỗi chung không lộ thông tin. Với ứng dụng local, không tự setup TLS/HTTPS. Chỉ ghi giao thức thực tế sau khi test; deployment diagram hoàn thiện sau.
- Nếu cần bổ sung lối đăng nhập/đăng xuất ngoài mockup, thể hiện tối thiểu, ghi rõ là UI hỗ trợ; không làm lệch các thành phần chính. Điều kiện chưa chốt: phiên bản JDK/Tomcat/port, credential DB, dự định phương án mật khẩu, nullability publisher theo diagram cuối. Báo cáo các điểm này, không tự khẳng định cấu hình của máy.
