package utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewLog {

    // Define a static logger variable so that it references the
    // Logger instance named "NewLog".
    private static final Logger logger = LogManager.getLogger(NewLog.class);

    public static void main(String... args) {

        // Set up a simple configuration that logs on the console.

        logger.trace("Entering application.");
        Bar bar = new Bar();
        if (!bar.doIt()) {
            logger.error("Didn't do it.");
        }
        logger.trace("Exiting application.");
    }
}