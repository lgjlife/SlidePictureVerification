package slide.picture.verification.demo.image;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Slf4j
public class ImgUtil {

    //图片的路径
    private static String basePathClasspath = "img/";
    private static String basePathFile = "src/main/resources/img/";

    private static String basePath = basePathFile;
    private static String basePathOutput = "src/main/resources/img/out/";
    //图片的最大大小
    private static int IMAGE_MAX_WIDTH = 300;
    private static int IMAGE_MAX_HEIGHT = 260;
    //抠图上面的半径
    private static int RADIUS = IMAGE_MAX_WIDTH/20;
    //抠图区域的高度
    private static int CUT_HEIGHT = IMAGE_MAX_WIDTH/5;
    //抠图区域的宽度
    private static int CUT_WIDTH = IMAGE_MAX_WIDTH/5;
    //被扣地方填充的颜色
    private static int FLAG = 0x778899;
    //输出图片后缀
    private static String IMAGE_SUFFIX =  "png";



    //
    private static String ORI_IMAGE_KEY = "ORI_IMAGE_KEY";
    private static String CUT_IMAGE_KEY = "CUT_IMAGE_KEY";

    //抠图区的原点坐标(x0,y0)

    /*
      (x0,y0)         (xMax,y0)
        ****************
        *              *
        *              *
        *              *
        ****************
       (x0,yMax)　　　(xMax,yMax)
    */
    private static int XPOS;
    private static int YPOS;


    public static void main(String[] args) {
        try{
            /*ImageResult imageResult = null;
            for(int i = 0; i< 1; i++){
                 imageResult = ImgUtil.imageResult("1.png");
                System.out.println();
            }*/
            File file = new File(basePath+"ori");
            File[] files = file.listFiles();
            for (File f:files){

                log.info("本次压缩的文件为:" + f);

                BufferedImage bufferedImage = ImageIO.read(f);
                bufferedImage = ImgUtil.compressImage(bufferedImage,IMAGE_MAX_WIDTH,IMAGE_MAX_HEIGHT);

                ImageIO.write(bufferedImage,IMAGE_SUFFIX,
                        new File(basePath  + "list/" + new Random().nextInt(100)+"."+IMAGE_SUFFIX));


            }



            log.info("执行完成");
        }
        catch(Exception ex){
            ex.getMessage();
        }
    }

    /**
     *功能描述 获取抠图区的坐标原点
     * @author lgj
     * @Description
     * @date 3/29/20
     * @param:
     * @return:  void
     *
    */
    public static void init(BufferedImage oriImage){

        int height = oriImage.getHeight();
        int width = oriImage.getWidth();

        XPOS = new Random().nextInt(width-CUT_WIDTH-RADIUS);
        XPOS = correctionXpos(XPOS);

        YPOS = new Random().nextInt(height-CUT_HEIGHT-RADIUS);

    }

    /**
     *功能描述  校正原点的x坐标,使xPos始终在图像宽度的2/4-3/4处
     * @author lgj
     * @Description  
     * @date 3/30/20
     * @param: 
     * @return:  int
     *
    */
    private static int correctionXpos(int xPos){

        int div = (IMAGE_MAX_WIDTH/4);

        if(xPos/div ==  0 ){
            xPos = xPos + div*2;
        }
        else if(xPos/div ==  1 ){
            xPos = xPos + div;
        }
        else if(xPos/div ==  3 ){
            xPos = xPos - div;
        }
        return xPos;
    }
    /**
     *功能描述 对外提供的接口
     * @author lgj
     * @Description
     * @date 3/29/20
     * @param:
     * @return:  com.example.demo.image.ImageResult
     *
     */

    public static ImageResult imageResult() throws IOException{
        return imageResult(getRandomImage(basePath+"list"));
    }

    public static ImageResult imageResult(File file) throws IOException {


        ImageResult imageResult = new ImageResult();
        log.info("file = {}",file.getName());

        BufferedImage oriBufferedImage = getBufferedImage(file);

        //检测图片大小
        oriBufferedImage = checkImage(oriBufferedImage);

        //初始化原点坐标
        init(oriBufferedImage);
        //获取被扣图像的标志图
        int[][] blockData = getBlockData(oriBufferedImage);
        //printBlockData(blockData);

        //获取扣了图的原图和被扣部分的图
        Map<String,BufferedImage> imageMap =  cutByTemplate(oriBufferedImage,blockData);

        imageResult.setXpos(XPOS);
        imageResult.setCutImage(ImageBase64(imageMap.get(CUT_IMAGE_KEY)));
        imageResult.setOriImage(ImageBase64(imageMap.get(ORI_IMAGE_KEY)));

        //对被扣出部分的图透明化
        BufferedImage parentCutImage = drawTransparent(imageMap.get(CUT_IMAGE_KEY),blockData);
        imageMap.put(CUT_IMAGE_KEY,parentCutImage);

        //for test
        if(false){
            int num = new Random().nextInt(100);
            File  oriImageFile = new File(basePathOutput+ num + "-oriImageFile."+IMAGE_SUFFIX);
            ImageIO.write(imageMap.get(ORI_IMAGE_KEY),IMAGE_SUFFIX,oriImageFile);

            File  cutImageFile = new File(basePathOutput+ num + "-cutImageFile."+IMAGE_SUFFIX);
            ImageIO.write(parentCutImage,IMAGE_SUFFIX,cutImageFile);
        }

        //////////////////////

        return imageResult;
    }

