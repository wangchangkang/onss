package work.onss.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum SettlementEnum implements Serializable {

    /**
     * 餐饮
     */
    INDIVIDUAL0(719, "餐饮"),
    INDIVIDUAL1(719, "食品生鲜"),
    INDIVIDUAL2(719, "私立/民营医院/诊所"),
    INDIVIDUAL3(719, "保健器械/医疗器械/非处方药品"),
    INDIVIDUAL4(719, "游艺厅/KTV/网吧"),
    INDIVIDUAL5(719, "机票/机票代理"),
    INDIVIDUAL6(719, "宠物医院"),
    INDIVIDUAL7(719, "培训机构"),
    INDIVIDUAL8(719, "零售批发/生活娱乐/其他"),
    INDIVIDUAL9(720, "话费通讯"),
    INDIVIDUAL10(746, "门户论坛/网络广告及推广/软件开发/其他"),
    INDIVIDUAL11(746, "游戏"),
    INDIVIDUAL12(721, "加油"),

    ENTERPRISE0(716, "餐饮"),
    ENTERPRISE1(716, "食品生鲜"),
    ENTERPRISE2(716, "私立/民营医院/诊所"),
    ENTERPRISE3(716, "保健器械/医疗器械/非处方药品"),
    ENTERPRISE4(716, "游艺厅/KTV/网吧"),
    ENTERPRISE5(716, "机票/机票代理"),
    ENTERPRISE6(716, "宠物医院"),
    ENTERPRISE7(716, "培训机构"),
    ENTERPRISE8(716, "零售批发/生活娱乐/其他"),
    ENTERPRISE9(716, "电信运营商/宽带收费"),
    ENTERPRISE10(716, "旅行社"),
    ENTERPRISE11(716, "宗教组织"),
    ENTERPRISE12(716, "房地产/房产中介"),
    ENTERPRISE13(716, "共享服务"),
    ENTERPRISE14(716, "文物经营/文物复制品销售"),
    ENTERPRISE15(716, "拍卖典当"),
    ENTERPRISE16(715, "保险业务"),
    ENTERPRISE17(714, "众筹"),
    ENTERPRISE18(713, "财经/股票类资讯"),
    ENTERPRISE19(728, "话费通讯"),
    ENTERPRISE20(728, "婚介平台/就业信息平台/其他"),
    ENTERPRISE21(711, "在线图书/视频/音乐/网络直播"),
    ENTERPRISE22(711, "游戏"),
    ENTERPRISE23(711, "门户论坛/网络广告及推广/软件开发/其他"),
    ENTERPRISE24(717, "物流/快递"),
    ENTERPRISE25(717, "加油"),
    ENTERPRISE26(717, "民办中小学及幼儿园"),
    ENTERPRISE27(730, "公共事业（水电煤气）"),
    ENTERPRISE28(718, "信用还款"),
    ENTERPRISE29(739, "民办大学及院校"),

    INSTITUTIONS0(725, "其他缴费"),
    INSTITUTIONS1(722, "公共事业（水电煤气）"),
    INSTITUTIONS2(723, "交通罚款"),
    INSTITUTIONS3(724, "公立医院"),
    INSTITUTIONS4(724, "公立学校"),
    INSTITUTIONS5(724, "挂号平台"),

    OTHERS0(727, "宗教组织"),
    OTHERS1(727, "机票/机票代理"),
    OTHERS2(727, "私立/民营医院/诊所"),
    OTHERS3(727, "咨询/娱乐票务/其他"),
    OTHERS5(738, "民办中小学及幼儿园"),
    OTHERS6(726, "民办大学及院校"),
    OTHERS7(726, "公益"),
    ;


    SettlementEnum(int number, String description) {
        this.code = number;
        this.description = description;
    }

    private int code;
    private String description;
}
