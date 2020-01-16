package stoner.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Consumer {

    private AtomicInteger count = new AtomicInteger(0);

    private static Logger logger = LoggerFactory.getLogger(Consumer.class);

    public AtomicInteger getCount() {
        return count;
    }

    public void handleMessage(String message) {
        logger.info("1. received a message : {}, countdown : {}", message, count.incrementAndGet());
    }

    public void handleMessage(Message message) {
        Object value = message.getMessageProperties().getHeader("set-key");
        logger.info("1. received a message : {}, countdown : {},set-value : {}", new String(message.getBody()), count.incrementAndGet(),value);
    }

    public void handleMessageDebug(String message) {
        logger.info("1. received a debug message : {}, countdown : {}", message, count.incrementAndGet());
    }
    public void handleMessageInfo(String message) {
        logger.info("1. received a info message : {}, countdown : {}", message, count.incrementAndGet());
    }
    public void handleMessageError(String message) {
        logger.info("1. received a error message : {}, countdown : {}", message, count.incrementAndGet());
    }
    public void handleMessageWarn(String message) {
        logger.info("1. received a warn message : {}, countdown : {}", message, count.incrementAndGet());
    }

}
