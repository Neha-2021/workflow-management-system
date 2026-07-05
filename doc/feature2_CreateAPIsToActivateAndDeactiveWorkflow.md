### APIs to activate and deactivate workflow

Add status to workflow definitions ensuring that only one active version can exist at a time, others can be inactive.
Moreover, a workflow with no active version is a valid case, denoting that the workflow is disabled for any execution.

## Activation API:

Should activate the workflow mentioned in the request, and be idempotent, i.e., even if the workflow version is
already active, still return 200 without performing any operation.

Suppose we have:

```
ORDER_WORKFLOW

V1  ACTIVE
V2  INACTIVE
V3  INACTIVE
```

Activate V3:

```
Deactivate V1 -> Activate V3
```

Result:

```
ORDER_WORKFLOW

V1  INACTIVE
V2  INACTIVE
V3  ACTIVE
```

API: `POST /api/v1/workflow/activate`

Request:

```
{
    "workflowName": "ORDER_PROCESSING",
    "version": "3"
}
```

Response:

```
200 OK
```

## Deactivation API:

Should deactivate the active workflow, and be idempotent, i.e., even if all workflow version is
already inactive, still return 200 without performing any operation.

Suppose we have:

```
ORDER_WORKFLOW

V1  ACTIVE
V2  INACTIVE
V3  INACTIVE
```

Deactivate V1:

```
Deactivate V1 
```

Result:

```
ORDER_WORKFLOW

V1  INACTIVE
V2  INACTIVE
V3  INACTIVE
```

API: `POST /api/v1/workflow/deactivate`

Request:

```
{
    "workflowName": "ORDER_PROCESSING"
}
```

Response:

```
200 OK
```

