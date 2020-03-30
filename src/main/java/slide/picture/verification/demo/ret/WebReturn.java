package slide.picture.verification.demo.ret;

import lombok.Data;

@Data
public class WebReturn {

    RetCode code;
    Object data;

    public WebReturn(RetCode code, Object data) {
        this.code = code;
        this.data = data;
    }
}
