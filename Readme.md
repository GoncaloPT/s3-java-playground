# AWS S3 Connection Demo (Java)

This is a minimal, complete Maven project demonstrating how to connect to an AWS S3 bucket using the **AWS SDK for Java v2**.

The program connects using **static credentials** (an Access Key ID and a Secret Access Key) and performs a simple read operation: **listing all available S3 buckets** to verify the connection is working correctly.

## 🚨 Security Warning: Do Not Use in Production!

This project intentionally hardcodes AWS credentials in the `.java` file for demonstration purposes only. This is a **major security risk** and should **NEVER** be done in production code.

```java
private static final String AWS_ACCESS_KEY = "YOUR_AWS_ACCESS_KEY_ID";
private static final String AWS_SECRET_KEY = "YOUR_AWS_SECRET_ACCESS_KEY";
```

### For Production: Use Environment Variables or IAM Roles

- **AWS Lambda**: Use IAM roles attached to the Lambda function
- **EC2 instances**: Use IAM instance profiles
- **Local development**: Use environment variables via `~/.aws/credentials` or set `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`
- **Applications**: Use AWS credential provider chain (default behavior)

---

## Prerequisites

Before running this project, ensure you have:

- **Java 25** or higher (as specified in `pom.xml`)
- **Maven 3.6.0** or higher
- **AWS Account** with S3 access
- **AWS Access Key ID** and **Secret Access Key** (generated in AWS IAM console)

### Check Your Environment

```bash
# Verify Java version
java -version

# Verify Maven version
mvn -version
```

---

## Project Structure

```
.
├── pom.xml                           # Maven configuration (dependencies, build plugins)
├── Readme.md                         # This file
└── src/
    └── main/
        ├── java/
        │   └── S3ConnectionDemo.java # Main application code
        └── resources/
            └── logback.xml           # Logging configuration
```

### Key Dependencies

- **AWS SDK for Java v2** (v2.26.12)
  - `software.amazon.awssdk:s3` - S3 client library
  - `software.amazon.awssdk:bom` - Bill of Materials for consistent versioning

- **Logging**
  - `org.slf4j:slf4j-api` (v2.0.11) - SLF4J logging facade
  - `ch.qos.logback:logback-classic` (v1.5.0) - Logback implementation for SLF4J

### Build Configuration

- **Java Source/Target**: Java 25
- **Build Plugin**: `exec-maven-plugin` (for easy execution)
- **Main Class**: `pt.goncalo.S3ConnectionDemo`

### Logging Configuration

Logging is configured via `src/main/resources/logback.xml`:
- **Console output**: Formatted logs with timestamp, thread, level, logger name, and message
- **Root logger**: INFO level by default
- **AWS SDK logger**: Set to WARN to reduce noise
- **Application logger**: Set to DEBUG for detailed application logs

You can modify the log levels in `logback.xml` to control verbosity.

---

## Setup Instructions

### Step 1: Clone or Download the Project

```bash
cd /path/to/s3-java-playground
```

### Step 2: Get Your AWS Credentials

