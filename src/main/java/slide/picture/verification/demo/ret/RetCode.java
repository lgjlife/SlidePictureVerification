package slide.picture.verification.demo.ret;


import lombok.Data;


public enum  RetCode {

    IMAGE_REQ_SUCCESS(1,"图片请求成功"),
    IMAGE_REQ_FAIL(2,"图片请求失败"),
    VERIFI_REQ_SUCCESS(3,"图片验证成功"),
    VERIFI_REQ_FAIL(4,"图片验证失败");

    int code;
    String message;

    RetCode(int code, String message) {
        this.code = code;
        this.message = message;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
