package stoner.component;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static stoner.component.Constants.*;

@Component
public class ProducerRunner implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Consumer consumer;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending message...");
        for (int i = 0; i < 100; i++) {
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, "foo.bar.baz", "[{\"name\":\"Hello from RabbitMQ!\"},{\"id\":\"hahaha\"}]");
            Thread.sleep(100);
        }
        for (int i = 0; i < 40; i++) {
            rabbitTemplate.convertAndSend(SCHEDULE_EXCHANGE, DEAD_LETTER_ROUTING_KEY, "[{\"name\":\"Hello from RabbitMQ!\"},{\"id\":\"hahaha\"}]");
            Thread.sleep(3000);
        }
    }
}
