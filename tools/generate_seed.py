"""Demo fixtures only; totals are computed from actual detail lines."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
sql=['-- Chạy MỘT LẦN sau schema.sql trên database demo mới. Không xóa dữ liệu.', 'BEGIN;']
def lit(v):
    if isinstance(v,int): return str(v)
    return "'"+v.replace("'","''")+"'"
def insert(table,cols,rows):
    sql.append('INSERT INTO "'+table+'" ('+','.join('"'+c+'"' for c in cols)+') VALUES\n'+',\n'.join('('+','.join(lit(v) for v in row)+')' for row in rows)+';')
insert('tblStaff',['staffCode','fullName','username','password'],[('LS001','Trần Thị Bình','library_demo','LibraryDemo1!'),('MS001','Quản lý Demo','manager_demo','LibManDemo1!')])
sql.append('INSERT INTO "tblManagementStaff" ("tblStaffid") SELECT "id" FROM "tblStaff" WHERE "staffCode"=\'MS001\';')
sql.append('INSERT INTO "tblLibraryStaff" ("id") SELECT "id" FROM "tblStaff" WHERE "staffCode"=\'LS001\';')
insert('tblSupplier',['supplierName','taxCode','address','phone','email'],[
('Công ty TNHH Sách Minh Triết','0101234567','Hà Nội','02437654321','minhtriet@example.com'),
('Công ty CP Phát hành Tri Thức','0102345678','Hà Nội','02430000002','trithuc@example.com'),
('Công ty TNHH Văn hóa Sao Việt','0103456789','Đà Nẵng','02360000003','saoviet@example.com')])
insert('tblDocument',['documentCode','title','publicationYear','edition'],[('DOC001','Lập trình hướng đối tượng',2025,'1'),('DOC002','Cơ sở dữ liệu',2025,'1'),('DOC003','Mạng máy tính',2025,'1')])
fixtures=[('PN000101','2026-01-10','0101234567',[(1,50,120000),(2,40,135000)]),('PN000118','2026-02-15','0101234567',[(1,60,120000),(3,50,110000)]),('PN000137','2026-03-20','0101234567',[(1,40,120000),(2,30,135000),(3,50,110000)]),('PN000201','2026-01-01','0102345678',[(1,100,120000)]),('PN000202','2026-03-31','0102345678',[(2,145,135000)]),('PN000301','2026-02-20','0103456789',[(3,180,110000)]),('PN000401','2025-12-31','0103456789',[(1,7,120000)]),('PN000402','2026-04-01','0102345678',[(2,9,135000)])]
for code,date,tax,details in fixtures:
    total=sum(q*p for _,q,p in details)
    sql.append(f'''INSERT INTO "tblImportInvoice" ("invoiceCode","importDate","totalAmount","tblSupplierid","tblLibraryStaffid")
SELECT '{code}','{date}',{total},s."id",l."id" FROM "tblSupplier" s CROSS JOIN "tblLibraryStaff" l
JOIN "tblStaff" st ON st."id"=l."id" WHERE s."taxCode"='{tax}' AND st."staffCode"='LS001';''')
    for doc,q,p in details:
        sql.append(f'''INSERT INTO "tblImportInvoiceDetail" ("quantity","unitPrice","tblImportInvoiceid","tblDocumentid")
SELECT {q},{p},i."id",d."id" FROM "tblImportInvoice" i CROSS JOIN "tblDocument" d
WHERE i."invoiceCode"='{code}' AND d."documentCode"='DOC{doc:03}';''')
sql+=['COMMIT;','']
(root/'database/seed.sql').write_text('\n'.join(sql),encoding='utf-8')
