# ddd-synchronizer

This is a Spring Boot application whose purpose is to help in a modernization project, by synchronizing data between
two Bounded Contexts.

TODO: Add more details

# Kinds of Messages

This application expects to receive two main type of messages: Events and Commands. Events are messages that
represent domain events that happened in either Bounded Context.
Commands integration actions that are intended to be executed by the integration context.

# Development

## Simulating a message that corresponds to an event communication `resource.a.created`

```bash
awslocal sqs send-message \
  --queue-url http://localhost:4566/000000000000/synchronizer-messages-queue.fifo \
  --message-group-id "resource-a-123" \
  --message-deduplication-id "$(date +%s)" \
  --message-body \
  <message-body-here>
```

### Event A Created Upstream Example Message
```json
{
  "type": "event",
  "origination": "UpstreamBoundedContext",
  "eventId": "abc",
  "aggregateId": "123",
  "createdAt": "2023-06-01T00:00:00.000Z",
  "event": {
    "type": "resource.a.created.upstream",
    "data": {
      "id": "123",
      "name": "A"
    }
  },
  "command": {}
}
```

### Event A Created Downstream Example Message
```json
{
"todo": "todo"
}
```

### Command Create Resource A Downstream Example Message
```json
{
  "type": "command",
  "commandId": "123",
  "aggregateId": "123",
  "createdAt": "2023-06-01T00:00:00.000Z",
  "event": {},
  "command": {
    "type": "create.resource.a.downstream",
    "data": {
      "id": "123",
      "name": "A"
    }
  }
}
```

If you need to purge the queue

```bash
awslocal sqs purge-queue --queue-url http://localhost:4566/000000000000/synchronizer-messages-queue.fifo
```

Important observations: As per the concept of Groups in SQS Fifo queues, you must have one group id per independent 
integration flow. In this example application, every domain aggregate has its own group id. The consequence is that 
events and commands from the same aggregate will be processed in strict order (it does not make sense to process them
in a distinct order compared to the order in which they were produced). The group id in the example above is a concatenation
between the aggregate type and the aggregate id (resource-a-123).

