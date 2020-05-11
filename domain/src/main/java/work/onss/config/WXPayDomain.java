package work.onss.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.stereotype.Component;

@Component
public class WXPayDomain implements IWXPayDomain {
    @Override
    public void report(String s, long l, Exception e) {
    }

    @Override
    public DomainInfo getDomain(WXPayConfig wxPayConfig) {
        return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
    }
}
