package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.Client;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ClientMybatisMapper {

    @Insert("INSERT INTO `client` (id_user, email, phone) VALUES (#{client.id}, #{client.email}, #{client.phone})")
    void insert(@Param("client") Client client);

    @Update("UPDATE `user` SET user_type = 'client' WHERE id = #{client.id}")
    void updateUserType(@Param("client") Client client);

    @Update("UPDATE `client` SET email = #{client.email}, phone = #{client.phone} WHERE id_user = #{client.id}")
    void updateClient(@Param("client") Client client);
}
