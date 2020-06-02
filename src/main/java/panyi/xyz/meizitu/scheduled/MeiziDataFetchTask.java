package panyi.xyz.meizitu.scheduled;

import com.jcraft.jsch.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import panyi.xyz.meizitu.Config;
import panyi.xyz.meizitu.model.Image;
import panyi.xyz.meizitu.model.Section;
import panyi.xyz.meizitu.service.ImgService;
import panyi.xyz.meizitu.service.SectionService;
import panyi.xyz.meizitu.util.UrlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static panyi.xyz.meizitu.Config.MEI_URL;
import static panyi.xyz.meizitu.util.UrlUtil.isDigitsOnly;

/**
 *   定时拉取数据 存入db
 *
 */
@Component
public class MeiziDataFetchTask {

    @Autowired
    private SectionService mSectionService;

    @Autowired
    private ImgService mImageService;

    @Scheduled(fixedDelay = 12 * 60 * 60 * 1000)
    public void fetchData() {
        System.out.println("fetch data from meizitu " + MEI_URL);

        int addSectionCount = 0;
        int addImageCount = 0;

        final Section lastSection = mSectionService.findLastSection(); //上一次发现的section

        String url = MEI_URL;

        long updateTime = System.currentTimeMillis(); //插入的数据降序排列

        boolean continueSearch = true;

            //List<Section> sectionList = new ArrayList<Section>(32);

            while (!StringUtils.isEmpty(url)) {
                System.out.println("url = " + url);
                List<Section> list = null;
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).userAgent(Config.UA).get();
                    list = readNodeList(url, doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (list != null) {
                    for (Section section : list) {
                        if (lastSection != null && section.isSame(lastSection)) {//
                            continueSearch = false;
                            break;
                        }

                        final Section insertSection = mSectionService.insertSection(section.getContent(), section.getLink(), section.getRefer(), section.getImage() , updateTime);
                        updateTime--;

                        System.out.println("add section " + insertSection.getSid() + "  " + insertSection.getContent());
                        addSectionCount++;

                        Document imageDoc = null;
                        try {
                            imageDoc = Jsoup.connect(section.getLink()).userAgent(Config.UA).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(imageDoc == null){
                            continue;
                        }

                        Image first = readMainImage(imageDoc, section.getRefer());
                        List<Image> images = readImages(imageDoc, first, insertSection.getSid());

                        if (images != null) {
                            for (Image img : images) {
                                //System.out.println(img.getImage() + "    " +img.getRefer() +"  " + img.getSid());
                                long imgId = mImageService.insertImage(insertSection.getSid(), insertSection.getContent(), img.getRefer(), img.getUrl());
                                System.out.println("add image imgID --> " + imgId + " " + img.getUrl());
                                addImageCount++;
                            }//end for each
                            insertSection.setImageCount(images.size());
                            int row = mSectionService.updateSection(insertSection);
                            //System.out.println("row = " + row);
                        }
                    }//end for each


                }

                if(!continueSearch){//不再继续搜索
                    break;
                }

                url = readNextUrl(doc);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }//end while

            System.out.println("ADD New Section : " + addSectionCount + "   New Image : " + addImageCount);
    }

    /**
     * 根节点列表 读取
     *
     * @param doc
     * @return
     * @throws Exception
     */
    private List<Section> readNodeList(String url, Document doc) throws Exception {
        if (doc == null)
            return null;

        Elements postList = doc.getElementsByClass("postlist");
        if (postList.size() == 0)
            return null;

        Element contentElem = postList.get(0);
        Elements list = contentElem.getElementsByTag("li");

        List<Section> sectionNodes = new ArrayList<Section>();

        for (Element liElem : list) {
            Section section = new Section();
            section.setRefer(url);

            Elements aList = liElem.getElementsByTag("a");
            if (aList.size() > 0) {
                Element aTag = aList.get(0);
                String link = aTag.absUrl("href");
                section.setLink(link);
            }

            Elements imgList = liElem.getElementsByTag("img");
            if (imgList.size() > 0) {
                Element imgTag = imgList.get(0);
                String title = imgTag.attr("alt");
                //System.out.println("title ----> "+title);
                section.setContent(title);
                String imageUrl = imgTag.absUrl("data-original");
                //System.out.println("imageUrl ----> "+imageUrl);
                section.setImage(imageUrl);
            }
            //System.out.println("sdsa--->"+liElem.toString());
            sectionNodes.add(section);
        }//end for each li tag

        return sectionNodes;
    }

    /**
     * @param doc
     * @return
     */
    private String readNextUrl(Document doc) {
        if (doc == null)
            return null;

        Elements ret = doc.getElementsByClass("next page-numbers");
        if (ret.size() == 0)
            return null;
        Element elem = ret.get(0);

        String nextHref = elem.absUrl("href");
        //System.out.println("next ---->"+nextHref);
        return nextHref;
    }


    private Image readMainImage(Document doc, final String refer) {
        if (doc == null)
            return null;

        Elements elements = doc.getElementsByClass("main-image");
        if (elements == null || elements.size() == 0)
            return null;

        Element elem = elements.get(0);
        Elements imgs = elem.getElementsByTag("img");
        if (imgs != null && imgs.size() > 0) {
            String firstImageUrl = imgs.get(0).absUrl("src");
            Image node = new Image();
            node.setRefer(refer);
            node.setUrl(firstImageUrl);
            return node;
        }

        return null;
    }

    /**
     * section read images
     */
    private List<Image> readImages(Document doc, Image firstNode, int sectionId) {
        if (doc == null || firstNode == null)
            return null;

        Elements pageElems = doc.getElementsByClass("pagenavi");
        if (pageElems == null || pageElems.size() == 0)
            return null;


        Element pageElem = pageElems.get(0);
        Elements childs = pageElem.children();
        int totalNum = 0;
        String link = null;
        for (int i = 0, len = childs.size(); i < len; i++) {
            try {
                Element childNode = childs.get(i);
                //System.out.println(childNode);
                if ("a".equals(childNode.tagName())) {
                    Elements as = childNode.getElementsByTag("a");
                    Element aElem = as.get(0);
                    link = aElem.absUrl("href");
                    //System.out.println("link = " + link);
                    Elements spans = aElem.getElementsByTag("span");
                    Element span = spans.get(0);
                    //System.out.println("span = " + span.html());
                    if (isDigitsOnly(span.html())) {
                        int spanVal = Integer.parseInt(span.html());
                        if (spanVal > totalNum) {
                            totalNum = spanVal;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end for i

        //System.out.println("total = " + totalNum);
        //System.out.println("result link = " + link);

        String imageBaseUrl = UrlUtil.findUrlWithOutSufix(firstNode.getUrl());
        String suffix = UrlUtil.findUrlSufix(firstNode.getUrl());
        String imageFormat = UrlUtil.getImageFormatStr(suffix, imageBaseUrl);

        String linkBase = UrlUtil.findUrlWithOutSufix(link);
        String linkFormat = linkBase + "/%s";

        List<Image> imageList = new ArrayList<Image>();
        for (int i = 1; i <= totalNum; i++) {
            String refer = String.format(linkFormat, i);
            String imageUrl = null;
            if (i < 10) {
                imageUrl = String.format(imageFormat, "0" + i);
            } else {
                imageUrl = String.format(imageFormat, i);
            }

            Image imageNode = new Image();
            imageNode.setUrl(imageUrl);
            imageNode.setRefer(refer);
            imageNode.setSid(sectionId);

            imageList.add(imageNode);
        }//end for i
        return imageList;
    }
}//end class
