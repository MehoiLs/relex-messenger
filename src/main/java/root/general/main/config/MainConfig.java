package root.general.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
@EnableAsync
public class MainConfig {

    @Bean(name = "customTaskExecutor")
    @Primary
    public ThreadPoolTaskExecutor customTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}
