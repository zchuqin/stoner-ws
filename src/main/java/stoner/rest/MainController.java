package stoner.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.util.HtmlUtils;
import stoner.bean.MessageVo;
import stoner.server.WebSocketServer;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("/main")
@Api("main")
@Controller
public class MainController {

    @Autowired
    private SimpMessagingTemplate template;

    private static Logger log = LoggerFactory.getLogger(MainController.class);

    private static final AtomicInteger OnlineCount = new AtomicInteger(0);

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public MessageVo greeting(@Payload MessageVo message, StompHeaderAccessor headerAccessor) throws Exception {
        Thread.sleep(1000); // simulated delay
        log.info("接收消息：{}",message.getName());
        Principal user = headerAccessor.getUser();
        if (user != null) {
            System.out.println("userName"+user.getName());
        }
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        log.info((String)sessionAttributes.get("username"));
        sessionAttributes.put("username", message.getName());

        MessageVo messageContentVo = new MessageVo("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
        return messageContentVo;
    }

    @SubscribeMapping("/hello2")
    public MessageVo sub() throws InterruptedException {
        log.info("用户订阅了我2。。。");
        MessageVo messageContentVo = new MessageVo("感谢你订阅了我。。。");
        template.convertAndSend("/topic/greetings",new MessageVo("BBBBBBBBBB"));
        Thread.sleep(1000); // simulated delay
        template.convertAndSend("/topic/greetings",new MessageVo("CCCCCCCCCCCCC"));
        Thread.sleep(1000); // simulated delay
        template.convertAndSend("/topic/greetings",new MessageVo("DDDDDDDDDDDDDDDDD"));
        Thread.sleep(1000); // simulated delay
        template.convertAndSend("/app/hello2",new MessageVo("EEEEEEEEEEEEEEE"));
        Thread.sleep(1000); // simulated delay
        return messageContentVo;
    }

    @MessageMapping("/hello3")
    @SendTo("/hello2")
    public MessageVo hello3(@Payload MessageVo message, StompHeaderAccessor headerAccessor) {
        log.info("用户订阅了我3。。。");
        template.convertAndSend("/topic/greetings",new MessageVo("AAAAAAAAAAAA"));
        template.convertAndSend("/topic/greetings",new MessageVo("AAAAAAAAAAAA"));
        template.convertAndSend("/topic/greetings",new MessageVo("AAAAAAAAAAAA"));//        String sessionId = headerAccessor.getSessionId();
        MessageHeaders messageHeaders = headerAccessor.getMessageHeaders();
        System.out.println("subscriptionId:"+messageHeaders.get("simpSubscriptionId"));


        template.convertAndSendToUser(messageHeaders.get("simpSessionId",String.class),"/app/hello3",new MessageVo("FFFFFFFFFFFFFF"));
        return new MessageVo("感谢你订阅了我" + message.getName());
    }

    @MessageExceptionHandler
    @SendToUser("/personal/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String socketId) {
        int cnt = OnlineCount.incrementAndGet();
        log.info("有连接加入，当前连接数为：{},当前连接：{}", cnt,socketId);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        int cnt = OnlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
    }


    @MessageMapping("/subscribe")
    public void subscribe(String message) {
        for(int i =1;i<=20;i++) {
            //广播使用convertAndSend方法，第一个参数为目的地，和js中订阅的目的地要一致
            template.convertAndSend("/topic/getResponse", message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @MessageMapping("/queue")
    public void queue(String message) {
        System.out.println("进入方法");
        for(int i =1;i<=20;i++) {
            /*广播使用convertAndSendToUser方法，第一个参数为用户id，此时js中的订阅地址为
            "/user/" + 用户Id + "/message",其中"/user"是固定的*/
            template.convertAndSendToUser("zhangsan","/message",message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @GetMapping("/sayHello")
    @ApiImplicitParam(name = "name")
    @ResponseBody
    public String sayHello(String name) {

        String s = HtmlUtils.htmlEscape(name);

        return name + "hello!";
    }


}
