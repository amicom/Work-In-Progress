/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p>
 * This file is part of org.appwork.utils
 * <p>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils;

import utils.exceptions.WTFException;
import utils.logging.Log;
import utils.logging.LogInterface;
import utils.os.CrossSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Application utils provide statis helper functions concerning the applications System integration
 *
 * @author $Author: unknown$
 */
public class Application {

    public static final long JAVA16 = 16000000L;
    public static final long JAVA17 = 17000000L;
    public static final long JAVA18 = 18000000L;
    private static final char OLD_CHAR = '.';
    private static final char NEW_CHAR = '/';
    private static final Pattern COMPILE = Pattern.compile("\\.");
    private static final String NO_JAR_FOUND = "No JarName Found";
    private static final String APPLICATION_ROOT = "Application Root: ";
    private static final String FILE_SEPARATOR = "file.separator";
    private static final Pattern PATTERN = Pattern.compile("jar\\:.*\\.(jar|exe)\\!.*");
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("[^\\d]");
    private static Boolean IS_JARED;
    private static String appFolder = ".appwork";
    private static String ROOT;
    private static int javaVersion;
    private static Boolean JVM64BIT;
    private static File TEMP;


    public static String getApplication() {
        return appFolder;
    }

    /**
     * sets current Application Folder and Jar ID. MUST BE SET at startup! Can only be set once!
     *
     * @param newAppFolder the desired Application Folder
     */
    public static void setApplication(String newAppFolder) {
        ROOT = null;
        appFolder = newAppFolder;
    }

    /**
     * @return the root location of the application
     */
    public static File getApplicationRoot() {
        return getRootByClass(Application.class, null);
    }

    public static String getHome() {
        return getRoot(Application.class);
    }

    public static String getPackagePath(Class<?> class1) {
        // TODO Auto-generated method stub
        return class1.getPackage().getName().replace(OLD_CHAR, NEW_CHAR) + NEW_CHAR;
    }


    public static URL getHomeURL() {

        try {
            return new File(getHome()).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new WTFException(e);
        }
    }

    // returns the jar filename of clazz
    public static File getJarFile(Class<?> clazz) {
        URL url = getResourceURL(getClass(clazz));
        String path = url.getPath();
        Log.L.info(String.valueOf(url));
        int index = path.indexOf(".jar!");
        //noinspection CallToStringEquals
        if (!"jar".equals(url.getProtocol()) || index < 0) {
            throw new WTFException("Works in Jared mode only");
        }
        try {
            return new File(new URL(path.substring(0, index + 4)).toURI());
        } catch (MalformedURLException | URISyntaxException e) {
            Log.exception(Level.WARNING, e);
        }
        return null;
    }

    public static String getJarName(Class<?> clazz) {

        String name = getClass(clazz);
        String url = getResourceURL(name).toString();

        int index = url.indexOf(".jar!");
        if (index < 0) {
            throw new IllegalStateException(NO_JAR_FOUND);
        }
        try {
            return new File(new URL(url.substring(4, index + 4)).toURI()).getName();
        } catch (MalformedURLException | URISyntaxException | RuntimeException ignored) {
        }
        throw new IllegalStateException(NO_JAR_FOUND);
    }

    public static int getJavaVersion() {
        if (javaVersion > 0) {
            return javaVersion;
        }
        try {
            /* this version info contains more information */
            String version = System.getProperty("java.runtime.version");
            int ver = Integer.parseInt(DIGITS_ONLY_PATTERN.matcher(version).replaceAll(""));
            javaVersion = ver;
            return ver;
        } catch (NumberFormatException e) {
            Log.exception(e);
            return -1;
        }
    }

