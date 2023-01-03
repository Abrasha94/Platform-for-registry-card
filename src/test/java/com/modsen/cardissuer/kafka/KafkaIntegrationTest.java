package com.modsen.cardissuer.kafka;

import com.modsen.cardissuer.model.Card;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.PaySystem;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.Type;
import com.modsen.cardissuer.repository.CardRepository;
import com.modsen.cardissuer.repository.CompanyRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class KafkaIntegrationTest {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer(DockerImageName.parse("postgres:14")).withUsername("postgres").withPassword("root");

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CompanyRepository companyRepository;

    List<NewTopic> topics = Collections.singletonList(TopicBuilder.name("balanceResponse").build());

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
    }

    @Test
    public void whenCreateTopic_thenTopicListNotNull() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
        adminClient.createTopics(topics);
        final Collection<TopicListing> topicListings = adminClient.listTopics().listings().get();

        assertThat(topicListings).isNotNull();
        assertThat(topicListings.size()).isEqualTo(1);
    }

    @Test
    public void whenSendingBalance_thenUpdatedBalanceOnRightCard() throws InterruptedException, ExecutionException {
        createCard();
        String data = "{\"balance\":12.34,\"cardNumber\":22}";

        kafkaProducer.send("balanceResponse", data);
        Thread.sleep(5000);
        final Optional<Card> byId = cardRepository.findById(22L);

        assertThat(byId).isPresent();
        assertThat(byId.get().getBalance()).isEqualTo(BigDecimal.valueOf(12.34));
    }

    private void createCard() {
        final Company company = new Company();
        company.setStatus(Status.ACTIVE);
        company.setName("test");
        final Company savedCompany = companyRepository.save(company);
        final Card card = new Card();
        card.setNumber(22L);
        card.setStatus("test");
        card.setType(Type.PERSONAL);
        card.setPaySystem(PaySystem.VISA);
        card.setCompany(savedCompany);
        cardRepository.save(card);
    }
}
