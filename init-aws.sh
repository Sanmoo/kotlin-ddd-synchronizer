#!/bin/sh

# Create Kinesis streams
echo "Creating Kinesis streams..."
awslocal kinesis describe-stream --stream-name resource-A-event-stream || awslocal kinesis create-stream --stream-name resource-A-event-stream --shard-count 1
awslocal kinesis describe-stream --stream-name resource-B-event-stream || awslocal kinesis create-stream --stream-name resource-B-event-stream --shard-count 1

# Put some data into the Kinesis streams
echo "Put some json data into the Kinesis streams..."
awslocal kinesis put-record --stream-name resource-A-event-stream --partition-key "1" --data '{"event": "resourceAEvent"}'
awslocal kinesis put-record --stream-name resource-B-event-stream --partition-key "1" --data '{"event": "resourceBEvent"}'

echo "Kinesis streams created successfully!"
echo "Available streams:"
awslocal kinesis list-streams
