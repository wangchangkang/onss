package work.onss.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangchanghao
 */
@Data
public class Work<T> implements Serializable {

    private String code;
    private String msg;
    private T content;

    public Work(String code, String msg, T t) {
        this.code = code;
        this.msg = msg;
        this.content = t;
    }

    public static <T> Work<T> success() {
        return new Work<>("success", "操作成功", null);
    }

    public static <T> Work<T> success(T t) {
        return new Work<>("success", "操作成功", t);
    }

    public static <T> Work<T> success(String msg, T t) {
        return new Work<>("success", msg, t);
    }

    public static <T> Work<T> fail() {
        return new Work<>("fail", "操作失败", null);
    }

    public static <T> Work<T> fail(String msg) {
        return new Work<>("fail", msg, null);
    }

    public static <T> Work<T> fail(String code,String msg) {
        return new Work<>(code, msg, null);
    }

    public static <T> Work<T> fail(T t) {
        return new Work<>("fail", "操作失败", t);
    }

    public static <T> Work<T> fail(String msg, T t) {
        return new Work<>("fail", msg, t);
    }

    public static <T> Work<T> message(String code, String msg, T t) {
        return new Work<>(code, msg, t);
    }

}
