package net.thumbtack.school.buscompany;

import lombok.Getter;
import lombok.Setter;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.daoimpl.UserDaoImpl;
import net.thumbtack.school.buscompany.util.MyBatisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
