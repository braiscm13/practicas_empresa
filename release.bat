SET mypath=%~dp0
set JAVA_HOME=%mypath%java
set REPO_HOME="%mypath%repository/"
set SETTINGS_XML="%mypath%settings.xml"
del %SETTINGS_XML%

@echo off
setlocal enabledelayedexpansion
set INTEXTFILE=settings_template.xml
set OUTTEXTFILE=settings.xml
set SEARCHTEXT=#PATH#
set REPLACETEXT=%REPO_HOME%
set OUTPUTLINE=

for /f "tokens=1,* delims=¶" %%A in ( '"type %INTEXTFILE%"') do (
SET string=%%A
SET modified=!string:%SEARCHTEXT%=%REPLACETEXT%!

echo !modified! >> %OUTTEXTFILE%
)
@echo on


"%mypath%maven/bin/mvn.bat" -o clean install -Ppre-pro,generate-jnlp -Dmaven.test.skip=true -DskipTests=true -s %SETTINGS_XML% -Dmaven.repo.local=%REPO_HOME%