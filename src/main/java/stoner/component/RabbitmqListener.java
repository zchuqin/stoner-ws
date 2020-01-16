package stoner.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RabbitmqListener {
    private static Logger logger = LoggerFactory.getLogger(RabbitmqListener.class);

    @Autowired
    private Consumer consumer;

    @RabbitListener(queues = "stoner-queue")
    public void process(String msg, Message message) {
        logger.info("received a message : {}, count : {}", msg, consumer.getCount().incrementAndGet());
    }

}
