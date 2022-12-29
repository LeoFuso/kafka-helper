package org.apache.kafka.clients.admin;

import java.util.*;

import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.kafka.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;

@AutoConfiguration
@AutoConfigureBefore(KafkaAutoConfiguration.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class ExtendedKafkaAdminConfiguration {

    private final KafkaProperties properties;

    public ExtendedKafkaAdminConfiguration(final KafkaProperties properties) {
        this.properties = Objects.requireNonNull(properties, KafkaProperties.class.getSimpleName() + " [properties] is required.");
    }

    @Bean
    public ExtendedKafkaAdminOperations kafkaAdmin() {
        ExtendedKafkaAdmin kafkaAdmin = new ExtendedKafkaAdmin(properties.buildAdminProperties());
        final KafkaProperties.Admin adminProps = properties.getAdmin();
        kafkaAdmin.setFatalIfBrokerNotAvailable(adminProps.isFailFast());
        kafkaAdmin.setModifyTopicConfigs(adminProps.isModifyTopicConfigs());
        kafkaAdmin.setOperationTimeout(5);
        return kafkaAdmin;
    }
}
