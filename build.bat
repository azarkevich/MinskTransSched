cd MinskTransSched
call c:\ant\bin\ant.bat clean
call c:\ant\bin\ant.bat jar
cd ..
c:\cygwin\bin\bash build.sh
call c:\ant\bin\ant.bat clean
