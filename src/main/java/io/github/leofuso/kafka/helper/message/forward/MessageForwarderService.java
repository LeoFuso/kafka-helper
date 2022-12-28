package io.github.leofuso.kafka.helper.message.forward;

import io.github.leofuso.kafka.helper.common.CommonHeaders;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;


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
        headers.put(CommonHeaders.X_OFFSET, metadata.offset());

        final T sent = message.getPayload();
        return new GenericMessage<>(sent, headers);
    }
}