    /**
     *功能描述 
     * @author lgj
     * @Description  检测图片大小是否符合要求
     * @date 3/30/20
     * @param: 
     * @return:  java.awt.image.BufferedImage
     *
    */
    private static BufferedImage checkImage(BufferedImage image) throws IOException {



        if((image.getWidth()  == IMAGE_MAX_WIDTH)
//            || (image.getHeight()  == IMAGE_MAX_HEIGHT)){
            log.info("图片大小符合要求");
            return image;
        }
        else if((image.getWidth()  < IMAGE_MAX_WIDTH)
                || (image.getHeight()  < IMAGE_MAX_HEIGHT)){
            log.info("图片太小．不符合要求w*h[300*240]");
            throw  new IllegalArgumentException("图片太小．不符合要求w*h[300*240]");
        }

        else {
            log.info("压缩图片");
            return compressImage(image,IMAGE_MAX_WIDTH,IMAGE_MAX_HEIGHT);
            //ImageIO.write(compress,"jpg",new File(basePathOutput+"compress-1.png"));

        }


    }


    /**
     *功能描述
     * @author lgj
     * @Description   使被扣地方之外变为透明色
     * @date 3/30/20
     * @param:
     * 　　image：图片　，　blockData：　抠图数据
     * @return:  java.awt.image.BufferedImage
     *
    */
    public static BufferedImage drawTransparent(BufferedImage image,int[][] blockData) throws IOException{

        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();

        BufferedImage parentImage = new BufferedImage(width,height,type);

        // 获取Graphics2D
        Graphics2D g2d = parentImage.createGraphics();

        //透明化整张图
        parentImage = g2d.getDeviceConfiguration()
                .createCompatibleImage(width, height, Transparency.BITMASK);
        g2d.dispose();
        g2d = parentImage.createGraphics();
        // 背景透明代码结束

        //重新对扣出来的部分进行填充原图的rgb
        for(int x = 0; x< image.getWidth();x++){
            for(int y = 0; y< image.getHeight(); y++){
                if (blockData[x][y] == FLAG){

                    int rgb = image.getRGB(x,y);
                   // log.info("rgb = " + rgb);
                    int b = (0xff & rgb);
                    int g = (0xff & (rgb >> 8));
                    int r = (0xff & (rgb >> 16));

                    g2d.setColor(new Color(r, g, b));
                    g2d.setStroke(new BasicStroke(1f));
                    g2d.fillRect(x, y, 1, 1);

                }
            }
        }

        // 释放对象
        g2d.dispose();

        return parentImage;
    }


