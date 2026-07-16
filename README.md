# Workflow Orchestrator

## Project Overview

Workflow Orchestrator is a Temporal-inspired workflow orchestration engine built with Java and Spring Boot. 
It supports versioned workflow definitions, activation and deactivation of workflow versions, asynchronous execution, 
pluggable activities, persistent execution state, and sequential activity orchestration.

The project currently focuses on a single-node execution model with PostgreSQL persistence and an in-memory queue.

## Architecture Diagram

```text
Client
  |
  | REST API
  v
Workflow Controllers
  |
  v
Workflow Definition Service ------> PostgreSQL
  |
  v
Workflow Execution Service -------> PostgreSQL
  |
  v
Transactional Execution Publisher
  |
  v
In-Memory Execution Queue
  |
  v
Workflow Worker
  |
  v
Workflow Engine
  |
  v
Activity Registry
  |
  v
Registered Activities
```

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker and Docker Compose
- Gradle
- JUnit 5
- Mockito
- JaCoCo
- Spotless

## Features

- Create versioned workflow definitions.
- Store workflow definitions and steps in PostgreSQL.
- Activate exactly one version of a workflow at a time.
- Deactivate workflows to prevent new executions.
- Start workflow executions asynchronously.
- Persist workflow and step execution state.
- Execute registered activities in sequence.
- Progress to the next step after successful activity execution.
- Mark workflow execution as completed when the final step succeeds.
- Register activities as Spring beans through `ActivityRegistry`.

## API Documentation

Detailed API contracts are maintained in `doc/features`:

- [Create workflow definition](doc/features/feature1_CreateWorkflowDefinition.md)
- [Activate and deactivate workflow](doc/features/feature2_CreateAPIsToActivateAndDeactiveWorkflow.md)
- [Start async workflow execution](doc/features/feature3_StartAsyncWorkflowExecution.md)
- [In-memory execution engine](doc/features/feature3.1_InMemoryExecutionEngine.md)

## How To Run Locally

Start PostgreSQL:

```bash
make docker-up
```

Run the application:

```bash
./gradlew bootRun
```

Run build with formatting:

```bash
make build
```

Run clean build with formatting:

```bash
make clean-build
```

View Docker Compose logs:

```bash
make logs
```

Stop Docker Compose services:

```bash
make docker-down
```

## Sample Workflow

Example workflow definition:

```json
{
  "name": "ORDER_WORKFLOW",
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
      "activityName": "SEND_EMAIL",
      "timeoutSeconds": 30
    }
  ]
}
```

Current registered activity names:

- `VALIDATE_ORDER`
- `PROCESS_PAYMENT`
- `NOTIFY_CUSTOMER`
- `SEND_EMAIL`

## Future Roadmap

### Sprint 6 - Retry Mechanism

Implement configurable retry policies, including fixed and exponential backoff, for failed activities. Track retry count and automatically re-queue failed steps until the retry limit is reached.

### Sprint 7 - Timeouts & Failure Handling

Add activity timeout support to automatically fail long-running steps and update workflow state based on timeout or unrecoverable failures.

### Sprint 8 - Workflow Recovery

Enable recovery of in-flight workflows after application restarts by resuming unfinished step executions persisted in the database.

### Sprint 9 - Workflow Scheduling

Support scheduled workflow execution, including one-time and recurring executions, using a scheduler that triggers workflows based on configured execution times.

### Sprint 10 - Distributed Execution

Replace the in-memory execution queue with Kafka to enable horizontally scalable workers, distributed execution, and higher throughput.
