package stoner.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Consumer {

    private CountDownLatch latch = new CountDownLatch(1);

    private static Logger logger = LoggerFactory.getLogger(Consumer.class);

    public CountDownLatch getLatch() {
        return latch;
    }

    public void receiveMessage1(String message) {
        logger.info("1. received a message : {}", message);
        latch.countDown();

    }

    public void receiveMessage2(String message) {
        logger.info("2. received a message : {}", message);
        latch.countDown();

    }
}
