# 🛡️ Abdal DroidGuard

<div align="center">
  <img src="shot-en.png" alt="Abdal DroidGuard Screenshot"    >
</div>

## 📘 Other Languages

- [🇮🇷 Persian - فارسی](README.fa.md)


## 📖 About This Software

**Abdal DroidGuard** is an advanced and powerful Android application hardening tool developed by **Ebrahim Shafiei (EbraSha)**. This software uses cutting-edge security techniques to protect Android applications against reverse engineering, tampering, and various cyber attacks.

### 🎯 Purpose and Application

This software was designed to solve the security challenges of Android applications. With the increasing number of cyber attacks and reverse engineering attempts, there was a growing need for a powerful tool to protect application code and logic. Abdal DroidGuard addresses this need and provides developers with professional-grade application protection capabilities.

## ✨ Features and Capabilities

### 🔒 Advanced Code Obfuscation
- **JVM and DEX Bytecode Obfuscation**: Transformation and complexity enhancement of Java Virtual Machine and Android Dalvik bytecodes
- **String Encryption and Control Flow Obfuscation**: Hiding sensitive strings and complicating program logic
- **Method and Class Name Obfuscation**: Converting readable names to unrecognizable identifiers
- **Arithmetic Obfuscation**: Complicating mathematical and logical operations

### 🛡️ Tamper Detection
- **Application File Integrity Verification**: Monitoring unauthorized changes in package files
- **Signature Validation**: Verifying the authenticity of digital signatures
- **Runtime Integrity Checks**: Continuous monitoring of potential modifications
- **Anti-tampering Protection Mechanisms**: Preventing unauthorized alterations

### 🚀 RASP (Runtime Application Self-Protection)
- **Anti-debugging Detection and Prevention**: Identifying and preventing debugging attempts
- **Emulator Detection**: Identifying emulation environments and preventing execution
- **Root Detection**: Identifying rooted devices and applying restrictions
- **Hook Detection**: Identifying hooking frameworks like Xposed, Frida, and Substrate
- **Runtime Monitoring and Protection**: Continuous monitoring of application behavior

## 🚀 How to Use the Software

### 📋 Prerequisites
- **Java 21 or higher**: Required to run the software
- **Android SDK** (optional): For advanced APK signing
- **APK File Access**: The target Android application file

### ⚙️ Installation and Setup

1. **Download the Software**: Download project files from the repository
2. **Compile**: Build the software using build commands
3. **Run**: Execute the software with your target APK file

### 💻 Usage Commands

```bash
# Compile the software
.\build-fix.bat

# Run hardening with all features
java -jar build\abdal-droidguard-simple.jar your_app.apk --all --verbose

# Run hardening with selected features
java -jar build\abdal-droidguard-simple.jar your_app.apk --obfuscate --tamper-detect --rasp
```

### 📝 Command Line Parameters

- `--all`: Enable all protection features
- `--obfuscate`: Enable code obfuscation
- `--tamper-detect`: Enable tamper detection
- `--rasp`: Enable runtime application self-protection
- `--verbose`: Show more detailed output
- `-o, --output`: Specify output file path

## 🔧 Advanced Configuration

### ⚙️ Configuration File
You can modify software settings through the `application.properties` file:

```properties
# Obfuscation settings
obfuscation.enabled=true
obfuscation.level=high

# Tamper detection settings
tamper.detection.enabled=true
tamper.check.interval=5000

# RASP settings
rasp.enabled=true
rasp.debug.detection=true
rasp.emulator.detection=true
```

 
 

## 📚 Technical Documentation

### 🏗️ Software Architecture
The software uses a modular architecture:
- **APKParser**: APK parsing and extraction
- **DexProcessor**: DEX file processing
- **InjectionEngine**: Protection code injection
- **APKBuilder**: APK rebuilding
- **APKSigner**: APK signing and alignment

### 🔐 Security Algorithms
- **XOR Encryption**: For string encryption
- **SHA-256**: For integrity verification
- **CRC32**: For file integrity checks
- **Custom Obfuscation**: Proprietary obfuscation algorithms

## 🌟 Unique Features

### 🎯 Native Design
- **No External Dependencies**: Completely native and independent software
- **High Performance**: Optimized for speed and efficiency
- **Full Compatibility**: Compatible with all Android versions

### 🔒 Advanced Security
- **Multi-layer Protection**: Combination of various security techniques
- **Analysis Resistance**: Resistant to reverse engineering tools
- **Continuous Updates**: Support for latest security threats

## 📞 Support and Contact

### 🐛 Reporting Issues
If you encounter any issues or have configuration problems, please reach out via email at **Prof.Shafiei@Gmail.com**. You can also report issues on GitLab or GitHub.

### ❤️ Donation
If you find this project helpful and would like to support further development, please consider making a donation:
- [Donate Here](https://alphajet.ir/abdal-donation)

### 🤵 Programmer
Handcrafted with Passion by **Ebrahim Shafiei (EbraSha)**
- **E-Mail**: Prof.Shafiei@Gmail.com
- **Telegram**: [@ProfShafiei](https://t.me/ProfShafiei)

### 📜 License
This project is licensed under the GPLv2 or later License.

 
 