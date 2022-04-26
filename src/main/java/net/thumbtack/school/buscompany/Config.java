package net.thumbtack.school.buscompany;

import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.daoimpl.AdminDaoImpl;
import net.thumbtack.school.buscompany.daoimpl.ClientDaoImpl;
import net.thumbtack.school.buscompany.daoimpl.UserDaoImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties
@PropertySource("application.properties")
@ComponentScan("net.thumbtack.school")
public class Config {
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
