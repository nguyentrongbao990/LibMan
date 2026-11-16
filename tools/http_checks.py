"""HTTP integration checks against the running localhost demo; synthetic data only."""
import urllib.request, urllib.parse, urllib.error, http.cookiejar, re, html, concurrent.futures
BASE='http://127.0.0.1:18080/LibMan'
class Client:
    def __init__(self):
        self.cookies=http.cookiejar.CookieJar()
        self.opener=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(self.cookies))
    def get(self,path): return self.request(path)
    def post(self,path,data): return self.request(path,urllib.parse.urlencode(data).encode())
    def request(self,path,data=None):
        try:
            with self.opener.open(BASE+path,data=data,timeout=15) as r: return r.status,r.url,r.read().decode('utf-8')
        except urllib.error.HTTPError as e: return e.code,e.url,e.read().decode('utf-8')
def check(ok,message):
    if not ok: raise AssertionError(message)
    print('PASS '+message)
def link(body,text):
    for href,label in re.findall(r'<a[^>]*href="([^"]+)"[^>]*>(.*?)</a>',body,re.S):
        if text in label: return html.unescape(href).removeprefix('/LibMan')
    raise AssertionError('link not found: '+text)

c=Client()
for p in ['ManagementStaffHomePage1','StatisticSelectionPage1','SupplierStatisticPage1','SupplierImportInvoiceListPage1','ImportInvoiceDetailPage1']:
    status,url,body=c.get('/view/managementStaff/'+p+'.jsp')
    check(status==200 and url.endswith('/staff/LoginPage1.jsp'),'unauthenticated '+p+' redirected')
status,url,body=c.get('/view/reader/LibManHomePage1.jsp')
check('Đăng ký thẻ bạn đọc' in body and 'Ä' not in body,'home Vietnamese UTF-8')
status,url,body=c.get('/view/reader/ReaderCardRegistrationResultPage1.jsp')
check(url.endswith('ReaderCardRegistrationPage1.jsp'),'direct result access redirects safely')
form={'inCccd':'bad','inFullName':'<script>alert(1)</script>','inDateOfBirth':'2099-01-01','inAddress':'Demo','inPhone':'abc','inEmail':'bad'}
status,url,body=c.post('/view/reader/ReaderCardRegistrationPage1.jsp',form)
check(status==200 and 'outErrorMessage' in body and 'aria-invalid="true"' in body,'invalid input rejected server-side')
check('&lt;script&gt;' in body and '<script>alert(1)</script>' not in body,'form retained and escaped against XSS')
valid={'inCccd':'000999999999','inFullName':'Demo','inDateOfBirth':'2003-08-15','inAddress':'Demo','inPhone':'0900000000','inEmail':'demo@example.com'}
for field,value,message in [('inFullName','','trường bắt buộc: Họ và tên'),('inCccd','123','CCCD không hợp lệ'),('inDateOfBirth','2099-01-01','Ngày sinh không hợp lệ'),('inDateOfBirth','2003-02-30','Ngày sinh không hợp lệ'),('inEmail','invalid','Email không hợp lệ'),('inPhone','abc','Số điện thoại không hợp lệ')]:
    invalid=valid.copy();invalid[field]=value
    status,url,body=c.post('/view/reader/ReaderCardRegistrationPage1.jsp',invalid)
    check('outErrorMessage' in body and 'outSuccessMessage' not in body,'independent invalid field rejected: '+field)
    check(message in body and re.search(r'<input[^>]*id="'+field+r'"[^>]*aria-invalid="true"',body) is not None,'specific message and invalid mark: '+field)
    check(all('name="'+name+'" type="'+{'inCccd':'text','inFullName':'text','inDateOfBirth':'date','inAddress':'text','inPhone':'tel','inEmail':'email'}[name]+'" value="'+html.escape(text,quote=True)+'"' in body for name,text in invalid.items()),'all entered values retained: '+field)
