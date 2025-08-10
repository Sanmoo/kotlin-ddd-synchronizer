# Tasks

[X] Processing of Events from Legacy Bounded Context (originated in downstream outbox table)
    [X] When event is originated by Integrator Action
        [X] Connection with Legacy Database, using ActiveJDBC
        [X] Definition of Legacy Event Schema
        [X] Real processing of Legacy Event, creating one or more Commands for processing
    [X] When not
[X] Processing of Events from New Bounded Context
    [X] When event is originated by Integrator Action
    [X] When not
[X] Review: Commands need to be serialized, Events need to be deserialized. Remove functions in Message classes
[X] Processing of Commands CreateResourceADownstream and CreateResourceAUpstream
[X] Processing of Commands UpdateResourceADownstream and UpdateResourceAUpstream
[ ] Test coverage for all classes and with Integration tests using In Memory H2 Database
[ ] Test Coverage configuration with minimal threshold
[ ] Review documentation and write down what needs improvement

## Legacy Event Data

```json
{
  "data": "EVENT_DATA",
  "datacontenttype": "application/json",
  "id": "123",
  "source": "core-system",
  "type": "resource.a.created.downstream",
  "time": "2020-01-01T00:00:00.000Z"
}
```

```json
{
  "id": "123",
  "name": "blable"
}
```

### Example

```json
{
  "data": {
    "id": "123",
    "name": "blable"
  },
  "datacontenttype": "application/json",
  "id": "1234",
  "source": "core-system",
  "type": "resource.a.created.downstream",
  "time": "2020-01-01T00:00:00.000Z"
}
```