package slide.picture.verification.demo.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import slide.picture.verification.demo.image.ImageResult;
import slide.picture.verification.demo.image.ImgUtil;
import slide.picture.verification.demo.ret.RetCode;
import slide.picture.verification.demo.ret.WebReturn;
import slide.picture.verification.demo.time.TimeUtil;

import javax.jws.WebResult;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/slider")
public class SliderController {

    private  int xPosCache = 0;

    @RequestMapping("/image")
    public WebReturn image(){

        log.info("/slider/image");
        ImageResult imageResult = null;

        try{

            TimeUtil.start(1);
            imageResult = new ImgUtil().imageResult();
            TimeUtil.end(1);

            xPosCache = imageResult.getXpos();
            return new WebReturn(RetCode.IMAGE_REQ_SUCCESS,imageResult);
        }
        catch(Exception ex){
            log.error(ex.getMessage());
            ex.printStackTrace();
            return new WebReturn(RetCode.IMAGE_REQ_FAIL,null);
        }
    }


    @RequestMapping("/verification")
    public WebReturn verification(@RequestParam("moveX") int moveX){

        log.info("/slider/verification/{}",moveX);

        int MOVE_CHECK_ERROR = 2;
        if(( moveX < ( xPosCache + MOVE_CHECK_ERROR))
                && ( moveX >  (xPosCache - MOVE_CHECK_ERROR))){
            log.info("验证正确");
            return new WebReturn(RetCode.VERIFI_REQ_SUCCESS,true);
        }
        return new WebReturn(RetCode.VERIFI_REQ_FAIL,false);
    }




}
