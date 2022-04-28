package net.thumbtack.school.buscompany;

import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.daoimpl.AdminDaoImpl;
import net.thumbtack.school.buscompany.daoimpl.ClientDaoImpl;
import net.thumbtack.school.buscompany.daoimpl.UserDaoImpl;
import net.thumbtack.school.buscompany.util.MyBatisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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

}

@SpringBootApplication
@EnableConfigurationProperties(Config.class)
public class MainApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        try{
            setUpDatabase();
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
