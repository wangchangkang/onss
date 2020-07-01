package work.onss.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public  class WXAddress {
    @NotEmpty(message = "缺少银行地址")
    @Size(min = 3, max = 3, message = "银行地址错误")
    private String[] value;
    @NotBlank(message = "缺少邮编")
    private String postcode;
    @NotEmpty(message = "缺少银行地址编号")
    @Size(min = 3, max = 3, message = "银行地址编号错误")
    private String[] code;
}
