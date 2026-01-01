package cc.rainyctl.rainylangchain4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RainyLangchain4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(RainyLangchain4jApplication.class, args);
    }
}
