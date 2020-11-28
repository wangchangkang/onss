
package work.onss.vo.wx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Log4j2
@Data
@NoArgsConstructor
public class SpeciallyMerchant implements Serializable {

    private String businessCode;//业务申请编号
    private ContactInfo contactInfo;//超级管理员信息
    private SubjectInfo subjectInfo;//主体资料
    private BusinessInfo businessInfo;//经营资料
    private SettlementInfo settlementInfo;//结算规则
    private BankAccountInfo bankAccountInfo;//结算银行账户
    private AdditionInfo additionInfo;//补充材料

    public SpeciallyMerchant(String businessCode, ContactInfo contactInfo, SubjectInfo subjectInfo, BusinessInfo businessInfo, SettlementInfo settlementInfo, BankAccountInfo bankAccountInfo) {
        this.businessCode = businessCode;
        this.contactInfo = contactInfo;
        this.subjectInfo = subjectInfo;
        this.businessInfo = businessInfo;
        this.settlementInfo = settlementInfo;
        this.bankAccountInfo = bankAccountInfo;
    }

    /**
     * SUBJECT_TYPE_INDIVIDUAL（个体户）：营业执照上的主体类型一般为个体户、个体工商户、个体经营；
     * SUBJECT_TYPE_ENTERPRISE（企业）：营业执照上的主体类型一般为有限公司、有限责任公司；
     * SUBJECT_TYPE_INSTITUTIONS（党政、机关及事业单位）：包括国内各级、各类政府机构、事业单位等（如：公安、党团、司法、交通、旅游、工商税务、市政、医疗、教育、学校等机构）；
     * SUBJECT_TYPE_OTHERS（其他组织）：不属于企业、政府/事业单位的组织机构（如社会团体、民办非企业、基金会），要求机构已办理组织机构代码证
     */
    @Getter
    public enum SubjectEnum implements Serializable {
        SUBJECT_TYPE_INDIVIDUAL, SUBJECT_TYPE_ENTERPRISE, SUBJECT_TYPE_INSTITUTIONS, SUBJECT_TYPE_OTHERS
    }

    /**
     * IDENTIFICATION_TYPE_IDCARD：中国大陆居民-身份证
     * IDENTIFICATION_TYPE_OVERSEA_PASSPORT：其他国家或地区居民-护照
     * IDENTIFICATION_TYPE_HONGKONG_PASSPORT：中国香港居民-来往内地通行证
     * IDENTIFICATION_TYPE_MACAO_PASSPORT：中国澳门居民-来往内地通行证
     * IDENTIFICATION_TYPE_TAIWAN_PASSPORT：中国台湾居民-来往大陆通行证
     */
    @Getter
    public enum CardEnum implements Serializable {
        IDENTIFICATION_TYPE_IDCARD,
        IDENTIFICATION_TYPE_OVERSEA_PASSPORT,
        IDENTIFICATION_TYPE_HONGKONG_PASSPORT,
        IDENTIFICATION_TYPE_MACAO_PASSPORT,
        IDENTIFICATION_TYPE_TAIWAN_PASSPORT,
    }

    /**
     * BANK_ACCOUNT_TYPE_CORPORATE：对公银行账户
     * BANK_ACCOUNT_TYPE_PERSONAL：经营者个人银行卡
     */
    @Getter
    @AllArgsConstructor
    public enum BankAccountEnum implements Serializable {
        BANK_ACCOUNT_TYPE_CORPORATE, BANK_ACCOUNT_TYPE_PERSONAL
    }

    /**
     * CERTIFICATE_TYPE_2388：事业单位法人证书
     * CERTIFICATE_TYPE_2389：统一社会信用代码证书
     * CERTIFICATE_TYPE_2390：有偿服务许可证（军队医院适用）
     * CERTIFICATE_TYPE_2391：医疗机构执业许可证（军队医院适用）
     * CERTIFICATE_TYPE_2392：企业营业执照（挂靠企业的党组织适用）
     * CERTIFICATE_TYPE_2393：组织机构代码证（政府机关适用）
     * CERTIFICATE_TYPE_2394：社会团体法人登记证书
     * CERTIFICATE_TYPE_2395：民办非企业单位登记证书
     * CERTIFICATE_TYPE_2396：基金会法人登记证书
     * CERTIFICATE_TYPE_2397：慈善组织公开募捐资格证书
     * CERTIFICATE_TYPE_2398：农民专业合作社法人营业执照
     * CERTIFICATE_TYPE_2399：宗教活动场所登记证
     * CERTIFICATE_TYPE_2400：其他证书/批文/证明
     */
    @Getter
    public enum CertEnum implements Serializable {
        CERTIFICATE_TYPE_2388,
        CERTIFICATE_TYPE_2389,
        CERTIFICATE_TYPE_2390,
        CERTIFICATE_TYPE_2391,
        CERTIFICATE_TYPE_2392,
        CERTIFICATE_TYPE_2393,
        CERTIFICATE_TYPE_2394,
        CERTIFICATE_TYPE_2395,
        CERTIFICATE_TYPE_2396,
        CERTIFICATE_TYPE_2397,
        CERTIFICATE_TYPE_2398,
        CERTIFICATE_TYPE_2399,
        CERTIFICATE_TYPE_2400
    }

    /**
     * 线下门店：SALES_SCENES_STORE
     * 公众号：SALES_SCENES_MP
     * 小程序：SALES_SCENES_MINI_PROGRAM
     * 互联网：SALES_SCENES_WEB
     * APP：SALES_SCENES_APP
     * 企业微信：SALES_SCENES_WEWORK
     */
    @Getter
    public enum SalesScenesEnum {
        SALES_SCENES_STORE,SALES_SCENES_MP,SALES_SCENES_MINI_PROGRAM,SALES_SCENES_WEB,SALES_SCENES_APP,SALES_SCENES_WEWORK
    }

    /**
     * 工商银行
     * 交通银行
     * 招商银行
     * 民生银行
     * 中信银行
     * 浦发银行
     * 兴业银行
     * 光大银行
     * 广发银行
     * 平安银行
     * 北京银行
     * 华夏银行
     * 农业银行
     * 建设银行
     * 邮政储蓄银行
     * 中国银行
     * 宁波银行
     * 其他银行
     */
    public enum BankEnum{
        ICBC,
        BANKCOMM,
        CMBCHINA,
        CMBC,
        CITICBANK,
        SPDB,
        CIB,
        CEBBANK,
        CGBCHINA,
        PINGAN,
        BANKOFBEIJING,
        HXB,
        ABCHINA,
        CCB,
        PSBC,
        BOC,
        NBCB,
        OTHER
    }

}
