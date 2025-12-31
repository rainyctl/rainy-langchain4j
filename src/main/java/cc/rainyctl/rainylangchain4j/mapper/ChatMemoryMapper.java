package cc.rainyctl.rainylangchain4j.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMemoryMapper {
    String findByMemoryId(@Param("memoryId") String memoryId);

    int insertMemory(@Param("memoryId") String memoryId, @Param("value")  String value);

    int updateMemory(@Param("memoryId") String memoryId, @Param("value")  String value);

    int upsertMemory(@Param("memoryId") String memoryId, @Param("value")  String value);

    int deleteMemory(@Param("memoryId") String memoryId);
}