    /**
     * Returns a ressourcefile relative to the instaldirectory
     */
    public static File getResource(String relative) {
        return new File(getHome(), relative);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Detects the applications home directory. it is either the pass of the appworkutils.jar or HOME/
     */
    private static String getRoot(Class<?> rootOfClass) {

        if (ROOT != null) {
            return ROOT;
        }
        String key = "awuhome" + appFolder;
        String sysProp = System.getProperty(key);
        if (sysProp != null) {

            ROOT = sysProp;
            return ROOT;
        }
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
                ROOT = appRoot.getAbsolutePath();
                Log.L.info(APPLICATION_ROOT + ROOT + " (jared) " + rootOfClass);
            } catch (URISyntaxException e) {
                Log.exception(e);
                ROOT = System.getProperty("user.home") + System.getProperty(FILE_SEPARATOR) + appFolder + System.getProperty(FILE_SEPARATOR);
                Log.L.warning(APPLICATION_ROOT + ROOT + " (jared but error) " + rootOfClass);
            }
        } else {
            ROOT = System.getProperty("user.home") + System.getProperty(FILE_SEPARATOR) + appFolder;

            Log.L.info(APPLICATION_ROOT + ROOT + " (DEV) " + rootOfClass);
        }
        return ROOT;
    }


    public static URL getRootUrlByClass(Class<?> class1, String subPaths) {
            try {
                File file = getRootByClass(class1, subPaths);
                if (file != null) {
                    return file.toURI().toURL();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        return null;
    }

    private static File getRootByClass(Class<?> class1, String subPaths) {
        // this is the jar file

        URL loc = class1.getProtectionDomain().getCodeSource().getLocation();

        try {
            File appRoot = new File(loc.toURI());

            if (appRoot.isFile()) {
                appRoot = appRoot.getParentFile();
            }
            if (subPaths != null) {
                return new File(appRoot, subPaths);
            }
            return appRoot;
        } catch (URISyntaxException e) {
            Log.exception(e);
            return null;
        }
    }

    public static File getTemp() {
        if(TEMP !=null){
            return TEMP;
        }
        TEMP = getResource("tmp");
        TEMP.mkdirs();
        return TEMP;
    }

    /**
     * @param cache
     * @return
     */
    public static File getTempResource(String cache) {
        return new File(getTemp(), cache);
    }

    public static boolean is64BitJvm() {
        if (JVM64BIT != null) {
            return JVM64BIT;
        }
        String archDataModel = System.getProperty("sun.arch.data.model");
        try {
            if (archDataModel != null) {
                if (Integer.parseInt(archDataModel) == 64) {
                    JVM64BIT = true;
                    return true;
                } else {
                    JVM64BIT = false;
                    return false;
                }
            }
        } catch (Throwable e) {
        }
        boolean is64BitJVM = CrossSystem.is64BitArch();
        JVM64BIT = is64BitJVM;
        return is64BitJVM;
    }

    /**
     * checks current java version for known issues/bugs or unsupported ones
     */
    public static boolean isOutdatedJavaVersion(boolean supportJAVA15) {
        long java = getJavaVersion();
        if (java < JAVA16 && !CrossSystem.isMac()) {
            Log.L.warning("Java 1.6 should be available on your System, please upgrade!");
            /* this is no mac os, so please use java>=1.6 */
            return true;
        }
        if (java < JAVA16 && !supportJAVA15) {
            Log.L.warning("Java 1.5 no longer supported!");
            /* we no longer support java 1.5 */
            return true;
        }
        if (java >= 16018000l && java < 16019000l) {
            Log.L.warning("Java 1.6 Update 18 has a serious bug in garbage collector!");
            /*
             * java 1.6 update 18 has a bug in garbage collector, causes java crashes
             *
             * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6847956
             */
            return true;
        }
        if (java >= 16010000l && java < 16011000l) {
            Log.L.warning("Java 1.6 Update 10 has a swing bug!");
            /*
             * 16010.26 http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6657923
             */
            return true;
        }
        if (CrossSystem.isMac() && java >= JAVA17 && java < 17006000l) {
            Log.L.warning("leaking semaphores bug");
            /*
             * leaking semaphores http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7166379
             */
            return true;
        }
        if (CrossSystem.isMac() && java >= 17250000l && java < 17550000l) {
            Log.L.warning("freezing AppKit thread bug");
            /*
             * http://bugs.java.com/view_bug.do?bug_id=8025588
             *
             * Frozen AppKit thread
             */
            return true;
        }
        return false;
    }

    /**
     * @param logger
     */
    public static void printSystemProperties(LogInterface logger) {
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        StringBuilder sb = new StringBuilder();
        String key;
        while (keys.hasMoreElements()) {
            key = (String) keys.nextElement();
            sb.append("SysProp: ").append(key).append(": ").append((String) p.get(key));

            logger.info(sb.toString());
            sb.setLength(0);
        }

        for (Entry<String, String> e : System.getenv().entrySet()) {

            sb.append("SysEnv: ").append(e.getKey()).append(": ").append(e.getValue());
            logger.info(sb.toString());
            sb.setLength(0);
        }

        URL url = getResourceURL("version.nfo");

        if (url != null) {
            try {
                logger.info(url + ":\r\n" + IO.readURLToString(url));
            } catch (IOException e1) {
                logger.log(e1);

            }
        }
        url = getResourceURL("build.json");

        if (url != null) {
            try {
                logger.info(url + ":\r\n" + IO.readURLToString(url));
            } catch (IOException e1) {
                logger.log(e1);

            }
        }

    }

    private static String getClass(Class<?> clazz) {
        if (clazz == null) {
            clazz = Application.class;
        }
        return COMPILE.matcher(clazz.getName()).replaceAll("/") + ".class";
    }

    /**
     * Detects if the Application runs out of a jar or not.
     *
     * @param clazz
     * @return
     */
    private static boolean isJared(Class<?> clazz) {
        if (IS_JARED != null) {

            return Objects.equals(IS_JARED, Boolean.TRUE);

        }
        String name = getClass(clazz);
        ClassLoader cll = Application.class.getClassLoader();
        if (cll == null) {
            Log.L.severe("getContextClassLoader() is null");
            IS_JARED = Boolean.TRUE;
            return true;
        }
        URL caller = cll.getResource(name);

        if (caller == null) {
            IS_JARED = false;
            return false;
        }
        boolean ret = PATTERN.matcher(caller.toString()).matches();
        IS_JARED = ret;
        return ret;
    }

    public static boolean isHeadless() {

        return GraphicsEnvironment.isHeadless();

    }

    /**
     * returns a file that does not exists. thus it ads a counter to the path until the resulting file does not exist
     *
     * @param string
     * @return
     */
    public static File generateNumberedTempResource(String string) {
        return generateNumbered(getTempResource(string));
    }

    /**
     * returns a file that does not exists. thus it ads a counter to the path until the resulting file does not exist
     *
     * @param stringFile
     * @return
     */
    public static File generateNumberedResource(String stringFile) {
        return generateNumbered(getResource(stringFile));

    }

    private static File generateNumbered(File orgFile) {
        int i = 0;

        String extension = Files.getExtension(orgFile.getName());
        File file = null;
        while (file == null || file.exists()) {
            i++;

            if (extension != null) {
                file = new File(orgFile.getParentFile(), orgFile.getName().substring(0, orgFile.getName().length() - extension.length() - 1) + '.' + i + '.' + extension);
            } else {
                file = new File(orgFile.getParentFile(), orgFile.getName() + '.' + i);
            }

        }
        return file;
    }
}
