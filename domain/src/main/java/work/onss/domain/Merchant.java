package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import work.onss.vo.WXAddress;
import work.onss.vo.wx.SpeciallyMerchant;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
@NoArgsConstructor
@ToString
public class Merchant implements Serializable {

    @NotBlank(message = "服务商小程序APPID不能为空")
    private String miniProgramSubAppid; // 服务商小程序APPID

    //超级管理员
    @NotBlank(message = "请填写管理员姓名")
    private String contactName;//姓名
    @NotBlank(message = "请填写管理员18位身份证号")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "管理员身份证格式错误")
    private String contactIdNumber;//身份证
    @NotBlank(message = "请填写管理员手机号")
    @Pattern(regexp = "^[1][34578][0-9]{9}$", message = "管理员手机号格式错误")
    private String mobilePhone;//手机号
    @NotBlank(message = "请填写常用邮箱")
    @Email(message = "常用邮箱格式错误")
    private String contactEmail;//常用邮箱

    //营业执照
    @NotNull(message = "请选择主体类型")
    private SpeciallyMerchant.SubjectEnum subjectType;//主体类型
    @NotBlank(message = "请上传营业执照副本")
    private String licenseCopy;//五证合一
    @NotBlank(message = "请填写营业执照编号")
    @Pattern(regexp = "[^_IOZSVa-z\\W]{2}\\d{6}[^_IOZSVa-z\\W]{10}", message = "营业执照编号格式错误")
    private String licenseNumber;//社会信用代码
    @NotBlank(message = "请填写主体全称")
    private String merchantName;//商户全称
    @NotBlank(message = "请填写经营者姓名")
    private String legalPerson;//经营者姓名

    //法人信息
    private SpeciallyMerchant.CardEnum idDocType = SpeciallyMerchant.CardEnum.IDENTIFICATION_TYPE_IDCARD;//法人证件类型
    @NotBlank(message = "请上传法人身份证正面")
    private String idCardCopy;//身份证正面
    @NotBlank(message = "请填写管理员18位身份证号")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "管理员身份证格式错误")
    private String idCardNumber;//身份证号
    @NotBlank(message = "请填写管理员姓名")
    private String idCardName;//姓名
    @NotBlank(message = "请上传法人身份证反面")
    private String idCardNational;//身份证反面
    @NotBlank(message = "请填写法人身份证注册日期")
    private String cardPeriodBegin;//开始时间
    @NotBlank(message = "请填写法人身份证过期日期")
    private String cardPeriodEnd;//结束时间
    @NotNull(message = "法人是否是最终受益人")
    private Boolean owner;//是否是最终受益人

    private SpeciallyMerchant.CardEnum idType = SpeciallyMerchant.CardEnum.IDENTIFICATION_TYPE_IDCARD;//证件类型
    private String idCardA;//身份证人像面照片
    private String idCardB;//身份证国徽面照片
    private String idDocCopy;//证件照片
    private String beneficiary;//受益人姓名
    private String idNumber;//证件号码
    private String idPeriodBegin;//证件有效期开始时间
    private String idPeriodEnd;//证件有效期结束时间

    //经营资料
    @NotBlank(message = "请填写商户简称")
    private String merchantShortname;//商户简称
    @NotBlank(message = "请填写客服电话")
    private String servicePhone;//客服电话

    private List<String> miniProgramPics = new ArrayList<>(0);//小程序截图

    //结算规则
    private String settlementId;//入驻结算规则ID
    @NotNull(message = "请选择所属行业")
    private String qualificationType;//所属行业
    @Size(min = 1, max = 5, message = "请上传1~5张特殊资质图片")
    private List<String> qualifications = new ArrayList<>(0);//特殊资质图片

    //结算银行账户
    @NotNull(message = "请填写账户类型")
    private SpeciallyMerchant.BankAccountEnum bankAccountType;//账户类型 个人 公户
    @NotBlank(message = "请填写开户名称")
    private String accountName;//开户名称
    @NotNull(message = "请选择开户银行")
    private String accountBank;//开户银行
    @Valid
    private WXAddress bankAddress;//开户地址
    @NotBlank(message = "请填写银行账号")
    private String accountNumber;//银行账号
    private String bankName;//其他银行，需填写银行全称
}
