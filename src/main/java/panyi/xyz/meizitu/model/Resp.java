package panyi.xyz.meizitu.model;

public class Resp<T> {
    private int code;
    private String msg;
    private T data;

    public static Resp<String> genError( String message){
        Resp<String> resp = new Resp<String>(Codes.ERROR , message);
        return resp;
    }

    public static <T> Resp<T> genResp( T data){
        Resp<T> resp = new Resp<T>(Codes.SUCCESS );
        resp.setData(data);
        return resp;
    }

    public Resp(int code) {
        this.code = code;
    }

    public Resp(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Resp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
