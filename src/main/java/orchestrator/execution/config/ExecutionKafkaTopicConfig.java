package orchestrator.execution.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ExecutionKafkaTopicConfig {

  @Bean
  public NewTopic stepExecutionTopic(
      @Value("${workflow.execution.topic}") String topicName,
      @Value("${workflow.execution.topic-partitions:3}") int partitions) {
    return TopicBuilder.name(topicName).partitions(partitions).replicas(1).build();
  }
}
