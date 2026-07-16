### Workflow Execution (Asynchronous)

```
             POST /workflow-executions
                     │
                     ▼
      Validate workflow definition
                     │
                     ▼
      Create WorkflowExecution (RUNNING)
                     │
                     ▼
      Create first StepExecution (PENDING)
                     │
                     ▼
         Publish Execution Task
                     │
                     ▼
      Return workflowExecutionId immediately (202 Accepted)
```

## API Contract:

Description: Fetch the latest active workflow by workflowName, and return immediately(async). Persist execution metadata
and create the execution step, which would enqueue the step.

API: `POST /api/v1/workflow-executions`

Request:

```
{
    "workflowName": "ORDER_PROCESSING",
    "input": {
        "customerId": "customer001",
        "orderId": "order001"
    }
}
```

Response:

```
202 Accepted
{
    "workflowExecutionId": "...",
    "status": "RUNNING"
}
```
