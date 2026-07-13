package orchestrator.execution.queue;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.stereotype.Component;

@Component
public class InMemoryExecutionQueue implements ExecutionQueue {

  private final BlockingQueue<UUID> queue = new LinkedBlockingQueue<>();

  @Override
  public void submit(UUID stepExecutionId) {
    queue.offer(stepExecutionId);
  }

  @Override
  public UUID take() throws InterruptedException {
    return queue.take();
  }
}