for field,label in [('inCccd','CCCD'),('inDateOfBirth','Ngày sinh'),('inAddress','Địa chỉ'),('inPhone','Số điện thoại'),('inEmail','Email')]:
    invalid=valid.copy(); invalid[field]='   '
    status,url,body=c.post('/view/reader/ReaderCardRegistrationPage1.jsp',invalid)
    message=re.search(r'<div id="outErrorMessage"[^>]*>(.*?)</div>',body,re.S).group(1)
    check('trường bắt buộc: '+label in message and 'không hợp lệ' not in message and re.search(r'<input[^>]*id="'+field+r'"[^>]*aria-invalid="true"',body) is not None,'blank field gets required message only: '+field)
status,url,body=c.post('/view/reader/ReaderCardRegistrationPage1.jsp',{})
check('trường bắt buộc: CCCD, Họ và tên, Ngày sinh, Địa chỉ, Số điện thoại, Email.' in body and body.count('aria-invalid="true"')==6,'all required fields reported together')
invalid=valid.copy(); invalid.update(inCccd='abc',inPhone='abc',inEmail='bad')
status,url,body=c.post('/view/reader/ReaderCardRegistrationPage1.jsp',invalid)
check(all(message in body for message in ['CCCD không hợp lệ','Số điện thoại không hợp lệ','Email không hợp lệ']) and body.count('id="outErrorMessage"')==1,'multiple specific errors use the existing single label')
invalid=valid.copy(); invalid['inFullName']='D'*151
status,url,body=c.post('/view/reader/ReaderCardRegistrationPage1.jsp',invalid)
check('Họ và tên không được vượt quá 150 ký tự.' in body and 'value="'+'D'*151+'"' in body,'length validation and retained value preserved')
for username,password in [('manager_demo','wrong'),('library_demo','LibraryDemo1!')]:
    status,url,body=c.post('/view/staff/LoginPage1.jsp',{'username':username,'password':password})
    check('không đúng' in body and url.endswith('LoginPage1.jsp'),'invalid login / wrong role denied')
c.get('/view/staff/LoginPage1.jsp'); before=[x.value for x in c.cookies if x.name=='JSESSIONID']
status,url,body=c.post('/view/staff/LoginPage1.jsp',{'username':'manager_demo','password':'LibManDemo1!'})
after=[x.value for x in c.cookies if x.name=='JSESSIONID']
check(status==200 and url.endswith('ManagementStaffHomePage1.jsp') and before!=after,'login success and session rotated')
status,url,body=c.get('/view/managementStaff/StatisticSelectionPage1.jsp')
check(body.count('<option')==3 and body.count('disabled')==2,'three options; two outside scope disabled')
stats='/view/managementStaff/SupplierStatisticPage1.jsp'
status,url,body=c.get(stats+'?inStartDate=2026-01-01&inEndDate=2026-03-31&subViewStatistics=1')
check(all('>'+str(q)+'</td>' in body for q in [320,245,180]),'HTTP statistics 320/245/180')
first_list=link(body,'Công ty TNHH Sách Minh Triết')
status,url,body=c.get(first_list)
check(all(s in body for s in ['PN000101','PN000118','PN000137','minhtriet@example.com','01/01/2026','31/03/2026']),'supplier header, dates and invoices')
detail=link(body,'PN000137')
status,url,body=c.get(detail)
check(all(s in body for s in ['LS001','Trần Thị Bình','DOC001','DOC002','DOC003','4.800.000','4.050.000','5.500.000','14.350.000']),'invoice detail quantities/money/staff rendered')
status,url,body=c.get(link(body,'Quay lại'))
check('PN000137' in body and '31/03/2026' in body,'detail back retains supplier and dates')
status,url,body=c.get(link(body,'Quay lại'))
check(all('>'+str(q)+'</td>' in body for q in [320,245,180]) and 'value="2026-01-01"' in body and 'value="2026-03-31"' in body,'list back retains both dates and all results')
status,url,body=c.get(detail.replace(re.search(r'id=\d+',detail).group(0),'id=999999999'))
check(url.endswith('SupplierStatisticPage1.jsp') and 'không khớp' in body,'forged invoice id rejected')
for query in ['inStartDate=&inEndDate=2026-03-31','inStartDate=2026-04-01&inEndDate=2026-01-01','inStartDate=bad&inEndDate=2026-01-01']:
    status,url,body=c.get(stats+'?'+query)
    check('outErrorMessage' in body and '>320</td>' not in body,'invalid range clears old results')
