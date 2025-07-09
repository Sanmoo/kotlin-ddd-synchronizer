#!/bin/sh

# Create Kinesis streams
echo "Creating Kinesis streams..."
awslocal kinesis describe-stream --stream-name resource-A-event-stream || awslocal kinesis create-stream --stream-name resource-A-event-stream --shard-count 1
awslocal kinesis describe-stream --stream-name resource-B-event-stream || awslocal kinesis create-stream --stream-name resource-B-event-stream --shard-count 1

echo "Kinesis streams created successfully!"
echo "Available streams:"
awslocal kinesis list-streams
