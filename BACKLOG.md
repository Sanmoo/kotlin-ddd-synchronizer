# Tasks

[X] Processing of Events from Legacy Bounded Context (originated in downstream outbox table)
    [X] When event is originated by Integrator Action
        [X] Connection with Legacy Database, using ActiveJDBC
        [X] Definition of Legacy Event Schema
        [X] Real processing of Legacy Event, creating one or more Commands for processing
    [X] When not
[ ] Processing of Events from New Bounded Context
    [ ] When event is originated by Integrator Action
    [ ] When not
[ ] Processing of Commands CreateResourceADownstream and CreateResourceAUpstream
[ ] Processing of Commands UpdateResourceADownstream and UpdateResourceAUpstream
[ ] Processing of Commands CreateResourceBDownstream and CreateResourceBUpstream
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