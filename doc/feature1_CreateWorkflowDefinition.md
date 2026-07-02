### Create Workflow definition

Requirement: The client wants to register a new workflow definition.

The request contains steps.

## Contract:
API: `POST /api/v1/workflow`
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
