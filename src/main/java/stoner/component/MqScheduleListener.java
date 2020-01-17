package stoner.component;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static stoner.component.Constants.SCHEDULE_QUEUE;

@Component
public class MqScheduleListener {

    private static final Logger logger = LoggerFactory.getLogger(MqScheduleListener.class);

    private static AtomicInteger count = new AtomicInteger();

    @RabbitListener(queues = {SCHEDULE_QUEUE})
    public void handleMessage(Channel channel, Message message) throws IOException {
        logger.info("定时任务" + count.incrementAndGet() + "： " + new String(message.getBody()));
    }
}
