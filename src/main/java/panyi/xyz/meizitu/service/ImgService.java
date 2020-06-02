package panyi.xyz.meizitu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panyi.xyz.meizitu.dao.ImgDao;
import panyi.xyz.meizitu.model.Image;

import java.util.List;

@Service
public class ImgService {
    @Autowired
    private ImgDao mImgDao;

    public long insertImage(int sid , String name , String refer , String url){
        Image img = new Image();
        img.setSid(sid);
        img.setName(name);
        img.setRefer(refer);
        img.setUrl(url);
        img.setUpdateTime(System.currentTimeMillis());

        mImgDao.insertImage(img);
        return img.getId();
    }

    /**
     *  根据sectionId 查询图片列表
     *
     * @param sid
     * @return
     */
    public List<Image> queryImageListBySid(long sid){
        return mImgDao.queryImageBySid(sid);
    }

}//end class