    /**
     *功能描述 获取抠完图的原图和被扣出来的图
     * @author lgj
     * @Description
     * @date 3/29/20
     * @param:
     * @return:  java.util.Map<java.lang.String,java.awt.image.BufferedImage>
     *
    */
    public static Map<String,BufferedImage> cutByTemplate(BufferedImage oriImage,  int[][] blockData){


        Map<String,BufferedImage> imgMap = new HashMap<>();

        int height = oriImage.getHeight();
        int width = oriImage.getWidth();

        BufferedImage cutImage = new BufferedImage(width,height,oriImage.getType());

        for(int x = 0; x< width; x++){
            for(int y = 0; y < height; y++){

                int oriRgb = oriImage.getRGB(x,y);

                if(blockData[x][y] == FLAG){
                    oriImage.setRGB(x,y,FLAG);
                    cutImage.setRGB(x,y,oriRgb);

                }
            }
        }

        imgMap.put(ORI_IMAGE_KEY,oriImage);
        imgMap.put(CUT_IMAGE_KEY,cutImage);

        return imgMap;
    }
    /**
     *功能描述
     * @author lgj
     * @Description 获取抠图数据，被扣的像素点将使用FLAG进行标记
     * @date 3/30/20
     * @param:
     * @return:  int[][]
     *
    */
    public static int[][] getBlockData(BufferedImage oriImage){

        int height = oriImage.getHeight();
        int width = oriImage.getWidth();
        int[][] blockData =new int[width][height];

        //矩形
        for(int x = 0; x< width; x++){
            for(int y = 0; y < height; y++){

                blockData[x][y] = 0;
                if ( (x > XPOS) && (x < (XPOS+CUT_WIDTH))
                    && (y > YPOS) && (y < (YPOS+CUT_HEIGHT))){
                    blockData[x][y] = FLAG;
                }
            }
        }

        //圆形突出区域
        //突出圆形的原点坐标(x,y)
        int xBulgeCenter=0,yBulgeCenter=0;
        //
        int xConcaveCenter=0,yConcaveCenter=0;

        //位于矩形的哪一边，0123--上下左右
        int location = new Random().nextInt(3);

        if(location == 0){
            //上 凸起
            xBulgeCenter = XPOS +  CUT_WIDTH/2;
            yBulgeCenter = YPOS;
            //左　凹陷
            xConcaveCenter = XPOS ;
            yConcaveCenter = YPOS + CUT_HEIGHT/2;


        }
        else if(location == 1){
            //下　凸起
            xBulgeCenter = XPOS +  CUT_WIDTH/2;
            yBulgeCenter = YPOS + CUT_HEIGHT;

            //右　凹陷
            xConcaveCenter = XPOS +  CUT_WIDTH;
            yConcaveCenter = YPOS + CUT_HEIGHT/2;
        }
        else if(location == 2){
            //左　凸起
            xBulgeCenter = XPOS ;
            yBulgeCenter = YPOS + CUT_HEIGHT/2;

            //下　凹陷
            xConcaveCenter = XPOS +  CUT_WIDTH/2;
            yConcaveCenter = YPOS + CUT_HEIGHT;
        }
        else {
            //右　凸起
            xBulgeCenter = XPOS +  CUT_WIDTH;
            yBulgeCenter = YPOS + CUT_HEIGHT/2;
            //上　凹陷
            xConcaveCenter = XPOS +  CUT_WIDTH/2;
            yConcaveCenter = YPOS;
        }

        log.info("突出圆形位置:"+location);

        log.info("XPOS={}  YPOS={}",XPOS,YPOS);
        log.info("xBulgeCenter={}  yBulgeCenter={}",xBulgeCenter,yBulgeCenter);
        log.info("xConcaveCenter={}  yConcaveCenter={}",xConcaveCenter,yConcaveCenter);

        //半径的平方
        int RADIUS_POW2 = RADIUS * RADIUS;

        //凸起部分
        for(int x = xBulgeCenter-RADIUS; x< xBulgeCenter+RADIUS; x++){
            for(int y = yBulgeCenter-RADIUS; y < yBulgeCenter+RADIUS; y++){
                //(x-a)2+(y-b)2 = r2

                if(Math.pow((x-xBulgeCenter),2) + Math.pow((y-yBulgeCenter),2) <= RADIUS_POW2){
                    blockData[x][y] = FLAG;
                }
            }
        }

        //凹陷部分
        for(int x = xConcaveCenter-RADIUS; x< xConcaveCenter+RADIUS; x++){
            for(int y = yConcaveCenter-RADIUS; y < yConcaveCenter+RADIUS; y++){
                //(x-a)2+(y-b)2 = r2

                if(Math.pow((x-xConcaveCenter),2) + Math.pow((y-yConcaveCenter),2) <= RADIUS_POW2){
                    blockData[x][y] = 0;
                }
            }
        }



        return blockData;
    }

    private static  void printBlockData(int[][] data){

        for(int x = 0; x< data.length; x++){
            for(int y = 0; y < data[x].length; y++){
                System.out.print(data[x][y]);
            }
            System.out.println();
        }
    }

    /**
     *功能描述 获取BufferedImage对象
     * @author lgj
     * @Description
     * @date 3/29/20
     * @param:
     * @return:  java.awt.image.BufferedImage
     *
    */
    public static BufferedImage getBufferedImage(File file)throws IOException{

        return ImageIO.read(file);
    }


    /**
     *功能描述 将图片转为base64存储
     * @author lgj
     * @Description
     * @date 3/29/20
     * @param:
     * @return:  java.lang.String
     *
     */
    private static String ImageBase64(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", out);
        //转成byte数组
        byte[] bytes = out.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        //生成BASE64编码
        return encoder.encode(bytes);
    }

    /**
     * 随机获取一个图片文件
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    private static  File getRandomImage(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("该文件路径{}不对", file.getAbsolutePath());
            throw new IOException("该文件路径不对");
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length <= 0) {
                throw new IOException("该文件夹内没有文件！");
            } else {

                int index = new Random().nextInt(files.length);
                return files[index];
            }
        } else {
            throw new IOException("该文件夹内没有文件！");
        }
    }

    /**
     *功能描述  压缩图片
     * @author lgj
     * @Description
     * @date 3/30/20
     * @param:
     * @return:  java.awt.image.BufferedImage
     *
    */
    private static BufferedImage compressImage(BufferedImage image,int width,int height) throws IOException{
       return  Thumbnails.of(image)
                .forceSize(width,height)
               //.width(width).height(height)
               .asBufferedImage();
    }


}
