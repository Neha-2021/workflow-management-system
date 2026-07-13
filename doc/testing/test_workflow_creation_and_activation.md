### Test - Create and activate a workflow

## Create:

```
curl --request POST \
  --url http://localhost:8080/api/v1/workflows \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "ORDER_WORKFLOW",
	"description": "Order Processing test 001",
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
    }
	]
}'
```

Expected response:
```
201 Created
{
	"workflowId": "60738b85-e41f-4054-852c-53c17cdba8ea",
	"version": 1
}
```

## Activate:

```
curl --request POST \
  --url http://localhost:8080/api/v1/workflows/activate \
  --header 'Content-Type: application/json' \
  --data '{
	"workflowName": "ORDER_WORKFLOW",
	"version": 1
}'
```

Expected response:
```
200 OK
```