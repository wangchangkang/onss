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

    private Work(Builder<T> builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.content = builder.content;
    }

    public static <T> Work.Builder<T> builder(T t) {
        return new Work.Builder<>(t);
    }

    public static class Builder<T> {
        private String code;
        private String msg;
        private final T content;

        public Builder(T content) {
            this.content = content;
        }

        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }

        public Builder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Work<T> build() {
            return new Work<>(this);
        }
    }


}
