package work.onss.vo;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@ToString
public class StoreInfo implements Serializable {

    @NotBlank(message = "简称不能为空")
    private String name;
    @NotBlank(message = "描述不能为空")
    private String description;
    @NotBlank(message = "地址不能为空")
    private String address;
    @NotBlank(message = "Logo不能为空")
    private String trademark;
    @NotBlank(message = "联系人姓名不能为空")
    private String username;
    @NotBlank(message = "联系方式不能为空")
    private String phone;
    @NotNull(message = "分类不能为空")
    private Integer type;
    private Point location;
    @Size(min = 1, max = 5, message = "图片数量限制{min}-{max}之间")
    private Collection<String> pictures = new ArrayList<>();
    @Size(min = 1, max = 5, message = "视频数量限制{min}-{max}之间")
    private Collection<String> videos = new ArrayList<>();


    public void setPictures(String pictures) {
        this.pictures = Arrays.asList(pictures.split(","));
    }

    public void setVideos(String videos) {
        this.videos = Arrays.asList(videos.split(","));
    }

    public void setLocation(String location) {
        double[] doubles = Arrays.stream(location.split(",")).mapToDouble(Double::parseDouble).toArray();
        this.location = new GeoJsonPoint(doubles[0], doubles[1]);
    }
}
