package work.onss.service;

import work.onss.vo.WXSession;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface MiniProgramService {

    Map<String, String> token(String appid, String secret);

    WXSession jscode2session(String appid, String secret, String code);

    Map<String, String> bizlicense(String imgUrl, String accessToken);

    Map<String, String> bizlicense(Path path, String accessToken) throws IOException;
}
