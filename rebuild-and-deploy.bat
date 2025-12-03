@echo off
echo ========================================
echo Rebuilding ArgoMachineManagement Project
echo ========================================
echo.

cd /d "%~dp0"

echo Step 1: Cleaning target directory...
if exist target rmdir /s /q target
echo Done.

echo.
echo Step 2: Please rebuild project in IntelliJ:
echo   1. Build -^> Rebuild Project (Ctrl+Shift+F9)
echo   2. Or use Maven: Lifecycle -^> clean -^> package
echo.
pause

echo.
echo Step 3: Copying WAR to Tomcat...
set TOMCAT_WEBAPPS=C:\Users\LOQ\Downloads\apache-tomcat-10.1.49\apache-tomcat-10.1.49\webapps

if exist "%TOMCAT_WEBAPPS%\ArgoMachineManagement.war" (
    echo Removing old WAR file...
    del "%TOMCAT_WEBAPPS%\ArgoMachineManagement.war"
)

if exist "%TOMCAT_WEBAPPS%\ArgoMachineManagement" (
    echo Removing old exploded folder...
    rmdir /s /q "%TOMCAT_WEBAPPS%\ArgoMachineManagement"
)

if exist "target\ArgoMachineManagement-1.0-SNAPSHOT.war" (
    echo Copying new WAR file...
    copy "target\ArgoMachineManagement-1.0-SNAPSHOT.war" "%TOMCAT_WEBAPPS%\ArgoMachineManagement.war"
    echo WAR file copied successfully!
) else (
    echo ERROR: WAR file not found in target directory!
    echo Please rebuild project first.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Deployment completed!
echo ========================================
echo.
echo Next steps:
echo 1. Restart Tomcat
echo 2. Access: http://localhost:8080/ArgoMachineManagement/list-user
echo.
pause

