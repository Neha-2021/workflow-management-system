### Create Workflow definition

Requirement: The client wants to register a new workflow definition.

The request contains steps.

Description: Create a new workflow definition with version 1 or version 2 (if a workflow definition with same name
exists with version 1). Also, create steps after validating sequence numbers. Return the id of the workflow definition
created with version.

## Contract:

API: `POST /api/v1/workflows`
Request:

```
{
  "name": "ORDER_PROCESSING",
  "description": "Order processing workflow",
  "steps": [
    {
      "sequenceNumber": 1,
      "activityName": "VALIDATE_ORDER",
      "timeoutSeconds": 30
    },
    {
      "sequenceNumber": 2,
      "activityName": "PROCESS_PAYMENT",
      "timeoutSeconds": 60
    },
    {
      "sequenceNumber": 3,
      "activityName": "SEND_EMAIL"
    }
  ]
}
```

Response:

```
201 Created
{
  "workflowId": "9d2b9d4b-2d62-42e4-b43b-4d6d8aef9b89",
  "version": 1
}
```
