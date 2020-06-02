package panyi.xyz.meizitu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import panyi.xyz.meizitu.model.Image;
import panyi.xyz.meizitu.model.Resp;
import panyi.xyz.meizitu.service.ImgService;

import java.util.List;

/**
 *  获取sections下图片
 */
@RestController
public class ImageController {

    @Autowired
    private ImgService mImageService;

    /**
     * 查询打包历史记录
     * @return
     */
    @GetMapping(value = "images")
    public Resp<List<Image>> findImagesBySid(
            @RequestParam(value="sid", required=true)  long sid){
        System.out.println("/images   sid = " + sid);

        List<Image> list = mImageService.queryImageListBySid(sid);
        System.out.println("/images result size = " + list!=null?list.size():null);

        return Resp.genResp(list);
    }
}//end class
