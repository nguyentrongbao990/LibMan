"""Compare implementation declarations against SPEC, not generated expectations."""
from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
spec=(root/'docs/SPEC.md').read_text(encoding='utf-8')
rows=spec.split('## 3. Entity')[1].split('## 4. CSDL')[0]
expected={name:definition for name,definition in re.findall(r'^\| (\w+1)(?: \(abstract\))? \| (.*?) \|$',rows,re.M)}
assert len(expected)==22
actual={p.stem:p for p in (root/'src/main/java/model').glob('*.java')}
assert expected.keys()==actual.keys()
for name,definition in expected.items():
    source=actual[name].read_text(encoding='utf-8')
    fields=re.findall(r'private ([\w\[\]]+) (\w+);',source)
    assert fields==[(t,n) for n,t in re.findall(r'(\w+):([\w\[\]]+)',definition)],name
    base=re.search(r'extends (\w+1)',definition)
    source_base=re.search(r'extends (\w+1)',source)
    assert (base.group(1) if base else None)==(source_base.group(1) if source_base else None),name
    assert ('abstract class' in source)==(name=='Staff1'),name
for cls,signature in {
    'ReaderDAO1':'public Reader1 findReaderByCccd(String cccd)',
    'ReaderCardDAO1':'public Reader1 registerReaderCard(Reader1 reader)',
    'SupplierStatisticDAO1':'public SupplierStatistic1[] getSupplierStatistics(LocalDate startDate, LocalDate endDate)',
    'ImportInvoiceDAO1':'public ImportInvoice1[] getImportInvoicesBySupplier(Supplier1 supplier, LocalDate startDate, LocalDate endDate)',
    'ImportInvoiceDetailDAO1':'public ImportInvoiceDetail1[] getImportInvoiceDetails(ImportInvoice1 importInvoice)',
}.items():
    source=(root/f'src/main/java/dao/{cls}.java').read_text(encoding='utf-8')
    assert signature in source,cls
    assert f'public class {cls} extends DAO1' in source,cls
assert 'protected Connection con;' in (root/'src/main/java/dao/DAO1.java').read_text(encoding='utf-8')
schema=(root/'database/schema.sql').read_text(encoding='utf-8')
assert len(re.findall('CREATE TABLE',schema))==19
pages={p.name:p.read_text(encoding='utf-8') for p in (root/'src/main/webapp/view').rglob('*.jsp')}
assert len(pages)==10
for name,source in pages.items():
    assert name.endswith('1.jsp')
    assert 'pageEncoding="UTF-8"' in source
    if '/managementStaff/' in str(next((root/'src/main/webapp/view').rglob(name))).replace('\\','/'):
        assert 'auth.jspf' in source,name
print('PASS: 22 exact model declarations/inheritance; 5 business DAO signatures; protected connection; 19 DDL tables; 8 business + 2 auth JSP.')
