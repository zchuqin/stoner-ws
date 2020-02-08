package stoner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stoner.bean.MessageVo;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@EnableScheduling
@ConfigurationProperties(prefix = "stoner")
public class CommonConfig {

    private List<String> categorys;

    private List<MessageVo> msg;

    public List<MessageVo> getMsg() {
        return msg;
    }

    public void setMsg(List<MessageVo> msg) {
        this.msg = msg;
    }

    public List<String> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<String> categorys) {
        this.categorys = categorys;
    }

    @PostConstruct
    void init() {
        if (msg != null) {
//            categorys.forEach(System.out::println);
            System.out.println(">>>>>> msg is "+msg);
        } else {
            System.out.println(">>>>>> msg is null!");
        }
    }
}
