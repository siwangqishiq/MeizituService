package panyi.xyz.meizitu.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import panyi.xyz.meizitu.model.Section;

import java.util.List;


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

    @Select("select sid, content,link ,refer, image,imageCount,updateTime,extra from section order by updateTime desc limit 1")
    Section findLastSection();

    @Select("select sid, content,link ,refer, image,imageCount,updateTime,extra from section " +
            " order by updateTime desc limit #{pageSize}")
    List<Section> querySectionListRecent(int pageSize);

    @Select("select sid, content,link ,refer, image,imageCount,updateTime,extra from section " +
            "where updateTime < #{updateTime} order by updateTime desc limit #{pageSize}")
    List<Section> querySectionList(int pageSize , long updateTime);
}//end class
