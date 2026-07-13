package orchestrator.execution.worker;

import jakarta.annotation.PostConstruct;
import java.util.UUID;
import orchestrator.execution.engine.WorkflowEngine;
import orchestrator.execution.queue.ExecutionQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkflowWorker {
  private final ExecutionQueue executionQueue;
  private final WorkflowEngine workflowEngine;

  private static final Logger log = LoggerFactory.getLogger(WorkflowWorker.class);

  public WorkflowWorker(ExecutionQueue executionQueue, WorkflowEngine workflowEngine) {
    this.executionQueue = executionQueue;
    this.workflowEngine = workflowEngine;
  }

  @PostConstruct
  public void start() {
    Thread.startVirtualThread(this::run);
  }

  private void run() {

    while (true) {
      try {
        UUID stepExecutionId = executionQueue.take();
        log.info("WorkflowWorker | Proceeding to execute step: {}", stepExecutionId);
        workflowEngine.execute(stepExecutionId);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        break;

      } catch (Exception ex) {
        log.error("Worker failed to execute step", ex);
      }
    }
  }
}
