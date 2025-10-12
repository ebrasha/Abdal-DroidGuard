@echo off

echo [INFO] Building Abdal DroidGuard with Manifest Fix
echo [INFO] ===========================================

REM Create directories
if not exist "build" mkdir "build"
if not exist "build\classes" mkdir "build\classes"

echo [INFO] Compiling Java files...

REM Compile REAL classes only
javac -d build\classes -sourcepath src\main\java ^
    src\main\java\com\ebrasha\droidguard\SimpleAbdalDroidGuard.java ^
    src\main\java\com\ebrasha\droidguard\core\RealAPKHardener.java ^
    src\main\java\com\ebrasha\droidguard\core\ManifestProcessor.java ^
    src\main\java\com\ebrasha\droidguard\core\RealObfuscationEngine.java ^
    src\main\java\com\ebrasha\droidguard\core\RealTamperDetection.java ^
    src\main\java\com\ebrasha\droidguard\core\RealRASProtection.java ^
    src\main\java\com\ebrasha\droidguard\core\AdvancedEmulatorDetector.java ^
    src\main\java\com\ebrasha\droidguard\core\NativeRASPSecurity.java ^
    src\main\java\com\ebrasha\droidguard\core\APKParser.java ^
    src\main\java\com\ebrasha\droidguard\core\DexProcessor.java ^
    src\main\java\com\ebrasha\droidguard\core\InjectionEngine.java ^
    src\main\java\com\ebrasha\droidguard\core\APKBuilder.java ^
    src\main\java\com\ebrasha\droidguard\core\APKSigner.java ^
    src\main\java\com\ebrasha\droidguard\utils\SimpleLogger.java ^
    src\main\java\com\ebrasha\droidguard\utils\AndroidSDKConfig.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [SUCCESS] Compilation completed!

REM Create manifest file
echo [INFO] Creating manifest file...
(
echo Manifest-Version: 1.0
echo Main-Class: com.ebrasha.droidguard.SimpleAbdalDroidGuard
echo Created-By: Abdal DroidGuard
echo Author: Ebrahim Shafiei ^(EbraSha^)
echo Email: Prof.Shafiei@Gmail.com
echo.
) > build\manifest.txt

REM Create JAR with manifest
cd build\classes
jar cfm ..\abdal-droidguard.jar ..\manifest.txt com\
cd ..\..

REM Copy configuration file to build directory
copy android-sdk-config.properties build\android-sdk-config.properties

REM Cleanup manifest file
del build\manifest.txt

echo [SUCCESS] JAR created: build\abdal-droidguard.jar
echo [SUCCESS] Configuration file copied: build\android-sdk-config.properties
echo.
echo [INFO] Press Enter to continue.....


pause

java -jar build\abdal-droidguard.jar your_app.apk --all --verbose

REM ## Foe Exam:  java -jar build\abdal-droidguard.jar your_app.apk --obfuscate --tamper-detect --rasp --verbose

echo [INFO] End of compilation.

pause
