package stoner.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stoner.component.Consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static stoner.component.Constants.*;


@Configuration
public class RabbitmqConfig {

    private static final AtomicInteger count = new AtomicInteger();

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareExchange(new TopicExchange(TOPIC_EXCHANGE_NAME,true,false));
        for (String queueName : QUEUE_NAMES) {
            rabbitAdmin.declareQueue(new Queue(queueName, true));
            rabbitAdmin.declareBinding(
                    new Binding(queueName, Binding.DestinationType.QUEUE, TOPIC_EXCHANGE_NAME, ROUTING_KEY, new HashMap<>()));
        }
        return rabbitAdmin;
    }

    //    @Bean
//    Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
//    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAMES);
        container.setConsumerTagStrategy(queue -> queue + count.incrementAndGet());
        container.setConcurrentConsumers(2);
        container.setMaxConcurrentConsumers(5);
        container.setAfterReceivePostProcessors(message -> {
            message.getMessageProperties().setHeader("set-key","set-value");
            return message;
        });
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Consumer consumer) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(consumer);
        HashMap<String, String> Q2MN = new HashMap<>();
        for (int i = 0; i < QUEUE_NAMES.length; i++) {
            if (i < LISTENING_METHODS.length) {
                Q2MN.put(QUEUE_NAMES[i], LISTENING_METHODS[i]);
            }
        }
        listenerAdapter.setQueueOrTagToMethodName(Q2MN);
        return listenerAdapter;
    }
}
