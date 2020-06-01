package panyi.xyz.meizitu.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import panyi.xyz.meizitu.model.Section;


/**
 *
 * CREATE TABLE IF NOT EXISTS section (
 *     sid INTEGER PRIMARY KEY AUTOINCREMENT,
 *     content VARCHAR(2000),
 *     link VARCHAR(2000),
 *     refer VARCHAR(2000),
 *     image VARCHAR(3000),
 *     imageCount INT,
 *     updateTime LONG,
 *     extra VARCHAR(1000)
 * );
 *
 *
 */
@Mapper
@Repository
public interface SectionDao {
    @Insert("insert into section (content,link ,refer, image,imageCount,updateTime,extra) " +
            "values(#{content},#{link},#{refer}," +
            "#{image},#{imageCount},#{updateTime},#{extra})")
    @SelectKey(statement = "select seq as sid  from sqlite_sequence where (name='section')",
            before = false, keyProperty = "sid", resultType = int.class)
    int insertSection(Section section);

    @Update("update section set content =#{content} ,link =#{link} ,  refer =#{refer}," +
            "image = #{image}, imageCount = #{imageCount},updateTime=#{updateTime},extra=#{extra}  where sid = #{sid}")
    int updateSection(Section section);
}
