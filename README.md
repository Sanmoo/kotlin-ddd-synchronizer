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
  "id": "abc",
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
  "id": "123",
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

## Creating Events in Legacy Database

```sql
INSERT INTO outbox (id, event_body, created_at)
VALUES (
    '123421',
    '{"data": {id": "123", "name": "A"}, "id": " || uuid_generate_v4() || ", "type": "resource.a.created.downstream"}',
    '2023-06-01T00:00:00.000Z'
);
```
