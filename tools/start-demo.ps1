param(
    [string]$TomcatHome = 'C:\Users\Admin\apache-tomcat-11.0.22',
    [int]$Port = 18080
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$demoBase = Join-Path $projectRoot '.local\tomcat'
if (!(Test-Path -LiteralPath (Join-Path $TomcatHome 'lib\catalina.jar'))) { throw 'TomcatHome không hợp lệ.' }
if (!(Test-Path -LiteralPath (Join-Path $projectRoot 'target\LibMan.war'))) { throw 'Hãy chạy mvn clean package trước.' }
$probe = [Net.Sockets.TcpListener]::new([Net.IPAddress]::Loopback,$Port)
try { $probe.Start() } finally { $probe.Stop() }
foreach ($dir in 'conf','logs','temp','webapps','work') { New-Item -ItemType Directory -Force (Join-Path $demoBase $dir) | Out-Null }
foreach ($file in 'web.xml','catalina.properties','logging.properties','context.xml') {
    Copy-Item -LiteralPath (Join-Path $TomcatHome "conf\$file") -Destination (Join-Path $demoBase "conf\$file")
}
@"
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1">
 <Service name="Catalina">
  <Connector address="127.0.0.1" port="$Port" protocol="HTTP/1.1" connectionTimeout="20000" />
  <Engine name="Catalina" defaultHost="localhost"><Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="false" /></Engine>
 </Service>
</Server>
"@ | Set-Content -LiteralPath (Join-Path $demoBase 'conf\server.xml') -Encoding utf8
Copy-Item -LiteralPath (Join-Path $projectRoot 'target\LibMan.war') -Destination (Join-Path $demoBase 'webapps\LibMan.war')
$javaArgs = @(
    "-Dcatalina.home=`"$TomcatHome`"", "-Dcatalina.base=`"$demoBase`"",
    "-Djava.io.tmpdir=`"$demoBase\temp`"", '-Duser.timezone=Asia/Ho_Chi_Minh',
    "-Dlibman.db.config=`"$projectRoot\.local\db.properties`"",
    "-Djava.util.logging.config.file=`"$demoBase\conf\logging.properties`"",
    '-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager',
    '-cp', "`"$TomcatHome\bin\bootstrap.jar;$TomcatHome\bin\tomcat-juli.jar`"",
    'org.apache.catalina.startup.Bootstrap','start'
)
$process = Start-Process -FilePath (Get-Command java).Source -ArgumentList $javaArgs -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $demoBase 'logs\stdout.log') -RedirectStandardError (Join-Path $demoBase 'logs\stderr.log')
$process.Id | Set-Content -LiteralPath (Join-Path $demoBase 'process.id')
Write-Output "Tomcat PID $($process.Id); đang khởi động tại http://127.0.0.1:$Port/LibMan/"
