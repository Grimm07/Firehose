provider "aws" {
  region = "us-east-1"  # Change this to your preferred region
}

# IAM role for Lambda execution
resource "aws_iam_role" "lambda_execution_role" {
  name = "lambda_firehose_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

# IAM Policy to allow Lambda to write to Firehose and OpenSearch
resource "aws_iam_role_policy" "lambda_policy" {
  role = aws_iam_role.lambda_execution_role.name

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = [
          "firehose:PutRecord",
          "firehose:PutRecordBatch",
          "es:ESHttpPost",
          "es:ESHttpPut"
        ],
        Effect = "Allow",
        Resource = "*"
      }
    ]
  })
}

# Create the Lambda function for data transformation
resource "aws_lambda_function" "firehose_transformation" {
  filename         = "build/libs/lambda-firehose-transformation.jar" # Path to your compiled JAR
  function_name    = "firehose_transformation"
  role             = aws_iam_role.lambda_execution_role.arn
  handler          = "com.example.Handler::handleRequest"  # Modify with actual handler
  runtime          = "java11"

  environment {
    variables = {
      OPENSEARCH_ENDPOINT = "your-opensearch-endpoint" # Set your OpenSearch endpoint
    }
  }
}

# Create the Firehose Delivery Stream to OpenSearch with Lambda function as transformer
resource "aws_kinesis_firehose_delivery_stream" "example" {
  name = "example-delivery-stream"

  destination = "elasticsearch"

  elasticsearch_configuration {
    domain_arn             = "arn:aws:es:us-east-1:123456789012:domain/your-opensearch-domain"
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

  lambda_function {
    role_arn        = aws_iam_role.lambda_execution_role.arn
    function_arn    = aws_lambda_function.firehose_transformation.arn
  }
}

# CloudWatch Log Group for Project (can be different for each project)
resource "aws_cloudwatch_log_group" "project_log_group" {
  name = "/aws/lambda/project-logs"  # Modify to match the log group for each project
}

# CloudWatch Log Subscription Filter to forward logs to Kinesis Firehose
resource "aws_cloudwatch_log_subscription_filter" "project_log_subscription" {
  name                = "project-log-filter"
  log_group_name      = aws_cloudwatch_log_group.project_log_group.name
  filter_pattern      = ""  # Can add a filter pattern if needed
  destination_arn     = aws_kinesis_firehose_delivery_stream.example.arn
  distribution        = "ByLogStream"
}

