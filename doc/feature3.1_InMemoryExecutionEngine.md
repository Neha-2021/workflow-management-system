### Execution Engine (In-memory)

```
Client
↓
POST /workflow-executions
↓
Persist execution
↓
Enqueue StepExecutionId
↓
Worker Thread
↓
Execute Activity
↓
Update StepExecution
↓
Create next StepExecution
↓
Repeat till last step
↓
Workflow COMPLETED
```