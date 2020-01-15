package stoner.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import stoner.bean.MessageVo;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class StompController {
    @Autowired
    private SimpMessagingTemplate template;

    private static Logger logger = LoggerFactory.getLogger(StompController.class);

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

//    private static final CopyOnWriteArraySet<Session> ONLINE_SESSION_SET = new CopyOnWriteArraySet<Session>();

    @MessageMapping("/good")
    public void publish(@Payload MessageVo messageVo,StompHeaderAccessor accessor) {
        String destination = "/topic/public";
        String name = messageVo.getName();
        if (StringUtils.isEmpty(name)) {
            name = "感谢订阅！\n" + name + ", sessionId : ";
        } else {
            name = "感谢订阅！\nsessionId : ";
        }
        MessageHeaders messageHeaders = accessor.getMessageHeaders();
        name += messageHeaders.get("simpSessionId", String.class);
        messageVo.setName(name);
        template.convertAndSend(destination, messageVo);
    }

    @MessageMapping("/good/{goodNo}")
    public void publish(@Payload MessageVo messageVo, @DestinationVariable String goodNo,StompHeaderAccessor accessor) {
        String destination = "/topic/" + goodNo;
        String name = messageVo.getName();
        if (StringUtils.isEmpty(name)) {
            name = "感谢订阅！\n" + name + ", sessionId : ";
        } else {
            name = "感谢订阅！\nsessionId : ";
        }
        MessageHeaders messageHeaders = accessor.getMessageHeaders();
        name += messageHeaders.get("simpSessionId", String.class);
        messageVo.setName(name);
        template.convertAndSend(destination, messageVo);
    }

    @MessageMapping("/good/{isBoardcast}/{goodNo}")
    public void publish(@Payload MessageVo messageVo, @DestinationVariable String isBoardcast, @DestinationVariable String goodNo,StompHeaderAccessor accessor) {
        String destination;
        if ("all".equals(isBoardcast)) {
            destination = "/topic/public";
        } else if ("one".equals(isBoardcast)) {
            destination = "/topic/" + goodNo;
        } else {
            return;
        }
        String name = messageVo.getName();
        if (StringUtils.isEmpty(name)) {
            name += ", uuid : ";
        } else {
            name = "uuid : ";
        }
        Principal user = accessor.getUser();
        String passcode = accessor.getPasscode();
        if (user != null) {
            name += user.getName();
        }
        messageVo.setName(name);
        template.convertAndSend(destination, messageVo);
    }

}
