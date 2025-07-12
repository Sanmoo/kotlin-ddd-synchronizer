# Development

## Simulating event `resource.a.created`
```bash
awslocal kinesis put-record \
  --stream-name resource-A-event-stream \
  --partition-key "1" --data \
  '{
      "type": "resource.a.created",
      "createdFromSystem": "OTHER_SYSTEM",
      "eventId": "abc",
      "id": "12",
      "createdAt": "2023-06-01T00:00:00"
   }'
```