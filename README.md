# OpenHealth Requirements

## Overview

OpenHealth Requirements scans source files for structured requirement annotations and extracts them into a structured
CSV file. It is available as both a **Gradle plugin** and a **standalone CLI executable**.

## Features

- Extracts structured requirements from code comments
- Supports **configurable comment prefixes** (e.g., `//`, `#`, etc.)
- Works with **any file type**
- Outputs results as a **CSV file**
- Available as a **Gradle plugin** and **CLI tool**

## Requirement Format

Requirements must follow a structured format within comments.

### Example Requirement

```kotlin
fun exampleFunction() = println("Hello, world!")

// REQ-BEGIN: A_1234, A_5678
// | example_spec
// | This function prints a greeting.
fun greetUser() = println("Hello, User!")
// REQ-END: A_1234, A_5678
```

### Breakdown:

| Part                               | Meaning                                                   |
|------------------------------------|-----------------------------------------------------------|
| `REQ-BEGIN: A_1234, A_5678`        | Start of requirement annotations, listing requirement IDs |
| `example_spec`                     | Specification name                                        |
| `This function prints a greeting.` | Description of how the code fulfills the requirement      |
| `REQ-END: A_1234, A_5678`          | End of requirement block                                  |

# Using the Requirement Extractor

## CLI Executable

### Available Options:

```sh
java -jar cli.jar --base-path <base-path> --comment-prefix <prefix> --output <output.csv> <file-paths>...
```

| Parameter          | Description                      | Default            |
|--------------------|----------------------------------|--------------------|
| `--base-path`      | Base path for file paths         | `/`                |
| `--comment-prefix` | Comment prefix (e.g., `//`, `#`) | `//`               |
| `--output`         | Output CSV file                  | `requirements.csv` |
| `<file-paths>`     | File paths to scan               | (required)         |

### Example Usage

```sh
# Scan specific files with default settings
java -jar cli.jar /path/to/file1.kt /path/to/file2.kt

# Scan specific files for Python comments (`#`)
java -jar cli.jar --comment-prefix "#" --output requirements.csv /path/to/file1.py /path/to/file2.py
```

## Gradle Plugin

### Apply Plugin

Add the plugin to your `build.gradle.kts`:

```kotlin
plugins {
    id("de.gematik.openhealth.requirements") version "1.0.0"
}
```

### Run Extraction

```sh
./gradlew extractRequirements
```

This generates a CSV file (`requirements.csv`) in the project root.

## CSV Output Format

| Requirement ID | Specification | Description                      | File Path        | Start Line | End Line |
|----------------|---------------|----------------------------------|------------------|------------|----------|
| A_1234         | example_spec  | This function prints a greeting. | /path/to/file.kt | 5          | 8        |

## Contributing

Feel free to submit pull requests or issues!

## License

Copyright 2025 gematik GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.

See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.

Unless required by applicable law the software is provided "as is" without warranty of any kind, either express or implied, including, but not limited to, the warranties of fitness for a particular purpose, merchantability, and/or non-infringement. The authors or copyright holders shall not be liable in any manner whatsoever for any damages or other claims arising from, out of or in connection with the software or the use or other dealings with the software, whether in an action of contract, tort, or otherwise.

The software is the result of research and development activities, therefore not necessarily quality assured and without the character of a liable product. For this reason, gematik does not provide any support or other user assistance (unless otherwise stated in individual cases and without justification of a legal obligation). Furthermore, there is no claim to further development and adaptation of the results to a more current state of the art.

Gematik may remove published results temporarily or permanently from the place of publication at any time without prior notice or justification.