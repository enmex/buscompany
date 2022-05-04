package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;

public interface UserMybatisMapper {

    @Insert("INSERT INTO `user` (firstname, lastname, patronymic, login, `password`)" +
            " VALUES (#{user.firstName}, #{user.lastName}, #{user.patronymic}, #{user.login}, #{user.password})")
    @Options(useGeneratedKeys = true, keyProperty = "user.id")
    void insert(@Param("user") User user);

    @Insert("INSERT INTO `session` (id_user, uuid, last_time_active) VALUES (#{user.id}, #{uuid}, CURRENT_TIME())")
    void insertSession(@Param("user") User user, @Param("uuid") String uuid);

    @Delete("DELETE FROM `session` WHERE uuid = #{uuid}")
    void closeSession(@Param("uuid") String uuid);

    @Delete("DELETE FROM `user` WHERE id = #{user.id}")
    void deleteUser(@Param("user") User user);

    @Select("SELECT user_type FROM `user` WHERE id = #{user.id}")
    String getUserType(@Param("user") User user);

    @Update("UPDATE `user` SET firstname = #{user.firstName}, lastname = #{user.lastName}, " +
            "patronymic = #{user.patronymic}, `password` = #{user.password}" +
            " WHERE id = #{user.id}")
    void updateUser(@Param("user") User user);

    @Delete("DELETE FROM `user`")
    void clear();

    @Delete("DELETE FROM `session` WHERE DATEDIFF(CURRENT_TIME(), last_time_active) > TIME_TO_SEC(#{userIdleTimeout})")
    void clearSessions(@Param("userIdleTimeout") int userIdleTimeout);

    @Update("UPDATE `session` SET last_time_active = CURRENT_TIME() WHERE uuid = #{uuid}")
    void updateSession(@Param("uuid") String uuid);
}
