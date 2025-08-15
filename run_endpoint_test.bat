@echo off
cd /d "D:\sy_study\IDEA_object\loveAgent"
echo Compiling Java classes...
call mvn compile -q
echo.
echo Running endpoint tests...
call mvn exec:java -Dexec.mainClass="com.example.loveagent.EndpointTestScript" -Dexec.args="" -q
pause