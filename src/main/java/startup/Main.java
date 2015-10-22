package startup;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Application;

public class Main {
    public static void main(String[] args) {
        Logger logger = LogManager.getRootLogger();
        //TODO: ZACK - If a system property cannot be read due to security restrictions FIX
        Application.setApplication(".jd_home");
        logger.error(Application.getApplicationRoot().toString());
//        Log.log.info(Application.getApplicationRoot().toString());
        System.out.println(SystemUtils.OS_ARCH);
    }
}
