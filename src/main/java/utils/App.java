package utils;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.exceptions.WTFException;
import utils.logging.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.regex.Pattern;

public final class App {
    public static final Logger log = LogManager.getRootLogger();
    private static String root;
    private static Boolean isJared;

    // string holders
    private static String appFolder = ".appwork";
    private static final String APPLICATION_ROOT = "Application Root: ";

    // pattern holders
    private static final Pattern PATTERN = Pattern.compile("jar\\:.*\\.(jar|exe)\\!.*");
    private static final Pattern COMPILE = Pattern.compile("\\.");


    public static String getHome() {
        return root == null ? getRoot(Application.class) : root;
    }

    /**
     * sets current Application Folder and Jar ID. MUST BE SET at startup! Can only be set once!
     *
     * @param newAppFolder the desired Application Folder
     */
    public static void setApplication(String newAppFolder) {
        root = null;
        appFolder = newAppFolder;
    }

    /**
     * returns the url for the resource. if The resource can be found in classpath, it will be returned. otherwise the function will return
     * the fileurl to current wprkingdirectory
     */
    public static URL getResourceURL(String relative) {
        return getResourceURL(relative, true);
    }

    /**
     * Returns the Resource url for relative.
     * <p>
     * NOTE:this function only returns URL's that really exists!
     * <p>
     * if preferClassPath is true:
     * <p>
     * we first check if there is a ressource available inside current classpath, for example inside the jar itself. if no such URL exists
     * we check for file in local filesystem
     * <p>
     * if preferClassPath if false:
     * <p>
     * first check local filesystem, then inside classpath
     */
    public static URL getResourceURL(String relative, boolean preferClasspath) {
        try {

            if (relative == null) {
                return null;
            }
            if (relative.startsWith("/") || relative.startsWith("\\")) {
                throw new WTFException("getResourceURL only works with relative paths.");
            }
            if (preferClasspath) {

                URL res = Application.class.getClassLoader().getResource(relative);
                if (res != null) {
                    return res;
                }
                File file = new File(getHome(), relative);
                if (!file.exists()) {
                    return null;
                }
                return file.toURI().toURL();

            } else {
                File file = new File(getHome(), relative);
                if (file.exists()) {
                    return file.toURI().toURL();
                }

                URL res = Application.class.getClassLoader().getResource(relative);
                if (res != null) {
                    return res;
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Detects the applications home directory. it is either the pass of the appworkutils.jar or HOME/
     */
    public static String getRoot(Class<?> rootOfClass) {

        if (root == null) {
            String key = "awuhome" + appFolder;
            String sysProp = System.getProperty(key);
            if (sysProp == null) {
                if (isJared(rootOfClass)) {
                    // this is the jar file

                    URL loc = rootOfClass.getProtectionDomain().getCodeSource().getLocation();

                    try {
                        String path = loc.getPath();
                        // loc may be a
                        File appRoot = null;
                        try {
                            appRoot = new File(URLDecoder.decode(path, "UTF-8"));
                            if (!appRoot.exists()) {
                                appRoot = null;
                            }
                        } catch (UnsupportedEncodingException e) {
                            Log.exception(e);
                        }
                        if (appRoot == null) {
                            appRoot = new File(path);
                            if (!appRoot.exists()) {
                                appRoot = null;
                            }
                        }
                        if (appRoot == null) {
                            appRoot = new File(loc.toURI());
                            if (!appRoot.exists()) {
                                appRoot = null;
                            }
                        }
                        if (appRoot == null) {
                            throw new URISyntaxException(String.valueOf(loc), "Bad URI");
                        }
                        if (appRoot.isFile()) {
                            appRoot = appRoot.getParentFile();
                        }
                        root = appRoot.getAbsolutePath();
                        log.info(APPLICATION_ROOT + root + " (jared) " + rootOfClass);
                    } catch (URISyntaxException e) {
                        Log.exception(e);
                        root = SystemUtils.getUserHome() + SystemUtils.FILE_SEPARATOR + appFolder + SystemUtils.FILE_SEPARATOR;
                        log.warn(APPLICATION_ROOT + root + " (jared but error) " + rootOfClass);
                    }
                } else {
                    root = SystemUtils.getUserHome() + SystemUtils.FILE_SEPARATOR + appFolder;
                    log.info(APPLICATION_ROOT + root + " (DEV) " + rootOfClass);
                }
                return root;
            } else {
                root = sysProp;
                return root;
            }
        } else {
            return root;
        }
    }

    public static boolean isJared(Class<?> clazz) {
        if (isJared == null) {
            String name = getClass(clazz);
            ClassLoader cll = App.class.getClassLoader();
            if (cll == null) {
                log.fatal("getContextClassLoader() is null");
                isJared = Boolean.TRUE;
                return true;
            }
            URL caller = cll.getResource(name);

            if (caller == null) {
                isJared = false;
                return false;
            }
            isJared = PATTERN.matcher(caller.toString()).matches();
            return isJared;
        } else {
            return Objects.equals(isJared, Boolean.TRUE);
        }
    }

    private static String getClass(Class<?> clazz) {
        if (clazz == null) {
            clazz = Application.class;
        }
        return COMPILE.matcher(clazz.getName()).replaceAll("/") + ".class";
    }
}
