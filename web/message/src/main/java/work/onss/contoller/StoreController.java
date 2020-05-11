package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class StoreController {

    @Autowired
    private RabbitTemplate template;

    @GetMapping("/score")
    public void send() {
        template.convertAndSend("work.1977", "score", "hello,rabbit~");
    }
}

