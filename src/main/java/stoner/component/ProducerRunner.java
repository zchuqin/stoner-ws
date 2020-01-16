package stoner.component;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
            rabbitTemplate.convertAndSend("stoner-exchange", "foo.bar.baz", "Hello from RabbitMQ!");
            Thread.sleep(100);
        }
    }
}
