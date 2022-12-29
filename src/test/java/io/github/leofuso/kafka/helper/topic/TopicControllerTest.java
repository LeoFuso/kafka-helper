package io.github.leofuso.kafka.helper.topic;

import java.util.*;
import java.util.function.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.http.*;
import org.springframework.test.web.servlet.*;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.*;
import org.assertj.core.api.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("NewTopic parser unit test")
@WebMvcTest(TopicController.class)
class TopicControllerTest {

    private static final TopicDescription genericTopicDescription;
    private static final TopicDescriptionAndConfig genericTopicDescriptionConfig;

    static {
        genericTopicDescription = new TopicDescription(
                /* @formatter:off */
                "obs.approved-statement-lines",
                false,
                List.of(
                        new TopicPartitionInfo(0, new Node(1, "localhost", 9090, "rack"), List.of(new Node(2, "localhost", 9091, "rack"), new Node(3, "localhost", 9092, "rack")), List.of()),
                        new TopicPartitionInfo(1, new Node(2, "localhost", 9091, "rack"), List.of(new Node(1, "localhost", 9090, "rack"), new Node(3, "localhost", 9092, "rack")), List.of()),
                        new TopicPartitionInfo(2, new Node(3, "localhost", 9092, "rack"), List.of(new Node(1, "localhost", 9090, "rack"), new Node(2, "localhost", 9091, "rack")), List.of())
                ),
                Set.of(),
                Uuid.fromString("yu1szff1Qf2sYuR2A3M0ew")
                /* @formatter:on */
        );
        genericTopicDescriptionConfig = new TopicDescriptionAndConfig(genericTopicDescription, Map.of("retention.ms", "3600000"));
    }


    @Autowired
    private MockMvc mvc;

    @MockBean
    private TopicOperations operations;

    @Captor
    private ArgumentCaptor<NewTopic> topicCaptor;

    @BeforeEach
    void setUp() {
        doReturn(genericTopicDescriptionConfig)
                .when(operations)
                .createOrUpdate(any());
    }

    @ValueSource(
            strings = {
                    """
                            {
                                "name": "obs.approved-statement-lines",
                                "numPartitions": 3,
                                "replicationFactor": 2,
                                "configs": {
                                    "retention.ms": "3600000"
                                }
                            }
                                   """,
                    """
                            {
                                "name": "obs.approved-statement-lines",
                                "replicasAssignments": {
                                    "0": [ 1, 2 ],
                                    "1": [ 0, 2 ],
                                    "2": [ 0, 1 ]
                                },
                                "configs": {
                                    "retention.ms": "3600000"
                                }
                            }
                                   """
            }
    )
    @ParameterizedTest(name = "{index} version")
    @DisplayName(
            """
                    Given a JSON input,
                    When parsing to NewTopic object,
                    Then all fields should match.
                    """
    )
    void yu1szff1Qf2sYuR2A3M0ew(final String content) throws Exception {

        /* When */
        mvc.perform(
                        post("/topics")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string("location", "http://localhost/topics/%3Ftopics=obs.approved-statement-lines"),
                        jsonPath("$.name").value("obs.approved-statement-lines"),
                        jsonPath("$.internal").value(false),
                        jsonPath("$.partitions").isArray(),
                        jsonPath("$.configs").isMap(),
                        jsonPath("$.id").value("yu1szff1Qf2sYuR2A3M0ew")
                );

        /* Then */
        verify(operations).createOrUpdate(topicCaptor.capture());

        final NewTopic actual = topicCaptor.getValue();

        assertThat(actual)
                .isNotNull()
                .satisfies(
                        input -> assertThat(input)
                                .extracting(NewTopic::name)
                                .isEqualTo("obs.approved-statement-lines")
                )
                .satisfiesAnyOf(
                        (Consumer<? super NewTopic>) input -> assertThat(input)
                                .satisfies(
                                        ignored -> assertThat(input)
                                                .extracting(NewTopic::replicationFactor)
                                                .isEqualTo((short) 2),

                                        ignored -> assertThat(input)
                                                .extracting(NewTopic::numPartitions)
                                                .isEqualTo(3)

                                ),
                        (Consumer<? super NewTopic>) input -> assertThat(input)
                                .extracting(NewTopic::replicasAssignments)
                                .asInstanceOf(InstanceOfAssertFactories.map(Integer.class, List.class))
                                .containsEntry(0, List.of(1, 2))
                                .containsEntry(1, List.of(0, 2))
                                .containsEntry(2, List.of(0, 1))
                );
    }
}