# Setup Guide - net-bullet

## Prerequisites

### ⚠️ MANDATORY: Java 25

This project **STRICTLY REQUIRES Java 25**. No exceptions.

- **Minimum Version:** Java 25 (Early Access builds accepted)
- **Recommended Distribution:** Eclipse Temurin (OpenJDK)
- **Build Tool:** Maven 3.9.9 or higher

### Why Java 25?

This project is built with modern Java 25 features and enforces strict version compliance via maven-enforcer-plugin. Using any earlier version will result in build failure.

**Note:** As of December 2024, Java 25 is available as Early Access (EA) builds. The project is configured to work with EA releases until the GA (General Availability) version is released.

## Installation

### Option 1: SDKMAN! (Recommended for Linux/Mac)

```bash
# Install SDKMAN! if not already installed
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 25 (Early Access)
sdk install java 25.ea-tem

# Verify installation
java -version
# Should show: openjdk version "25-ea"...
```

The repository includes a `.sdkmanrc` file that will automatically switch to Java 25 when you `cd` into the project directory (with `sdk env`).

### Option 2: Manual Installation

#### Windows
1. Download Java 25 EA from [Adoptium](https://adoptium.net/temurin/releases/?version=25) (select Early Access)
2. Install and set `JAVA_HOME` environment variable
3. Add `%JAVA_HOME%\bin` to PATH
4. Verify: `java -version`

#### macOS (Homebrew)
```bash
# Install Java 25 EA
brew install --cask temurin25

# Verify and set JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home
java -version
```

#### Linux (Debian/Ubuntu)
```bash
# Add Adoptium repository
wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -
echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list

# Install Java 25
sudo apt update
sudo apt install temurin-25-jdk

# Set as default
sudo update-alternatives --config java
```

### Maven

```bash
# Install Maven 3.9.9+
# On macOS
brew install maven

# On Linux
sudo apt install maven

# Verify
mvn -version
# Should show: Apache Maven 3.9.9 or higher
# AND Java version: 25
```

## Verification

After installation, verify your environment:

```bash
# Check Java version
java -version
# Expected: openjdk version "25"...

# Check Maven version
mvn -version
# Expected: 
#   Apache Maven 3.9.9+
#   Java version: 25

# Test build (in project directory)
mvn clean compile
# Should succeed with "BUILD SUCCESS"
```

## Quick Start

```bash
# Clone the repository
git clone https://github.com/TheStackTraceWhisperer/net-bullet.git
cd net-bullet

# If using SDKMAN, activate environment
sdk env

# Build and run all tests
mvn clean verify

# Run only unit tests
mvn test

# Run only integration tests
mvn integration-test
```

## Troubleshooting

### "BUILD FAILURE: STRICT COMPLIANCE: Java 25 is mandatory"

**Cause:** You are not running Java 25.

**Solution:**
1. Check your Java version: `java -version`
2. If not Java 25, install it using one of the methods above
3. Ensure `JAVA_HOME` points to Java 25
4. Restart your terminal/IDE

### Maven picks wrong Java version

```bash
# Set JAVA_HOME explicitly
export JAVA_HOME=/path/to/java-25

# Or use Maven's toolchains.xml
mvn -version  # Verify it shows Java 25
```

### IDE not using Java 25

**IntelliJ IDEA:**
1. File → Project Structure → Project
2. Set "SDK" to Java 25
3. Set "Language level" to 25

**Eclipse:**
1. Project → Properties → Java Build Path
2. Libraries → Add Library → JRE System Library
3. Select Java 25

**VS Code:**
1. Install "Extension Pack for Java"
2. Set `java.configuration.runtimes` in settings.json:
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-25",
      "path": "/path/to/jdk-25",
      "default": true
    }
  ]
}
```

## Docker Development (Alternative)

If you cannot install Java 25 locally, use Docker:

```bash
# Build in Docker
docker run --rm -v "$(pwd)":/project -w /project maven:3.9-eclipse-temurin-25 mvn clean verify

# Interactive shell
docker run -it --rm -v "$(pwd)":/project -w /project maven:3.9-eclipse-temurin-25 bash
```

## Next Steps

- Read [README.md](../README.md) for build commands and quality tools
- Check [Phase 1 Spec](specs/phase-1-network-kernel.md) for architecture details
- Review [ADR 001](adr/001-no-di-frameworks.md) for design decisions
- See [Copilot Instructions](../.github/copilot-instructions.md) for coding standards

## Getting Help

If you encounter issues:
1. Verify Java 25 is installed: `java -version`
2. Check Maven version: `mvn -version`
3. Review build logs carefully
4. Open an issue with full environment details
