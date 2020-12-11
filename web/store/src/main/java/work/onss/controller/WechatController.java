package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpTpXmlPackage;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.util.crypto.WxCpTpCryptUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.WxCpTpConfiguration;
import work.onss.config.WxCpTpProperties;
import work.onss.vo.Work;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * 营业员登录
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class WechatController {

    @Autowired
    private WxCpTpProperties wxCpTpProperties;
    @Autowired
    private WxCpTpConfiguration wxCpTpConfiguration;

    @GetMapping(value = {"wechat/{suiteid}"})
    public String wechat(
            @PathVariable String suiteid,
            @RequestParam(name = "msg_signature", required = false) String signature,
                               @RequestParam(name = "timestamp", required = false) String timestamp,
                               @RequestParam(name = "nonce", required = false) String nonce,
                               @RequestParam(name = "echostr", required = false) String echostr,
                               HttpServletRequest request) throws WxErrorException, IOException {
//        wxCpTpConfiguration.initServices();
//        List<WxCpTpProperties.AppConfig> appConfigs = wxCpTpProperties.getAppConfigs();
//        log.info("数据回调");
//        log.info("signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]", signature, timestamp, nonce, echostr);
//        WxCpTpService wxCpTpService = WxCpTpConfiguration.getCpTpService(suiteid);
//
//        BufferedReader reader = request.getReader();
//        StringBuffer buffer = new StringBuffer();
//        String line = " ";
//        while (null != (line = reader.readLine())) {
//            buffer.append(line);
//        }
//        String postData = buffer.toString();
//        String decryptMsgs = new WxCpTpCryptUtil(wxCpTpService.getWxCpTpConfigStorage()).decrypt(signature, timestamp, nonce, postData);
//        WxCpTpXmlPackage tpXmlPackage = WxCpTpXmlPackage.fromXml(decryptMsgs);

        return "success";
    }

}

