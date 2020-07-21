package work.onss.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum SettlementEnum implements Serializable {

    /**
     * 餐饮
     */
    SUBJECT_TYPE_INDIVIDUAL0(719, "餐饮"),
    SUBJECT_TYPE_INDIVIDUAL1(719, "食品生鲜"),
    SUBJECT_TYPE_INDIVIDUAL2(719, "私立/民营医院/诊所"),
    SUBJECT_TYPE_INDIVIDUAL3(719, "保健器械/医疗器械/非处方药品"),
    SUBJECT_TYPE_INDIVIDUAL4(719, "游艺厅/KTV/网吧"),
    SUBJECT_TYPE_INDIVIDUAL5(719, "机票/机票代理"),
    SUBJECT_TYPE_INDIVIDUAL6(719, "宠物医院"),
    SUBJECT_TYPE_INDIVIDUAL7(719, "培训机构"),
    SUBJECT_TYPE_INDIVIDUAL8(719, "零售批发/生活娱乐/其他"),
    SUBJECT_TYPE_INDIVIDUAL9(720, "话费通讯"),
    SUBJECT_TYPE_INDIVIDUAL10(746, "门户论坛/网络广告及推广/软件开发/其他"),
    SUBJECT_TYPE_INDIVIDUAL11(746, "游戏"),
    SUBJECT_TYPE_INDIVIDUAL12(721, "加油"),

    SUBJECT_TYPE_ENTERPRISE0(716, "餐饮"),
    SUBJECT_TYPE_ENTERPRISE1(716, "食品生鲜"),
    SUBJECT_TYPE_ENTERPRISE2(716, "私立/民营医院/诊所"),
    SUBJECT_TYPE_ENTERPRISE3(716, "保健器械/医疗器械/非处方药品"),
    SUBJECT_TYPE_ENTERPRISE4(716, "游艺厅/KTV/网吧"),
    SUBJECT_TYPE_ENTERPRISE5(716, "机票/机票代理"),
    SUBJECT_TYPE_ENTERPRISE6(716, "宠物医院"),
    SUBJECT_TYPE_ENTERPRISE7(716, "培训机构"),
    SUBJECT_TYPE_ENTERPRISE8(716, "零售批发/生活娱乐/其他"),
    SUBJECT_TYPE_ENTERPRISE9(716, "电信运营商/宽带收费"),
    SUBJECT_TYPE_ENTERPRISE10(716, "旅行社"),
    SUBJECT_TYPE_ENTERPRISE11(716, "宗教组织"),
    SUBJECT_TYPE_ENTERPRISE12(716, "房地产/房产中介"),
    SUBJECT_TYPE_ENTERPRISE13(716, "共享服务"),
    SUBJECT_TYPE_ENTERPRISE14(716, "文物经营/文物复制品销售"),
    SUBJECT_TYPE_ENTERPRISE15(716, "拍卖典当"),
    SUBJECT_TYPE_ENTERPRISE16(715, "保险业务"),
    SUBJECT_TYPE_ENTERPRISE17(714, "众筹"),
    SUBJECT_TYPE_ENTERPRISE18(713, "财经/股票类资讯"),
    SUBJECT_TYPE_ENTERPRISE19(728, "话费通讯"),
    SUBJECT_TYPE_ENTERPRISE20(728, "婚介平台/就业信息平台/其他"),
    SUBJECT_TYPE_ENTERPRISE21(711, "在线图书/视频/音乐/网络直播"),
    SUBJECT_TYPE_ENTERPRISE22(711, "游戏"),
    SUBJECT_TYPE_ENTERPRISE23(711, "门户论坛/网络广告及推广/软件开发/其他"),
    SUBJECT_TYPE_ENTERPRISE24(717, "物流/快递"),
    SUBJECT_TYPE_ENTERPRISE25(717, "加油"),
    SUBJECT_TYPE_ENTERPRISE26(717, "民办中小学及幼儿园"),
    SUBJECT_TYPE_ENTERPRISE27(730, "公共事业（水电煤气）"),
    SUBJECT_TYPE_ENTERPRISE28(718, "信用还款"),
    SUBJECT_TYPE_ENTERPRISE29(739, "民办大学及院校"),

    SUBJECT_TYPE_INSTITUTIONS0(725, "其他缴费"),
    SUBJECT_TYPE_INSTITUTIONS1(722, "公共事业（水电煤气）"),
    SUBJECT_TYPE_INSTITUTIONS2(723, "交通罚款"),
    SUBJECT_TYPE_INSTITUTIONS3(724, "公立医院"),
    SUBJECT_TYPE_INSTITUTIONS4(724, "公立学校"),
    SUBJECT_TYPE_INSTITUTIONS5(724, "挂号平台"),

    SUBJECT_TYPE_OTHERS0(727, "宗教组织"),
    SUBJECT_TYPE_OTHERS1(727, "机票/机票代理"),
    SUBJECT_TYPE_OTHERS2(727, "私立/民营医院/诊所"),
    SUBJECT_TYPE_OTHERS3(727, "咨询/娱乐票务/其他"),
    SUBJECT_TYPE_OTHERS4(738, "民办中小学及幼儿园"),
    SUBJECT_TYPE_OTHERS5(726, "民办大学及院校"),
    SUBJECT_TYPE_OTHERS6(726, "公益"),
    ;


    SettlementEnum(int number, String description) {
        this.code = number;
        this.description = description;
    }

    private int code;
    private String description;
}
