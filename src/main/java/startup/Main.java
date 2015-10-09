package startup;

import org.apache.commons.lang3.SystemUtils;
import utils.Application;

public class Main {
    public static void main(String[] args) {
        //TODO: ZACK - If a system property cannot be read due to security restrictions FIX
        Application.setApplication(".jd_home");
        System.out.println(Application.is64BitJvm());
        System.out.println(SystemUtils.OS_ARCH);
    }
}
