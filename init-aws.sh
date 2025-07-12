#!/bin/sh

# Create Kinesis streams
echo "Creating Kinesis streams..."
awslocal kinesis describe-stream --stream-name resource-A-event-stream || awslocal kinesis create-stream --stream-name resource-A-event-stream --shard-count 1
awslocal kinesis describe-stream --stream-name resource-B-event-stream || awslocal kinesis create-stream --stream-name resource-B-event-stream --shard-count 1

# The following commands can be used inside the container to simulate events
# awslocal kinesis put-record --stream-name resource-A-event-stream --partition-key "1" --data '{"event": "resourceAEvent"}'
# awslocal kinesis put-record --stream-name resource-B-event-stream --partition-key "1" --data '{"event": "resourceBEvent"}'

echo "Kinesis streams created successfully!"
echo "Available streams:"
awslocal kinesis list-streams

# Create SQS fifo queues (for aggregate related commands)
awslocal sqs get-queue-url --queue-name aggregate-a-commands-queue.fifo || awslocal sqs create-queue --queue-name aggregate-a-commands-queue.fifo --attributes "FifoQueue=true"
awslocal sqs get-queue-url --queue-name aggregate-b-commands-queue.fifo || awslocal sqs create-queue --queue-name aggregate-b-commands-queue.fifo --attributes "FifoQueue=true"

echo "SQS queues created successfully!"
echo "Available queues:"
awslocal sqs list-queues
