#!/bin/sh

# Create SQS fifo queues (for aggregate related commands)
awslocal sqs get-queue-url --queue-name synchronizer-messages-queue.fifo || awslocal sqs create-queue \
--queue-name synchronizer-messages-queue.fifo --attributes "FifoQueue=true"

echo "SQS queues created successfully!"
echo "Available queues:"
awslocal sqs list-queues
