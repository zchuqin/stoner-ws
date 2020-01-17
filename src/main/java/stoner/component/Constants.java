package stoner.component;

public class Constants {

    public static final String TOPIC_EXCHANGE_NAME = "stoner-exchange";

    public static final String[] QUEUE_NAMES = {"stoner-queue-debug", "stoner-queue-info", "stoner-queue-warn", "stoner-queue-error", "stoner-queue-default", "DEAD_LETTER_QUEUE"};

    public static final String[] LISTENING_METHODS = {"handleMessageDebug", "handleMessageInfo", "handleMessageWarn", "handleMessageError"};

    public static final String ROUTING_KEY = "foo.bar.#";

    public static final String DEAD_LETTER_QUEUE = "DEAD_LETTER_QUEUE";
}
