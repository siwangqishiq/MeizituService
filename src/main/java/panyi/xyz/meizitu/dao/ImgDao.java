package panyi.xyz.meizitu.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;
import panyi.xyz.meizitu.model.Image;
import panyi.xyz.meizitu.model.Section;

import java.util.List;


/**
 *
 *
 *     private long sid;// section
 *     private String url;
 *     private String name;
 *     private String refer;
 *     private String extra;
 *     private long updateTime;
 *
 *
 *     CREATE TABLE IF NOT EXISTS image (
 *     id INTEGER PRIMARY KEY AUTOINCREMENT,
 *     sid LONG,
 *     url VARCHAR(3000),
 *     refer VARCHAR(3000),
 *     name VARCHAR(1000),
 *     updateTime LONG,
 *     extra VARCHAR(1000)
 * );
 *
 *
 */
@Mapper
@Repository
public interface ImgDao {
    @Insert("insert into image (sid,url ,name,refer,extra,updateTime) " +
            "values(#{sid},#{url},#{name},#{refer},#{extra},#{updateTime})")
    @SelectKey(statement = "select seq as id from sqlite_sequence where (name='image')",
            before = false, keyProperty = "id", resultType = int.class)
    int insertImage(Image image);

    @Select("select sid,url , refer , name , updateTime , extra from image where sid = #{sid}")
    List<Image> queryImageBySid(long sid);
}
