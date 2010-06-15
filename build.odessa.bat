call %ANT_HOME%\bin\ant.bat clean
call %ANT_HOME%\bin\ant.bat build -Dcity=Odessa | iconv -f cp1251 -t utf-8
