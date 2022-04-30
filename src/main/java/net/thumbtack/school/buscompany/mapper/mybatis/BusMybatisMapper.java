package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.Bus;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BusMybatisMapper {
    @Insert("INSERT INTO bus (bus_name) VALUES (#{bus.busName})")
    @Options(useGeneratedKeys = true, keyProperty = "bus.id")
    void insert(@Param("bus") Bus bus);

    @Select("SELECT * FROM bus")
    @Results({
            @Result(column = "bus_name", property = "busName"),
            @Result(column = "seats_number", property = "placeCount")
    })
    List<Bus> getAllBuses();

    @Select("SELECT * FROM bus WHERE bus_name = #{busName}")
    @Results({
        @Result(column = "bus_name", property = "busName"),
        @Result(column = "seats_number", property = "placeCount")
    })
    Bus getBus(@Param("busName") String busName);

    @Delete("DELETE FROM bus")
    void clear();
}
