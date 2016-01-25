set nowpath=%~dp0
cd /d %nowpath%

echo compile.......
cd ../../../../../../../build
call ant clean openfire plugin -Dplugin=vplugin

echo replace openfire.xml.....
cd ../target/openfire/conf/
copy /y openfire-local-admin-1.xml openfire.xml

echo package.........
cd ../../
echo %CD%
del openfire.tar openfire.tar.gz
set zip="D:\Program Files\2345Soft\HaoZip\HaoZipC.exe"
%zip% a -ttar openfire.tar
%zip% a -tzip openfire.tar.gz openfire.tar
del openfire.tar

cd /d %nowpath%

pause