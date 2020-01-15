package stoner.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitmqListener {
    private static Logger logger = LoggerFactory.getLogger(RabbitmqListener.class);

    @RabbitListener(queues = "stoner-queue")
    public void process(String msg, Message message) {
        logger.info("received a message : {}", msg);
    }

}
