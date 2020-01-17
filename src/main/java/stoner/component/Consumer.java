package stoner.component;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Consumer {

    private AtomicInteger count = new AtomicInteger(0);

    private AtomicInteger countUp = new AtomicInteger(0);


    private static Logger logger = LoggerFactory.getLogger(Consumer.class);

    public AtomicInteger getCount() {
        return count;
    }

    public void handleMessage(List message) throws IOException {
        logger.info("countUp : {},1. received a message : {}, countdown : {}", countUp.incrementAndGet(), message, count.incrementAndGet());
    }

    public void handleMessageDebug(List message) {
        logger.info("1. received a debug message : {}, countdown : {}", message, count.incrementAndGet());
    }
    public void handleMessageInfo(List message) {
        logger.info("1. received a info message : {}, countdown : {}", message, count.incrementAndGet());
    }
    public void handleMessageError(List message) {
        logger.info("1. received a error message : {}, countdown : {}", message, count.incrementAndGet());
    }

    public void handleMessageWarn(List message) {
        logger.info("1. received a warn message : {}, countdown : {}", message, count.incrementAndGet());
    }

}
