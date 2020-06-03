package panyi.xyz.meizitu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import panyi.xyz.meizitu.model.Resp;
import panyi.xyz.meizitu.model.Section;
import panyi.xyz.meizitu.service.SectionService;

import java.util.List;

/**
 * 获取图片族列表
 */
@RestController
public class SectionController {

    public final static Logger logger = LoggerFactory.getLogger(SectionController.class);

    public static final String DEFAULT_SECTION_SIZE = "20";

    @Autowired
    private SectionService mSectionService;

    /**
     * 查询打包历史记录
     *
     * @return
     */
    @GetMapping(value = "sections")
    public Resp<List<Section>> findSections(
            @RequestParam(value = "pagesize", required = false, defaultValue = DEFAULT_SECTION_SIZE) int pagesize,
            @RequestParam(value = "updatetime", required = false, defaultValue = "0") long updatetime) {
        logger.info("/sections  pagesize = "+pagesize+" updatetime = " + updatetime);

        List<Section> list = mSectionService.querySectionList(updatetime  , pagesize);
        Resp<List<Section>> result = Resp.genResp(list);

        logger.info("/sections result size = " + (list!=null?list.size():null));
        return result;
    }


}//end class
