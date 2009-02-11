cd MinskTransSched
call %ANT_HOME%\bin\ant.bat clean
call %ANT_HOME%\bin\ant.bat jar

cd ..
c:\cygwin\bin\bash build.sh

cd MinskTransSched
call %ANT_HOME%\bin\ant.bat clean
