-- Chạy MỘT LẦN sau schema.sql trên database demo mới. Không xóa dữ liệu.
BEGIN;
INSERT INTO "tblStaff" ("staffCode","fullName","username","password") VALUES
('LS001','Trần Thị Bình','library_demo','LibraryDemo1!'),
('MS001','Quản lý Demo','manager_demo','LibManDemo1!');
INSERT INTO "tblManagementStaff" ("tblStaffid") SELECT "id" FROM "tblStaff" WHERE "staffCode"='MS001';
INSERT INTO "tblLibraryStaff" ("id") SELECT "id" FROM "tblStaff" WHERE "staffCode"='LS001';
INSERT INTO "tblSupplier" ("supplierName","taxCode","address","phone","email") VALUES
('Công ty TNHH Sách Minh Triết','0101234567','Hà Nội','02437654321','minhtriet@example.com'),
('Công ty CP Phát hành Tri Thức','0102345678','Hà Nội','02430000002','trithuc@example.com'),
('Công ty TNHH Văn hóa Sao Việt','0103456789','Đà Nẵng','02360000003','saoviet@example.com');
INSERT INTO "tblDocument" ("documentCode","title","publicationYear","edition") VALUES
('DOC001','Lập trình hướng đối tượng',2025,'1'),
('DOC002','Cơ sở dữ liệu',2025,'1'),
('DOC003','Mạng máy tính',2025,'1');
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000101','2026-01-10',11400000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0101234567' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 50,120000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000101' AND d."documentCode"='DOC001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 40,135000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000101' AND d."documentCode"='DOC002';
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000118','2026-02-15',12700000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0101234567' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 60,120000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000118' AND d."documentCode"='DOC001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 50,110000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000118' AND d."documentCode"='DOC003';
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000137','2026-03-20',14350000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0101234567' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 40,120000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000137' AND d."documentCode"='DOC001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 30,135000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000137' AND d."documentCode"='DOC002';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 50,110000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000137' AND d."documentCode"='DOC003';
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000201','2026-01-01',12000000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0102345678' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 100,120000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000201' AND d."documentCode"='DOC001';
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000202','2026-03-31',19575000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0102345678' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 145,135000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000202' AND d."documentCode"='DOC002';
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000301','2026-02-20',19800000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0103456789' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 180,110000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000301' AND d."documentCode"='DOC003';
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000401','2025-12-31',840000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0103456789' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 7,120000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000401' AND d."documentCode"='DOC001';
INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT 'PN000402','2026-04-01',1215000,s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='0102345678' AND st."staffCode"='LS001';
INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT 9,135000,i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='PN000402' AND d."documentCode"='DOC002';
COMMIT;
