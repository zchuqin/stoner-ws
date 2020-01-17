package stoner.config;

import com.sun.org.apache.bcel.internal.generic.FADD;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Header;
import stoner.component.Consumer;

import javax.swing.table.TableRowSorter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static stoner.component.Constants.*;


@Configuration
public class RabbitmqConfig {

    private static final AtomicInteger count = new AtomicInteger();

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareExchange(new TopicExchange(TOPIC_EXCHANGE_NAME, true, false));
        for (String queueName : QUEUE_NAMES) {
            rabbitAdmin.declareQueue(new Queue(queueName, true));
            rabbitAdmin.declareBinding(
                    new Binding(queueName, Binding.DestinationType.QUEUE, TOPIC_EXCHANGE_NAME, ROUTING_KEY, new HashMap<>()));
        }
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("=====消息进行消费了======");
            if (ack) {
                System.out.println("消息id为: " + correlationData + "的消息，已经被ack成功，原因是：" + cause);
            } else {
                System.out.println("消息id为: " + correlationData + "的消息，消息nack，失败原因是：" + cause);
            }
        });
        return rabbitTemplate;
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAMES);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConsumerTagStrategy(queue -> queue + count.incrementAndGet());
        container.setConcurrentConsumers(2);
        container.setMaxConcurrentConsumers(5);
        container.setAfterReceivePostProcessors(message -> {
            message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
            long tag = message.getMessageProperties().getDeliveryTag();

            return message;
        });
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Consumer consumer) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(consumer);
        listenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
        HashMap<String, String> q2m = new HashMap<>();
        int min = Math.min(QUEUE_NAMES.length, LISTENING_METHODS.length);
        for (int i = 0; i < min; i++) {
            q2m.put(QUEUE_NAMES[i], LISTENING_METHODS[i]);
        }
        listenerAdapter.setQueueOrTagToMethodName(q2m);
        return listenerAdapter;
    }

    @Bean("deadLetterExchange")
    DirectExchange deadLetterExchange() {
        return new DirectExchange("deadLetterExchange");
    }

        @Bean("deadLetterQueue")
    Queue deadLetterQueue() {
        Map<String, Object> args = new HashMap<>(2);
            args.put("x-message-ttl", 3000);
        args.put("x-dead-letter-exchange", "deadLetterExchange");
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE);
        return new Queue(DEAD_LETTER_QUEUE, true, false, false);
    }

}
