package slide.picture.verification.demo.time;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TimeUtil {

    private static Map<Integer,Long> time = new HashMap<>();


    public  static void  start(int record){

        time.put(record,System.currentTimeMillis());
    }


    public static void end(int record){

        log.info("{}-花费时间:{}ms",record,System.currentTimeMillis()-time.get(record));

    }

}
