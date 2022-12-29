package ma.archi.smart.archzy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class ArchzyApplication {
    private static ConfigurableApplicationContext ctx;

    public static void main(String[] args) {
        ctx = SpringApplication.run(ArchzyApplication.class, args);
    }

    public static ConfigurableApplicationContext getCtx() {
        return ctx;
    }
}