1. Log in to your [AWS Management Console](https://console.aws.amazon.com)
2. Navigate to **IAM** → **Users** → Select your user
3. Go to the **Security credentials** tab
4. Click **Create access key**
5. Copy your **Access Key ID** and **Secret Access Key**

### Step 3: Configure Credentials in the Code

Open `src/main/java/S3ConnectionDemo.java` and replace:

```java
private static final String AWS_ACCESS_KEY = "YOUR_AWS_ACCESS_KEY_ID";
private static final String AWS_SECRET_KEY = "YOUR_AWS_SECRET_ACCESS_KEY";
private static final Region AWS_REGION = Region.US_EAST_1; // Change if needed
```

With your actual credentials and desired region.

**Example:**
```java
private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
private static final Region AWS_REGION = Region.US_WEST_2;
```

⚠️ **Important**: Never commit credentials to version control!

### Step 4: Build the Project

```bash
mvn clean install
```

This command:
- Cleans previous builds
- Downloads dependencies (including SLF4J and Logback)
- Compiles the source code
- Packages the application

---

## Running the Application

### Option 1: Using Maven Exec Plugin (Recommended)

```bash
mvn exec:java
```

### Option 2: Compile and Run Manually

```bash
# Compile
mvn compile

# Run
mvn exec:java
```

### Option 3: Run the Compiled Class Directly

```bash
# Compile
mvn compile

# Run (requires all dependencies in classpath)
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" pt.goncalo.S3ConnectionDemo
```

### Logging Output

The application uses **SLF4J with Logback** for logging. All logs will be printed to the console with the following format:

```
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - Successfully created S3 client.
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - Attempting to list buckets...
```

To adjust log verbosity, edit `src/main/resources/logback.xml` and change the log levels:
- `DEBUG`: Very detailed logging
- `INFO`: General informational messages (default)
- `WARN`: Warning messages only
- `ERROR`: Errors only

---

## Expected Output

If everything is configured correctly, you should see:

```
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - Successfully created S3 client.
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - Attempting to list buckets...
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - 
Your S3 Buckets:
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo -   - my-bucket-1  
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo -   - my-bucket-2  
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo -   - backup-bucket  
```

### If No Buckets Are Found

```
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - Successfully created S3 client.
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - Attempting to list buckets...
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo - 
Your S3 Buckets:
2025-10-30 14:23:45 [main] INFO  pt.goncalo.S3ConnectionDemo -   (No buckets found or no permission to list)
```

This could mean:
- Your AWS account has no S3 buckets
- Your IAM user lacks `s3:ListAllMyBuckets` permission

### If an Error Occurs

```
2025-10-30 14:23:45 [main] ERROR pt.goncalo.S3ConnectionDemo - Error communicating with S3:
2025-10-30 14:23:45 [main] ERROR pt.goncalo.S3ConnectionDemo - Error Code:    InvalidAccessKeyId
2025-10-30 14:23:45 [main] ERROR pt.goncalo.S3ConnectionDemo - Error Message: The AWS Access Key Id provided does not exist in our records.
2025-10-30 14:23:45 [main] ERROR pt.goncalo.S3ConnectionDemo - 
Check your credentials, region, and permissions.
```

Common issues:
- **InvalidAccessKeyId**: Wrong Access Key ID
- **SignatureDoesNotMatch**: Wrong Secret Access Key
- **AccessDenied**: User lacks required S3 permissions
- **RegionNotFound**: Invalid AWS region specified

---

## Code Explanation

The program performs the following steps:

1. **Initializes Logger**: Creates a SLF4J logger for the application
2. **Creates AWS Credentials**: Uses `AwsBasicCredentials` with your access key and secret key
3. **Initializes S3 Client**: Builds an S3 client with the specified region
4. **Lists Buckets**: Calls `listBuckets()` to retrieve all S3 buckets in the account
5. **Displays Results**: Uses logger to print bucket names or error messages
6. **Handles Exceptions**: Catches and logs S3-specific and general exceptions

Key classes:
- `AwsBasicCredentials`: Represents AWS access credentials
- `StaticCredentialsProvider`: Provides credentials to the S3 client
- `S3Client`: Main client for interacting with AWS S3
- `ListBucketsResponse`: Response object containing bucket list
- `S3Exception`: S3-specific error handling
- `Logger` (SLF4J): For application logging

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `Cannot find symbol: class S3Client` | Run `mvn clean install` to download dependencies |
| `Logs not printing` | Ensure `logback.xml` exists in `src/main/resources/` and Logback dependency is in pom.xml |
| `AccessDenied` error for S3 operations | **Your IAM user lacks S3 permissions.** See `S3_PERMISSIONS_GUIDE.md` for detailed setup instructions |
| `Maven not found` | Ensure Maven is installed and added to PATH. Run `mvn -version` |
| `InvalidAccessKeyId error` | Verify your Access Key ID is correct |
| `SignatureDoesNotMatch error` | Verify your Secret Access Key is correct |
| `AuthorizationHeaderMalformed error` | Check credentials are correctly pasted (no extra spaces/newlines) |
| No buckets found | Create an S3 bucket or check IAM permissions (see `S3_PERMISSIONS_GUIDE.md`) |
| Java compilation error about module/records | Ensure Java 25 is installed: `java -version` |

---

## Quick Start Summary

```bash
# 1. Navigate to project
cd s3-java-playground

# 2. Edit credentials in S3ConnectionDemo.java
nano src/main/java/S3ConnectionDemo.java

# 3. Build and run
mvn clean install
mvn exec:java
```

---

## Documentation Files

- **`S3_PERMISSIONS_GUIDE.md`** - Comprehensive guide for fixing S3 permission issues (READ THIS if you get AccessDenied errors)
- **`LOGGING_REFACTOR.md`** - Summary of SLF4J and Logback logging setup

---

## Additional Resources

- [AWS SDK for Java v2 Documentation](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [AWS S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/)
- [SLF4J Documentation](https://www.slf4j.org/)
- [Logback Documentation](https://logback.qos.ch/)
- [Maven Official Documentation](https://maven.apache.org/guides/)
- [IAM Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)

---

## License

This project is provided as-is for educational purposes.

