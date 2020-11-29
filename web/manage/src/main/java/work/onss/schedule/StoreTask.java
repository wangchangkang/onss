package work.onss.schedule;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import work.onss.service.MerchantService;

/**
 * 定时任务:同步微信审核特约商户进度及驳回原因
 *
 * @author wangchanghao
 */
@Log4j2
@Component
public class StoreTask {

    @Autowired
    private MerchantService merchantService;

    @Scheduled(cron = "0/5 * *  * * ? ")   //每5秒执行一次
    public void execute() {
    }

}
