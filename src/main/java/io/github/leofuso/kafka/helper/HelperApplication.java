package io.github.leofuso.kafka.helper;

import java.util.concurrent.*;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.kafka.*;
import org.springframework.boot.autoconfigure.task.*;
import org.springframework.boot.web.embedded.tomcat.*;
import org.springframework.context.annotation.*;
import org.springframework.core.task.*;
import org.springframework.core.task.support.*;
import org.springframework.kafka.core.*;

@SpringBootApplication
public class HelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelperApplication.class, args);
    }

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public ExtendedKafkaAdminOperations kafkaAdmin(KafkaProperties properties) {
        ExtendedKafkaAdmin kafkaAdmin = new ExtendedKafkaAdmin(properties.buildAdminProperties());
        final KafkaProperties.Admin adminProps = properties.getAdmin();
        kafkaAdmin.setFatalIfBrokerNotAvailable(adminProps.isFailFast());
        kafkaAdmin.setModifyTopicConfigs(adminProps.isModifyTopicConfigs());
        kafkaAdmin.setOperationTimeout(5);
        return kafkaAdmin;
    }
}
