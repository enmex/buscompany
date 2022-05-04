package net.thumbtack.school.buscompany;

import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.daoimpl.AdminDaoImpl;
import net.thumbtack.school.buscompany.daoimpl.ClientDaoImpl;
import net.thumbtack.school.buscompany.daoimpl.UserDaoImpl;
import net.thumbtack.school.buscompany.service.SessionService;
import net.thumbtack.school.buscompany.util.MyBatisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.*;

@EnableWebMvc
@Configuration
@ConfigurationProperties
@PropertySource("classpath:application.properties")
@ComponentScan("net.thumbtack.school.buscompany")
class Config {
    @Bean
    public UserDao getUserDao() {
        return new UserDaoImpl();
    }

    @Bean
    public AdminDao getAdminDao(){
        return new AdminDaoImpl();
    }

    @Bean
    public ClientDao getClientDao(){
        return new ClientDaoImpl();
    }

    @Bean
    public SessionService getSessionService(){
        return new SessionService(new UserDaoImpl());
    }

}

@SpringBootApplication
@EnableConfigurationProperties(Config.class)
public class MainApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainApplication.class);

    private static SessionService service = new SessionService(new UserDaoImpl());

    public static void main(String[] args) {
        try{
            setUpDatabase();

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> {
                service.clearSessions();
                LOGGER.info("Sessions cleared");
            }, 0, 1, TimeUnit.HOURS);

            SpringApplication.run(MainApplication.class, args);
        }
        catch(RuntimeException ex){
            ex.printStackTrace();
            LOGGER.error("Can`t create connection to database, stop");
        }
    }

    private static void setUpDatabase(){
        boolean initSqlSessionFactory = MyBatisUtils.initSqlSessionFactory();
        if (!initSqlSessionFactory) {
            throw new RuntimeException("Can't create connection, stop");
        }
    }
}
