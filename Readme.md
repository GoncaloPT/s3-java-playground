# AWS S3 Connection Demo (Java)

This is a minimal, complete Maven project demonstrating how to connect to an AWS S3 bucket using the **AWS SDK for Java v2**.

The program connects using **credentials from environment variables** (loaded from a `.env` file) and performs:
1. **List all available S3 buckets** to verify the connection
2. **Upload a dummy object** to a specified bucket


## 🚨 Security Notice

This project uses environment variables loaded from a `.env` file instead of hardcoding credentials. The `.env` file is added to `.gitignore` and will **never be committed** to version control.

### Credentials are loaded from `.env`:
```
AWS_ACCESS_KEY_ID=your_key_here
AWS_SECRET_ACCESS_KEY=your_secret_here
AWS_REGION=eu-central-1
S3_BUCKET_NAME=your_bucket_name
```

### For Production: Use IAM Roles or Credential Chains

- **AWS Lambda**: Use IAM roles attached to the Lambda function
- **EC2 instances**: Use IAM instance profiles
- **Local development**: Use `~/.aws/credentials` file or environment variables
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
├── .env.example                      # Template for environment variables (commit to Git)
├── .env                              # Actual credentials (NEVER commit - in .gitignore)
├── .gitignore                        # Excludes .env and other sensitive files
├── Readme.md                         # This file
└── src/
    └── main/
        ├── java/
        │   └── pt/goncalo/
        │       └── S3ConnectionDemo.java # Main application code
        └── resources/
            └── logback.xml           # Logging configuration
```

### Build Configuration

- **Java Source/Target**: Java 25
- **Build Plugin**: `exec-maven-plugin` (for easy execution)
- **Main Class**: `pt.goncalo.S3ConnectionDemo`
- **Package**: `pt.goncalo`

## Setup Instructions

### Step 1: Clone or Download the Project

```bash
cd /path/to/s3-java-playground
```

### Step 2: Create `.env` File from Template

```bash
cp .env.example .env
```

### Step 3: Get Your AWS Credentials

1. Log in to your [AWS Management Console](https://console.aws.amazon.com)
2. Navigate to **IAM** → **Users** → Select your user
3. Go to the **Security credentials** tab
4. Click **Create access key**
5. Copy your **Access Key ID** and **Secret Access Key**

### Step 4: Configure the `.env` File

Edit `.env` and replace the placeholder values:

```bash
AWS_ACCESS_KEY_ID=REPLACE_ME
AWS_SECRET_ACCESS_KEY=REPLACE_ME
AWS_REGION=REPLACE_ME
S3_BUCKET_NAME=my-bucket-name
```

⚠️ **Important**: 
- The `.env` file is listed in `.gitignore` and will never be committed to Git
- Never share your `.env` file or commit it to version control
- Rotate credentials regularly for security

### Step 5: Build the Project

```bash
mvn clean install
```

This command:
- Cleans previous builds
- Downloads dependencies (including dotenv-java)
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

### Output

The application will:
1. Load credentials from `.env` file
2. Create an S3 client
3. List all your S3 buckets
4. Upload a dummy object to the specified bucket

Example output:
```
Successfully created S3 client.
AWS Region: eu-central-1
Attempting to list buckets...

📦 Your S3 Buckets:
   ✓ my-bucket-1
   ✓ my-bucket-2
   ✓ backup-bucket

✅ SUCCESS! Found 3 bucket(s)

Attempting to upload dummy object to bucket: my-bucket-1
✅ Object uploaded successfully!
   Bucket: my-bucket-1
   Key: demo/dummy-object-1761865713382.txt
   ETag: "6f4e3d4a0e497f468dd5dd7ab51f5152"
```

---

## Code Structure

The application is organized into separate methods for clarity:

### `main()`
- Loads credentials from `.env` file
- Validates credentials
- Creates S3 client
- Calls `listBuckets()` and `uploadDummyObject()` methods

### `listBuckets(S3Client s3Client)`
- Lists all S3 buckets in the AWS account
- Displays bucket names with checkmarks
- Shows total bucket count

### `uploadDummyObject(S3Client s3Client, String bucketName)`
- Creates a timestamped dummy text object
- Uploads it to the specified bucket
- Returns the object key and ETag

---

## Quick Start Summary

```bash
# 1. Set up environment
cp .env.example .env
nano .env  # Add your credentials

# 2. Build
mvn clean install

# 3. Run
mvn exec:java
```

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

