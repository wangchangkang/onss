//package work.onss.config;
//
//
//import com.github.benmanes.caffeine.cache.Caffeine;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//@EnableCaching
//public class CacheConfig {
//
//
//    @Bean
//    public CacheManager getCacheManager() {
//        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("access_token");
//        caffeineCacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(71000, TimeUnit.SECONDS));
//        return caffeineCacheManager;
//    }
//
//}
