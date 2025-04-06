When creating **CloudWatch Log Subscription Filters** in AWS, the filter pattern defines the structure of the logs you're interested in capturing. The pattern can be used to filter logs based on specific keywords, log levels, or other custom log fields.

CloudWatch filter patterns are simple yet powerful tools to specify what types of logs should be streamed to a downstream service like **Kinesis Firehose**, **Lambda**, or **SNS**.

Here’s a **clean and detailed** example of how to write CloudWatch filter patterns.

---

### **CloudWatch Filter Pattern Syntax Overview**

CloudWatch logs support **filter patterns** that can match parts of a log message. You can:
- Match **specific words**.
- Match **log levels** like `ERROR`, `INFO`, `WARN`.
- Extract **specific fields** (using `?` or `*`).
- Apply **logical operators** such as `AND`, `OR`, and `NOT`.
- **Regular Expressions** can be used to match specific patterns in the logs.

---

### **Example 1: Basic Keyword Matching**

#### Scenario:
We want to match logs that contain the word `ERROR`.

```plaintext
ERROR
```

This pattern will match any log entry that contains the word `ERROR`. It is case-sensitive and looks for the exact word in the log message.

---

### **Example 2: Matching Multiple Words (OR condition)**

#### Scenario:
We want to match logs that contain either the word `ERROR` or `WARN`.

```plaintext
ERROR OR WARN
```

This pattern uses the `OR` operator to capture logs that contain either `ERROR` or `WARN`.

---

### **Example 3: Matching Log Level and Custom Fields**

#### Scenario:
We want to capture logs where the log level is `ERROR` and the user ID field is present and matches a pattern (`userId=123`).

```plaintext
ERROR ?userId=123
```

- `ERROR`: Matches logs with the `ERROR` level.
- `?userId=123`: Matches logs where `userId` is `123`.

The `?` operator checks if the field (`userId=123`) is present in the log entry, matching the specified value.

---

### **Example 4: Matching Specific Fields with Wildcards**

#### Scenario:
We want to capture logs that contain the word `ERROR` and where the field `requestId` has a value that starts with `req-`.

```plaintext
ERROR ?requestId=req-*
```

- `ERROR`: Matches the logs that have `ERROR`.
- `?requestId=req-*`: Matches logs where the `requestId` field starts with `req-`. The `*` wildcard matches any characters following `req-`.

---

### **Example 5: Excluding Logs (NOT condition)**

#### Scenario:
We want to match all logs that contain `ERROR` but exclude those with `DEBUG` level logs.

```plaintext
ERROR NOT DEBUG
```

This pattern captures logs with `ERROR` but excludes logs that contain the `DEBUG` level.

---

### **Example 6: Exact Phrase Matching**

#### Scenario:
We want to match logs that contain the exact phrase `"Unable to process request"`.

```plaintext
"Unable to process request"
```

This pattern will match logs that contain this exact phrase. It is important to enclose the phrase in double quotes to ensure the entire phrase is captured as a whole.

---

### **Example 7: Using Regular Expressions**

#### Scenario:
We want to match logs with any log level (e.g., `INFO`, `ERROR`, `WARN`) and that contain an alphanumeric `userId` pattern.

```plaintext
(?i)^(ERROR|INFO|WARN)\s+.*userId=\w+
```

- `(?i)`: Makes the pattern case-insensitive.
- `^(ERROR|INFO|WARN)`: Matches logs starting with either `ERROR`, `INFO`, or `WARN`.
- `\s+`: Matches any whitespace after the log level.
- `.*`: Matches the rest of the log message.
- `userId=\w+`: Matches a `userId` field that contains one or more word characters (alphanumeric).

This regular expression will match logs with one of the specified log levels (`ERROR`, `INFO`, or `WARN`) and a valid `userId` value.

---

### **Example 8: Complex Pattern with Multiple Conditions**

#### Scenario:
We want to capture logs with `ERROR` level, including logs that contain both `userId` and `requestId`, where `userId` is numeric and `requestId` follows the pattern `req-`.

```plaintext
ERROR ?userId=\d+ ?requestId=req-*
```

- `ERROR`: Matches logs with `ERROR`.
- `?userId=\d+`: Matches logs with `userId` being a numeric value (`\d+` matches one or more digits).
- `?requestId=req-*`: Matches logs with a `requestId` that starts with `req-`.

This pattern captures logs with `ERROR` level that include a numeric `userId` and a `requestId` starting with `req-`.

---

### **Example 9: Matching JSON Structure (Complex Structured Logs)**

#### Scenario:
If the logs are in JSON format, you can create a pattern to match specific JSON key-value pairs.

```plaintext
{ $.level = "ERROR" && $.message = "Failed to process" }
```

This pattern will match logs in JSON format where the `level` key has the value `"ERROR"` and the `message` key has the value `"Failed to process"`. This approach works well when logs are structured in JSON.

---

### **CloudWatch Log Subscription Filter Terraform Configuration**

To set up the **CloudWatch Log Subscription Filter** that forwards logs to **Kinesis Firehose**, here’s an example of how to define the subscription filter in Terraform.

#### **Terraform Configuration for CloudWatch Subscription Filter**

```hcl
resource "aws_cloudwatch_log_group" "example_log_group" {
  name = "example-log-group"
}

resource "aws_kinesis_firehose_delivery_stream" "example_firehose" {
  name = "example-firehose"
  destination = "elasticsearch"
  
  elasticsearch_configuration {
    domain_arn             = aws_opensearch_domain.example.arn
    role_arn               = aws_iam_role.lambda_execution_role.arn
    buffer_interval        = 300
    buffer_size           = 5
    s3_backup_mode        = "FailedDocumentsOnly"
    s3_backup_configuration {
      bucket_arn          = "arn:aws:s3:::your-backup-bucket"
      compression_format  = "UNCOMPRESSED"
      prefix              = "firehose-backups/"
    }
  }
}

resource "aws_cloudwatch_log_subscription_filter" "example_filter" {
  name            = "example-log-filter"
  log_group_name  = aws_cloudwatch_log_group.example_log_group.name
  filter_pattern  = "ERROR ?userId=123"  # Example filter pattern
  destination_arn = aws_kinesis_firehose_delivery_stream.example_firehose.arn
  role_arn        = aws_iam_role.lambda_execution_role.arn
}
```

- **`aws_cloudwatch_log_group`**: Creates a CloudWatch log group for the logs to be captured from your applications or services.
- **`aws_kinesis_firehose_delivery_stream`**: Creates a Firehose stream that will forward logs to OpenSearch.
- **`aws_cloudwatch_log_subscription_filter`**: Configures a subscription filter to forward logs matching the filter pattern to Kinesis Firehose.

---

### Conclusion

In this example, we’ve:
1. **Defined detailed filter patterns** that cover a wide range of use cases (basic keyword matching, structured logs, regular expressions, and JSON-based filters).
2. **Set up a Terraform configuration** for CloudWatch log subscription filters that will capture logs and forward them to Kinesis Firehose for transformation and ingestion into OpenSearch.

You can modify the `filter_pattern` field to match the specific logs that are relevant to your use case. The filter patterns can be as simple as matching `ERROR` logs or as complex as using regular expressions to capture logs with specific fields.