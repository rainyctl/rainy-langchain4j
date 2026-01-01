package cc.rainyctl.rainylangchain4j.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * mysql -uroot -p
 * use test_db;
 * CREATE TABLE chat_memory (
 *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     memory_id VARCHAR(255) NOT NULL,
 *     value TEXT NOT NULL,
 *     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 *     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *     UNIQUE KEY uq_memory_id (memory_id)
 * );
 */
@Mapper
public interface ChatMemoryMapper {
    String findByMemoryId(@Param("memoryId") String memoryId);

    int insertMemory(@Param("memoryId") String memoryId, @Param("value")  String value);

    int updateMemory(@Param("memoryId") String memoryId, @Param("value")  String value);

    int upsertMemory(@Param("memoryId") String memoryId, @Param("value")  String value);

    int deleteMemory(@Param("memoryId") String memoryId);
}
