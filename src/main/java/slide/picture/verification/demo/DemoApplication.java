package slide.picture.verification.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import slide.picture.verification.demo.image.ImageResult;
import slide.picture.verification.demo.image.ImgUtil;

@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);

        /*try{
            ImageResult imageResult = ImgUtil.imageResult("1.png");
            log.info("执行完成");
        }
        catch(Exception ex){
            ex.getMessage();
        }*/
    }

}
