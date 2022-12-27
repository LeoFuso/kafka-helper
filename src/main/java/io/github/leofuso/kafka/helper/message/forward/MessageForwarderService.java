package io.github.leofuso.kafka.helper.message.forward;

import io.github.leofuso.kafka.helper.common.*;

import jdk.incubator.concurrent.*;

import org.apache.kafka.clients.producer.*;

import org.springframework.kafka.core.*;
import org.springframework.kafka.support.*;
import org.springframework.messaging.*;
import org.springframework.messaging.support.*;
import org.springframework.stereotype.*;

import org.apache.avro.generic.*;

import java.util.*;
import java.util.concurrent.*;


@Service
public class MessageForwarderService {

    private final KafkaTemplate<String, GenericData.Record> template;

    public MessageForwarderService(final KafkaTemplate<String, GenericData.Record> kafkaTemplate) {
        template = kafkaTemplate;
    }

    <T extends GenericRecord> Message<T> forward(final Message<T> message) {

        final CompletableFuture<SendResult<String, GenericData.Record>> completableFuture = template.send(message);
        final SendResult<String, GenericData.Record> result = completableFuture.join();

        final HashMap<String, Object> headers = new HashMap<>(message.getHeaders());

        final RecordMetadata metadata = result.getRecordMetadata();
        headers.put(HeaderConstants.X_OFFSET, metadata.offset());

        final T sent = message.getPayload();
        return new GenericMessage<>(sent, headers);
    }
}
