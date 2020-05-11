package work.onss.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhoneEncryptedData implements Serializable {

    /**
     * phoneNumber : 13580006666
     * purePhoneNumber : 13580006666
     * countryCode : 86
     * watermark : {"appid":"APPID","timestamp":"TIMESTAMP"}
     */
    private String phoneNumber;
    private String purePhoneNumber;
    private String countryCode;
    private WatermarkEntity watermark;


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWatermark(WatermarkEntity watermark) {
        this.watermark = watermark;
    }

    public void setPurePhoneNumber(String purePhoneNumber) {
        this.purePhoneNumber = purePhoneNumber;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public WatermarkEntity getWatermark() {
        return watermark;
    }

    public String getPurePhoneNumber() {
        return purePhoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public static class WatermarkEntity {
        /**
         * appid : APPID
         * timestamp : TIMESTAMP
         */
        private String appid;
        private String timestamp;

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getAppid() {
            return appid;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