status,url,body=c.get(first_list)
check(url.endswith('SupplierStatisticPage1.jsp'),'invalidated context rejects old supplier link')
status,url,body=c.get(stats+'?inStartDate=2024-01-01&inEndDate=2024-12-31')
check('Không có dữ liệu' in body and '>320</td>' not in body,'empty range notice')
status,url,body=c.get(stats+'?inStartDate=2026-01-01&inEndDate=2026-03-31')
status,url,body=c.get(first_list)
check(url.endswith('SupplierStatisticPage1.jsp'),'new period rejects stale context token')
status,url,body=c.get(stats)
old_list=link(body,'Công ty TNHH Sách Minh Triết')
status,url,body=c.get(old_list)
old_detail=link(body,'PN000137')
status,url,body=c.get('/view/managementStaff/StatisticSelectionPage1.jsp')
status,url,body=c.get(stats)
check('>320</td>' in body,'opening selection without choosing a type retains current context')
session_before=[x.value for x in c.cookies if x.name=='JSESSIONID']
status,url,body=c.get('/view/managementStaff/StatisticSelectionPage1.jsp?inStatisticType=supplier')
check(url.endswith('SupplierStatisticPage1.jsp') and 'value=""' in body and 'value="2026-01-01"' not in body and 'value="2026-03-31"' not in body and 'data-component="subSelectSupplier"' not in body and 'outErrorMessage' not in body,'new supplier statistics starts with empty dates/results and clears prior notice')
check(session_before==[x.value for x in c.cookies if x.name=='JSESSIONID'],'starting new statistics keeps the login session ID')
for path in [old_list,old_detail]:
    status,url,body=c.get(path)
    check(url.endswith('SupplierStatisticPage1.jsp') and 'không khớp' in body and 'PN000137' not in body,'new statistics invalidates prior supplier/invoice context')
status,url,body=c.get('/view/managementStaff/ManagementStaffHomePage1.jsp')
check(url.endswith('ManagementStaffHomePage1.jsp') and 'subViewStatistics' in body,'starting new statistics preserves authenticated management access')
status,url,body=c.get('/view/managementStaff/StatisticSelectionPage1.jsp?inStatisticType=supplier')
check('outErrorMessage' not in body and body.count('value=""')==2,'another fresh entry clears stale context errors')
status,url,body=c.get(stats+'?inStartDate=2026-01-01&inEndDate=2026-03-31')
check(all('>'+str(q)+'</td>' in body for q in [320,245,180]),'new statistics can query again without logging in')
status,url,body=c.get('/view/staff/LogoutPage1.jsp')
check(status==405,'GET logout rejected')
status,url,body=c.post('/view/staff/LogoutPage1.jsp',{})
status,url,body=c.get('/view/managementStaff/ManagementStaffHomePage1.jsp')
check(url.endswith('LoginPage1.jsp'),'POST logout invalidates session')
# New random synthetic reader for each run. Real sample CCCD reserved for browser demonstration.
import secrets
form={'inCccd':'000'+str(secrets.randbelow(10**9)).zfill(9),'inFullName':'Bạn đọc HTTP Demo','inDateOfBirth':'2003-08-15','inAddress':'Demo','inPhone':'0900000000','inEmail':'http@example.com'}
with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
    results=list(pool.map(lambda _:Client().post('/view/reader/ReaderCardRegistrationPage1.jsp',form),range(2)))
check(sum('outSuccessMessage' in r[2] for r in results)==1 and sum('CCCD đã đăng ký' in r[2] for r in results)==1,'two concurrent POSTs give exactly one success')
status,url,body=c.post('/view/reader/ReaderCardRegistrationPage1.jsp',form)
check('outSuccessMessage' not in body and 'CCCD đã đăng ký' in body,'refresh/resubmit rejected as duplicate')
print('HTTP checks complete. Only synthetic demo reader data was added.')
