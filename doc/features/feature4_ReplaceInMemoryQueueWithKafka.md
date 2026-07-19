### Replace In-Memory Queue with Kafka

## Goal

Replace the in-memory execution queue with Kafka so workflow step execution becomes durable and can be processed by multiple worker instances.

## Why

Current execution uses an in-memory queue inside a single Spring Boot JVM:

```text
Client
  |
  v
Spring Boot
  |
  v
In-Memory Queue
  |
  v
Worker
```

This has several limitations:

- Queue data disappears if the application crashes.
- Workers cannot scale horizontally across JVMs.
- Only one application instance can reliably execute queued workflow steps.
- There is no durable message log or replay capability.

Kafka provides a durable execution topic that can be consumed by multiple worker instances:

```text
                +----------------+
                |    Client      |
                +-------+--------+
                        |
                        v
              WorkflowExecutionService
                        |
                        v
                 Kafka Producer
                        |
                        v
              +------------------+
              | Execution Topic  |
              +------------------+
                        |
          +-------------+-------------+
          |                           |
          v                           v
   Workflow Worker 1          Workflow Worker 2
          |                           |
          +-------------+-------------+
                        |
                        v
                 WorkflowEngine
```

## Functional Requirements

- A new workflow execution publishes the first `stepExecutionId` to Kafka.
- A worker consumes step execution events from Kafka.
- A consumed event invokes `WorkflowEngine.execute(stepExecutionId)`.
- On successful execution, the Kafka offset is committed.
- If activity execution fails, the message is not acknowledged until failure handling completes.
- Initially, failed activity handling logs the failure and commits the offset. Retry is handled in a future development.
- Existing workflow execution API behavior remains unchanged.
- `InMemoryExecutionQueue` is no longer used.

## Non-Functional Requirements

- Durable messaging
- Horizontal scalability
- At-least-once delivery
- Loose coupling
- Event-driven execution

## Scope

### In Scope

- Kafka producer
- Kafka consumer
- Execution topic
- Replace `InMemoryExecutionQueue`
- Worker consumes from Kafka

### Out of Scope

- Retry
- Dead-letter queue
- Scheduling
- Workflow recovery
- Idempotency improvements

## Event Contract

Topic:

```text
workflow.step-executions
```

Payload:

```json
{
  "stepExecutionId": "708e096f-59e4-4ca9-aceb-11a0e41e5eca"
}
```

## Implementation Notes

- `StepExecutionDispatcher` is the publishing abstraction.
- `KafkaStepExecutionDispatcher` publishes `StepExecutionEvent` to Kafka.
- `TransactionalExecutionPublisher` dispatches only after the database transaction commits.
- `StepExecutionKafkaConsumer` receives `StepExecutionEvent` and invokes `WorkflowEngine.execute(stepExecutionId)`.
- Kafka acknowledgement is manual so the offset is committed only after the engine call returns or failure handling has completed.
