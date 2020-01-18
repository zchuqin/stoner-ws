package stoner.component;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MqTimer {
    private static final long DEAD_LETTER_QUEUE_TTL = 3600000;

    private static final String DEAD_LETTER_QUEUE = "DEAD_LETTER_QUEUE";

    private static final String DEAD_LETTER_EXCHANGE = "DEAD_LETTER_EXCHANGE";

    private static final String DEAD_LETTER_BIND = "DEAD_LETTER_BIND";

    private static final String SCHEDULE_QUEUE = "SCHEDULE_QUEUE";

    private static final String SCHEDULE_BIND = "SCHEDULE_BIND";

    private static final String SCHEDULE_EXCHANGE = "SCHEDULE_EXCHANGE";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MqTimer.class);

    private static AtomicInteger count = new AtomicInteger();

    private MessageListener delegate;

    public void setDelegate(MessageListener delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    @Bean(DEAD_LETTER_EXCHANGE)
    private DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean(SCHEDULE_EXCHANGE)
    private DirectExchange scheduleExchange() {
        return new DirectExchange(SCHEDULE_EXCHANGE);
    }

    @Bean(DEAD_LETTER_QUEUE)
    private Queue deadLetterQueue() {
        Map<String, Object> args = new HashMap<>(4);
        args.put("x-message-ttl", DEAD_LETTER_QUEUE_TTL);
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", SCHEDULE_QUEUE);
        return new Queue(DEAD_LETTER_QUEUE, true, false, false, args);
    }

    @Bean(SCHEDULE_QUEUE)
    private Queue scheduleQueue() {
        return new Queue(SCHEDULE_QUEUE, true, false, false);
    }

    @Bean(DEAD_LETTER_BIND)
    private Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(scheduleExchange()).with(DEAD_LETTER_QUEUE);
    }

    @Bean(SCHEDULE_BIND)
    private Binding scheduleBinding() {
        return BindingBuilder.bind(scheduleQueue()).to(deadLetterExchange()).with(SCHEDULE_QUEUE);
    }

    public void send(Message message) {
        rabbitTemplate.send(SCHEDULE_EXCHANGE, DEAD_LETTER_QUEUE, message);
    }

    public void send(String message, long ttl) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration(String.valueOf(ttl));
        send(new Message(message.getBytes(), messageProperties));
    }

    @RabbitListener(queues = {SCHEDULE_QUEUE})
    public void onMessage(Message message, Channel channel) throws Exception {
        if (delegate != null) {
            this.delegate.onMessage(message);
        } else {
            logger.info("定时任务" + count.incrementAndGet() + "： " + new String(message.getBody()));
        }
    }
}
