### Test - Start a workflow

```
curl --request POST \
  --url http://localhost:8080/api/v1/workflow-executions \
  --header 'Content-Type: application/json' \
  --data '{
	"workflowName": "ORDER_WORKFLOW",
	"input": {
		"orderId": "order001",
		"customerId": "customer001"
	}
}'
```

Expected Response (Asynchronous): 
```
202 Accepted
{
	"workflowExecutionId": "9b3276e1-a3e0-48ac-9558-8eeac47ee43c",
	"status": "RUNNING"
}
```

Activities: VALIDATE_ORDER and PROCESS_PAYMENT was completed successfully, hence ORDER_WORKFLOW was completed. 