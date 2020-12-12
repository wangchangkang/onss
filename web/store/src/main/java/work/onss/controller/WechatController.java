package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.cp.bean.WxCpTpXmlPackage;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.util.crypto.WxCpTpCryptUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.WxCpTpConfiguration;
import work.onss.config.WxCpTpProperties;
import work.onss.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    @RequestMapping(value = {"wechat/{suiteid}"}, produces = {"text/xml"})
    public String wechat(
            @PathVariable String suiteid,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr,
            HttpServletRequest request) throws IOException {
        wxCpTpConfiguration.initServices();

        log.info("数据回调");
        log.info("signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]", signature, timestamp, nonce, echostr);
        WxCpTpService wxCpTpService = WxCpTpConfiguration.getCpTpService(suiteid);
        WxCpTpConfigStorage wxCpTpConfigStorage = wxCpTpService.getWxCpTpConfigStorage();

        log.info("数据回调");
        log.info("signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]", signature, timestamp, nonce, echostr);

        String xml = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());

        if (StringUtils.hasLength(xml)) {
            try {
                log.info("xml:{}", xml);
                String decryptMsgs = new WxCpTpCryptUtil(wxCpTpConfigStorage).decrypt(signature, timestamp, nonce, xml);
                log.info("数据回调-解密后的xml数据:{}", decryptMsgs);
                WxCpTpXmlPackage tpXmlPackage = WxCpTpXmlPackage.fromXml(decryptMsgs);
                log.info(JsonUtils.toJson(tpXmlPackage));
                wxCpTpService.setSuiteTicket(tpXmlPackage.getAllFieldsMap().get("SuiteTicket").toString());
                String suiteAccessToken = wxCpTpService.getSuiteAccessToken(false);
                log.info("suiteAccessToken:{}",suiteAccessToken);
                wxCpTpService.setWxCpTpConfigStorage(wxCpTpConfigStorage);
                WxCpTpConfiguration.setCpTpService(suiteid, wxCpTpService);
                return "success";
            } catch (Exception e) {
                log.error("校验失败：{}", e.getMessage());
                return "success";
            }
        }

        try {
            if (wxCpTpService.checkSignature(signature, timestamp, nonce, echostr)) {
                String decrypt = new WxCpTpCryptUtil(wxCpTpConfigStorage).decrypt(echostr);
                log.info("数据回调-解密后的xml数据:{}", decrypt);
                return decrypt;
            }
        } catch (Exception e) {
            log.error("校验签名失败：{}", e.getMessage());
        }
        return "success";
    }

}

