╔═ testFrom ═╗
CreateResourceADownstream(createdAt=2023-06-01T00:00Z, aggregateId=123, id=abc, resourceA=ResourceA(id=123, name=A))
╔═ testToJsonNode ═╗
{
  "id" : "abc",
  "aggregateId" : "123",
  "createdAt" : "2023-06-01T00:00:00Z",
  "type" : "command",
  "command" : {
    "type" : "create.resource.a.downstream",
    "data" : {
      "id" : "123",
      "name" : "A"
    }
  }
}
╔═ [end of file] ═╗
