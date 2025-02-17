# OpenHealth Requirements

## ✨ Overview
OpenHealth Requirements scans source files for structured requirement annotations and extracts them into a structured CSV file. It is available as both a **Gradle plugin** and a **standalone CLI executable**.

## ✅ Features
- Extracts structured requirements from code comments
- Supports **configurable comment prefixes** (e.g., `//`, `#`, etc.)
- Works with **any file type**
- Outputs results as a **CSV file**
- Available as a **Gradle plugin** and **CLI tool**

---

## 📚 Requirement Format
Requirements must follow a structured format within comments.

### ✨ Example Requirement
```kotlin
fun exampleFunction() = println("Hello, world!")

// REQ-BEGIN: GS-A_1234, GS-A_5678
// | gemSpec_Example
// | This function prints a greeting.
fun greetUser() = println("Hello, User!")
// REQ-END: GS-A_1234, GS-A_5678
```

### ✅ Breakdown:
| Part | Meaning |
|------|---------|
| `REQ-BEGIN: GS-A_1234, GS-A_5678` | Start of requirement annotations, listing requirement IDs |
| `| gemSpec_Example` | Specification name |
| `| This function prints a greeting.` | Description of how the code fulfills the requirement |
| `REQ-END: GS-A_1234, GS-A_5678` | End of requirement block |

---

# 🛠️ Using the Requirement Extractor

## 💻 CLI Executable

### 🔍 Available Options:
```sh
java -jar requirement-extractor.jar --scan-dir <directory> --comment-prefix <prefix> --output <output.csv>
```

| Parameter | Description | Default |
|-----------|-------------|---------|
| `--scan-dir` | Directory to scan | `.` (Project root) |
| `--comment-prefix` | Comment prefix (e.g., `//`, `#`) | `//` |
| `--output` | Output CSV file | `requirements.csv` |

### 📝 Example Usage
```sh
# Scan entire project with default settings
java -jar requirement-extractor.jar

# Scan "src" directory for Python comments (`#`)
java -jar requirement-extractor.jar --scan-dir src --comment-prefix "#" --output requirements.csv
```

---

## 🌱 Gradle Plugin

### 🗂 Apply Plugin
Add the plugin to your `build.gradle.kts`:
```kotlin
plugins {
    id("de.gematik.openhealth.requirements") version "1.0.0"
}
```

### 🔧 Configure in `gradle.properties`
```properties
requirementScanDir=.
requirementCommentPrefix=//
requirementOutputFile=requirements.csv
```

### 🌟 Run Extraction
```sh
./gradlew extractRequirements
```

This generates a CSV file (`requirements.csv`) in the project root.

---

## 📂 CSV Output Format
| Requirement ID | Specification | Description | File Path | Start Line | End Line |
|---------------|--------------|-------------|-----------|------------|----------|
| GS-A_1234 | gemSpec_Example | This function prints a greeting. | /path/to/file.kt | 5 | 8 |

---

## 🚀 Contributing
Feel free to submit pull requests or issues!

---

## 🔗 License
TODO: Add license information
```
