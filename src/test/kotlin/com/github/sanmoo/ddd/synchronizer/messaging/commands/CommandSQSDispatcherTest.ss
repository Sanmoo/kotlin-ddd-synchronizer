╔═ testDispatch/request 1 ═╗
SendMessageRequest(QueueUrl=my-queue-url, MessageBody={"id":"uuid","aggregateId":"asdf","createdAt":"2023-06-01T00:00:00Z","type":"command","command":{"type":"create.resource.a.upstream","data":{"id":"id","name":"testingname"}}}, MessageDeduplicationId=uuid, MessageGroupId=resource1a1asdf)
╔═ testDispatch/request 2 ═╗
SendMessageRequest(QueueUrl=my-queue-url, MessageBody={"id":"uuid","aggregateId":"asdf","createdAt":"2023-06-01T00:00:00Z","type":"command","command":{"type":"update.resource.a.downstream","data":{"id":"id","name":"testingname"}}}, MessageDeduplicationId=uuid, MessageGroupId=resource1a1asdf)
╔═ [end of file] ═╗
