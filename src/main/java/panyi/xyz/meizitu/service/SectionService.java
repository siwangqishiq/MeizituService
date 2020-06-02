package panyi.xyz.meizitu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panyi.xyz.meizitu.dao.SectionDao;
import panyi.xyz.meizitu.model.Section;

import java.util.List;

/**
 *  图片族数据服务
 *
 */
@Service
public class SectionService {
    @Autowired
    private SectionDao mSectionDao;

    public Section insertSection(String content , String link , String refer , String image , long updateTime){
        Section section = new Section();
        section.setContent(content);
        section.setLink(link);
        section.setRefer(refer);
        section.setImage(image);
        section.setUpdateTime(updateTime);
        section.setImageCount(0);

        mSectionDao.insertSection(section);
        return section;
    }

    public int updateSection(Section section){
        if(section == null)
            return -1;

        return mSectionDao.updateSection(section);
    }

    /**
     *  找到最近的一条section记录
     * @return
     */
    public Section findLastSection(){
        return mSectionDao.findLastSection();
    }


    /**
     *  查询图片族列表
     * @param updateTime
     * @param pageSize
     * @return
     */
    public List<Section> querySectionList(long updateTime , int pageSize){
        if(updateTime <= 0){ //返回最新的pageSize条数据
            return mSectionDao.querySectionListRecent(pageSize);
        }else{ //返回updateTime 时间点之后的 pagesize条数据 用于分页
            return mSectionDao.querySectionList(pageSize , updateTime);
        }
    }

}//end class
