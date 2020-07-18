package panyi.xyz.meizitu.scheduled;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import panyi.xyz.meizitu.Config;
import panyi.xyz.meizitu.model.Image;
import panyi.xyz.meizitu.util.UrlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static panyi.xyz.meizitu.util.UrlUtil.isDigitsOnly;

public class TestTask {

    public static void main(String[] agrs){
        String url = "https://www.mzitu.com/241778";

        Document imageDoc = null;
        try {
            imageDoc = Jsoup.connect(url).userAgent(Config.UA).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image first = readMainImage(imageDoc, url);
        List<Image> images = readImages(imageDoc, first, 123);

        if (images != null) {
            for (Image img : images) {
                System.out.println(img.getUrl()+ "    " +img.getRefer() +"  " + img.getSid());
            }//end for each
        }

    }

    private static Image readMainImage(Document doc, final String refer) {
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
    private static List<Image> readImages(Document doc, Image firstNode, int sectionId) {
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

        System.out.println("total = " + totalNum);
        System.out.println("result link = " + link);

        String imageBaseUrl = UrlUtil.findUrlWithOutSufix(firstNode.getUrl());

        String suffix = UrlUtil.findUrlSufix(firstNode.getUrl());

        System.out.println("imageBaseUrl = " + imageBaseUrl+"  suffix = " + suffix);



        String imageFormat = UrlUtil.getImageFormatStr(suffix, imageBaseUrl);

        String linkBase = UrlUtil.findUrlWithOutSufix(link);
        String linkFormat = linkBase + "/%s";

        int startIndex = 1;
        try{
            if(suffix.indexOf(".")!= -1){
                if(suffix.charAt(suffix.indexOf(".") -1) != '1'){
                    startIndex = Integer.parseInt(suffix.charAt(suffix.indexOf(".") -1)+"");
                }
            }
        }catch (Exception e){
            startIndex = 1;
            e.printStackTrace();
        }

        List<Image> imageList = new ArrayList<Image>();
        for (int i = 0; i < totalNum; i++) {
            String refer = String.format(linkFormat, i);

            int offsetIndex = i + startIndex;
            String imageUrl = null;
            if (offsetIndex < 10) {
                imageUrl = String.format(imageFormat, "0" + offsetIndex);
            } else {
                imageUrl = String.format(imageFormat, offsetIndex);
            }

            Image imageNode = new Image();
            imageNode.setUrl(imageUrl);
            imageNode.setRefer(refer);
            imageNode.setSid(sectionId);

            imageList.add(imageNode);
        }//end for i
        return imageList;
    }
}
