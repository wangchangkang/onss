package work.onss;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author wangchanghao
 */
@Log4j2
@EnableAsync
@EnableAdminServer
@SpringBootApplication
public class ManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class, args);
    }

}
