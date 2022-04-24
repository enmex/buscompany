package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.Admin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface AdminMybatisMapper {

    @Insert("INSERT INTO `admin` (id_user, position) VALUES(#{admin.id}, #{admin.position})")
    void insert(@Param("admin") Admin admin);

    @Update("UPDATE `user` SET user_type = 'admin' WHERE id = #{admin.id}")
    void updateUserType(@Param("admin") Admin admin);

    @Update("UPDATE `admin` SET position = #{admin.position} WHERE id_user = #{admin.id}")
    void updateAdmin(@Param("admin") Admin admin);
}
