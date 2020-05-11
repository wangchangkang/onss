package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@Document
public class Score implements Serializable {

    @Id
    private String id;
    private String uid;
    private String sid;
    private Integer status;
    private String total;
    private String difference;
    private List<Item> items;
    @Indexed(unique = true)
    private String outTradeNo1;
    @Indexed(unique = true)
    private String outTradeNo2;
    @Indexed(unique = true)
    private String prepayId1;
    @Indexed(unique = true)
    private String transaction_id1;
    @Indexed(unique = true)
    private String prepayId2;
    @Indexed(unique = true)
    private String transaction_id2;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime insertTime;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updateTime;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime payTime1;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime payTime2;

    private String phone;
    private String username;
    private String address;
    private Integer tag;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2D)
    private double[] point;

    public Score(String uid, String sid, String total, List<Item> items, String outTradeNo1, String outTradeNo2, LocalDateTime insertTime, Address address) {
        this.uid = uid;
        this.sid = sid;
        this.total = total;
        this.items = items;
        this.outTradeNo1 = outTradeNo1;
        this.outTradeNo2 = outTradeNo2;


        this.insertTime = insertTime;

        this.phone = address.getPhone();
        this.username = address.getUsername();
        this.address = address.getDetail();
        this.tag = address.getTag();
        this.point = address.getPoint();

        this.status = 0;
        this.difference = "0.00";
        this.prepayId1 = null;
        this.prepayId2 = null;
        this.payTime1 = null;
        this.payTime2 = null;
        this.transaction_id1 = null;
        this.transaction_id2 = null;
    }
}
