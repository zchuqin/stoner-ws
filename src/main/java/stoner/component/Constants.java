package stoner.component;

public class Constants {

    public static final String TOPIC_EXCHANGE_NAME = "stoner-exchange";

    public static final String[] QUEUE_NAMES = {"stoner-queue-debug", "stoner-queue-info", "stoner-queue-warn", "stoner-queue-error", "stoner-queue-default"};

    public static final String[] LISTENING_METHODS = {"handleMessageDebug", "handleMessageInfo", "handleMessageWarn", "handleMessageError"};

    public static final String ROUTING_KEY = "foo.bar.#";

    public static final String DEAD_LETTER_QUEUE = "DEAD_LETTER_QUEUE";

    public static final String DEAD_LETTER_EXCHANGE = "DEAD_LETTER_EXCHANGE";

    public static final long DEAD_LETTER_QUEUE_TTL = 30000;

    public static final String SCHEDULE_QUEUE = "SCHEDULE_QUEUE";

    public static final String SCHEDULE_BING = "SCHEDULE_BING";

    public static final String SCHEDULE_ROUTING_KEY = "SCHEDULE_ROUTING_KEY";

    public static final String DEAD_LETTER_BING = "DEAD_LETTER_BING";

    public static final String DEAD_LETTER_ROUTING_KEY = "DEAD_LETTER_ROUTING_KEY";

    public static final String SCHEDULE_EXCHANGE = "SCHEDULE_EXCHANGE";
}
