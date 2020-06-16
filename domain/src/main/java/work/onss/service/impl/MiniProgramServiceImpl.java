package work.onss.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import work.onss.service.MiniProgramService;
import work.onss.utils.Utils;
import work.onss.vo.WXSession;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class MiniProgramServiceImpl implements MiniProgramService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<String, String> token(String appid, String secret) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("secret", secret);
        String seesion = restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}", String.class, map);
        Map<String, String> data = Utils.fromJson(seesion);
        return data;
    }

    @Override
    public WXSession jscode2session(String appid, String secret, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("secret", secret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String seesion = restTemplate.getForObject("https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}", String.class, map);
        return Utils.fromJson(seesion, WXSession.class);
    }

    @Override
    public Map<String, String> bizlicense(String imgUrl, String accessToken) {
        Map<String, String> map = new HashMap<>();
        map.put("img_url", imgUrl);
        map.put("access_token", accessToken);
        String seesion = restTemplate.postForObject("https://api.weixin.qq.com/cv/ocr/bizlicense?img_url={img_url}&access_token={access_token}", null, String.class, map);
        Map<String, String> data = Utils.fromJson(seesion);
        log.info(Utils.toJson(data));
        return data;
    }

    @Override
    public Map<String, String> bizlicense(Path path, String accessToken) throws IOException {
        log.info(path.toString());
        log.info(accessToken);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("img", path.toFile());
        String result = restTemplate.postForObject("https://api.weixin.qq.com/cv/ocr/bizlicense?access_token=".concat(accessToken), param, String.class);
        Map<String, String> data = Utils.fromJson(result);
        return data;
    }

//
}
