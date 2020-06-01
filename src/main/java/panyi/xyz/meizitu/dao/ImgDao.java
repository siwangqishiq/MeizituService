package panyi.xyz.meizitu.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;
import panyi.xyz.meizitu.model.Image;
import panyi.xyz.meizitu.model.Section;


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
 */
@Mapper
@Repository
public interface ImgDao {
    @Insert("insert into image (sid,url ,name,refer,extra,updateTime) " +
            "values(#{sid},#{url},#{name},#{refer},#{extra},#{updateTime})")
    @SelectKey(statement = "select seq as id  from sqlite_sequence where (name='image')",
            before = false, keyProperty = "id", resultType = int.class)
    public int insertImage(Image image);

}
