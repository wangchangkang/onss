package work.onss.controller.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 交换机有四种类型,分别为Direct,topic,headers,Fanout.
 *
 * @author wangchanghao
 */
@Log4j2
@Configuration
public class RabbitConfig {

    /**
     * 创建队列
     *
     * @return 订单队列
     */
    @Bean
    public Queue score() {
        return new Queue("score");
    }

    /**
     * 创建队列
     *
     * @return 商品队列
     */
    @Bean
    public Queue product() {
        return new Queue("product");
    }

    /**
     * 创建订阅交换机
     *
     * @return work.1977 订阅交换机
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("work.1977");
    }

    /**
     * 订单队列和交换机绑定在一起
     *
     * @param score          订单队列
     * @param directExchange 订阅交换机
     * @return 订单路由
     */
    @Bean
    public Binding score(Queue score, DirectExchange directExchange) {
        return BindingBuilder.bind(score).to(directExchange).with("score");
    }

    /**
     * 商品队列和交换机绑定在一起
     *
     * @param product        商品队列
     * @param directExchange 订阅交换机
     * @return 商品路由
     */
    @Bean
    public Binding product(Queue product, DirectExchange directExchange) {
        return BindingBuilder.bind(product).to(directExchange).with("product");
    }


    /**
     * 订单消费者
     *
     * @param score 订单信息
     */
    @RabbitListener(queues = {"score"})
    public void score(String score) {
        log.info("------------data: {}", score);
    }


    /**
     * 商品消费者
     *
     * @param product 商品信息
     */
    @RabbitListener(queues = {"product"})
    public void product(String product) {
        log.info("------------userInfo: {}", product);
    }


}
