package stoner.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stoner.component.Consumer;


@Configuration
public class RabbitmqConfig {

    private static final String TOPIC_EXCHANGE_NAME = "stoner-exchange";

    private static final String QUEUE_NAME = "stoner-queue";

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

    @Bean
    SimpleMessageListenerContainer container1(ConnectionFactory connectionFactory,@Qualifier(value = "listenerAdapter1")
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    @Bean
    SimpleMessageListenerContainer container2(ConnectionFactory connectionFactory,@Qualifier(value = "listenerAdapter2")
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean(name = "listenerAdapter1")
    MessageListenerAdapter listenerAdapter1(Consumer consumer) {
        return new MessageListenerAdapter(consumer, "receiveMessage1");
    }

    @Bean(name = "listenerAdapter2")
    MessageListenerAdapter listenerAdapter2(Consumer consumer) {
        return new MessageListenerAdapter(consumer, "receiveMessage2");
    }
}
