package panyi.xyz.meizitu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panyi.xyz.meizitu.dao.SectionDao;
import panyi.xyz.meizitu.model.Section;

@Service
public class SectionService {
    @Autowired
    private SectionDao mSectionDao;

    public Section insertSection(String content , String link , String refer , String image){
        Section section = new Section();
        section.setContent(content);
        section.setLink(link);
        section.setRefer(refer);
        section.setImage(image);
        section.setUpdateTime(System.currentTimeMillis());
        section.setImageCount(0);

        mSectionDao.insertSection(section);
        return section;
    }

    public int updateSection(Section section){
        if(section == null)
            return -1;

        return mSectionDao.updateSection(section);
    }
}
