package pt.goncalo;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;

/**
 * A Java program to connect to AWS S3 using credentials from environment variables.
 * <p>
 * Credentials are loaded from:
 * 1. .env file in the project root (uses dotenv-java library)
 * 2. Environment variables
 * <p>
 * WARNING: Hardcoding credentials is a major security risk.
 * This approach using environment variables is MUCH safer!
 */
public class S3ConnectionDemo {
    private static final Logger log = LoggerFactory.getLogger(S3ConnectionDemo.class);

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Get credentials from environment variables
        String awsAccessKey = dotenv.get("AWS_ACCESS_KEY_ID");
        String awsSecretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
        String awsRegion = dotenv.get("AWS_REGION", "US_EAST_1");
        String bucketName = dotenv.get("S3_BUCKET_NAME");

        // Validate credentials
        if (awsAccessKey == null || awsAccessKey.isBlank()) {
            log.error("❌ AWS_ACCESS_KEY_ID not set in .env file or environment");
            return;
        }
        if (awsSecretKey == null || awsSecretKey.isBlank()) {
            log.error("❌ AWS_SECRET_ACCESS_KEY not set in .env file or environment");
            return;
        }

        // Convert region string to Region enum
        Region region;
        try {
            region = Region.of(awsRegion.toLowerCase());
        } catch (Exception _) {
            log.error("❌ Invalid AWS_REGION: {}. Using US_EAST_1", awsRegion);
            region = Region.US_EAST_1;
        }

        // 1. Create the credentials object from environment variables
        var credentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        var credentialsProvider = StaticCredentialsProvider.create(credentials);

        // 2. Build the S3 client
        try (S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build()) {

            log.info("Successfully created S3 client.");
            log.info("AWS Region: {}", region);

            // 3. List buckets
            listBuckets(s3Client);

            // 4. Upload dummy object to bucket
            if (bucketName != null && !bucketName.isBlank()) {
                uploadDummyObject(s3Client, bucketName);
            } else {
                log.warn("⚠️  S3_BUCKET_NAME not set in .env file. Skipping upload.");
            }

        } catch (S3Exception e) {
            String errorCode = e.awsErrorDetails().errorCode();
            String errorMessage = e.awsErrorDetails().errorMessage();

            log.error("❌ Error communicating with S3:");
            log.error("   Error Code:    {}", errorCode);
            log.error("   Error Message: {}", errorMessage);
            log.error("");

            // Provide helpful diagnostics based on error type
            if ("AccessDenied".equals(errorCode)) {
                log.error("⚠️  PERMISSION ISSUE DETECTED");
                log.error("   Your IAM user does not have the required S3 permissions.");
                log.error("   Missing Permission: s3:ListAllMyBuckets");
                log.error("");
                log.error("   📋 Quick Fix:");
                log.error("   1. Go to AWS IAM Console → Users → service.qmm-s3-dev");
                log.error("   2. Click 'Add permissions' → 'Attach policies directly'");
                log.error("   3. Search for and attach: AmazonS3FullAccess (for testing)");
                log.error("   4. Run this app again");
                log.error("");
                log.error("   📖 For detailed guidance, see: S3_PERMISSIONS_GUIDE.md");
            } else if ("InvalidAccessKeyId".equals(errorCode)) {
                log.error("⚠️  CREDENTIAL ISSUE - Invalid Access Key ID");
                log.error("   The AWS Access Key ID does not exist.");
                log.error("   Check that AWS_ACCESS_KEY_ID is correct in .env file");
            } else if ("SignatureDoesNotMatch".equals(errorCode)) {
                log.error("⚠️  CREDENTIAL ISSUE - Signature Does Not Match");
                log.error("   The AWS Secret Key is incorrect.");
                log.error("   Check that AWS_SECRET_ACCESS_KEY is correct in .env file");
            } else if ("InvalidBucketName".equals(errorCode)) {
                log.error("⚠️  BUCKET ISSUE - Invalid Bucket Name");
                log.error("   The bucket name is invalid or doesn't exist.");
            } else {
                log.error("⚠️  Check your credentials, region, and permissions.");
            }
        } catch (Exception e) {
            log.error("❌ An unexpected error occurred:", e);
        }
    }

    /**
     * Lists all S3 buckets in the AWS account.
     */
    private static void listBuckets(S3Client s3Client) {
        try {
            log.info("Attempting to list buckets...");
            log.info("");

            var response = s3Client.listBuckets();

            log.info("\n📦 Your S3 Buckets:");
            if (response.buckets().isEmpty()) {
                log.info("   (No buckets found in this account)");
            } else {
                for (Bucket bucket : response.buckets()) {
                    log.info("   ✓ {}", bucket.name());
                }
                log.info("");
                log.info("✅ SUCCESS! Found {} bucket(s)", response.buckets().size());
            }
        } catch (S3Exception e) {
            log.error("❌ Error listing buckets: {}", e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    /**
     * Uploads a dummy text object to the specified S3 bucket.
     *
     * @param s3Client  The S3 client
     * @param bucketName The name of the bucket
     */
    private static void uploadDummyObject(S3Client s3Client, String bucketName) {
        try {
            log.info("");
            log.info("Attempting to upload dummy object to bucket: {}", bucketName);

            var objectKey = "demo/dummy-object-" + System.currentTimeMillis() + ".txt";
            var objectContent = "This is a dummy object created at " + java.time.LocalDateTime.now();

            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            var putObjectResponse = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromString(objectContent)
            );

            log.info("✅ Object uploaded successfully!");
            log.info("   Bucket: {}", bucketName);
            log.info("   Key: {}", objectKey);
            log.info("   ETag: {}", putObjectResponse.eTag());

        } catch (S3Exception e) {
            var errorCode = e.awsErrorDetails().errorCode();
            var errorMessage = e.awsErrorDetails().errorMessage();

            log.error("❌ Error uploading object to S3:");
            log.error("   Error Code:    {}", errorCode);
            log.error("   Error Message: {}", errorMessage);
            log.error("");

            if ("AccessDenied".equals(errorCode)) {
                log.error("⚠️  PERMISSION ISSUE");
                log.error("   Your IAM user does not have permission to upload to this bucket.");
                log.error("   Required Permission: s3:PutObject");
            } else if ("NoSuchBucket".equals(errorCode)) {
                log.error("⚠️  BUCKET NOT FOUND");
                log.error("   The bucket '{}' does not exist.", bucketName);
                log.error("   Check S3_BUCKET_NAME in .env file.");
            } else {
                log.error("⚠️  Upload failed. Check your permissions and bucket name.");
            }
        } catch (Exception e) {
            log.error("❌ An unexpected error occurred during upload:", e);
        }
    }
}
