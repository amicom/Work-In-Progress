package startup;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import utils.*;
import utils.IO.SYNC;
import utils.logging.LogController;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.Security;
import java.util.concurrent.atomic.AtomicBoolean;


public class Main {

    static {

        // checks if the machine is using an older version of java than the minimum required
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
            JOptionPane.showMessageDialog(null,
                    "Your version of Java is not compatible with this application.\nplease upgrade to the latest version!",
                    "Incompatible Java Version",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        /*
         * only use ipv4, because debian changed default stack to ipv6
         * we have to make sure that this property gets set before any network stuff gets loaded!!
         */
        System.setProperty("java.net.preferIPv4Stack", "true");
        /*
         * never cache negative answers,workaround for buggy dns servers that can fail and then the cache would be polluted for cache
         * timeout
         */
        Security.setProperty("networkaddress.cache.negative.ttl", 0 + "");
        App.setApplication(".jd_home");
        String root = App.getRoot(SecondLevelLaunch.class);



        try {
            copySVNtoHome();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        IO.setErrorHandler(new IOErrorHandler() {
            private final AtomicBoolean reported = new AtomicBoolean(SystemUtils.isJavaAwtHeadless());

            @Override
            public void onWriteException(Throwable e, File file, byte... data) {
                LogController.getInstance().getLogger("GlobalIOErrors").severe("An error occured while writing " + data.length + " bytes to " + file);
                LogController.getInstance().getLogger("GlobalIOErrors").log(e);
                if (reported.compareAndSet(false, true)) {
                    new Thread() {
                        public void run() {
                            Dialog d =new Dialog();
                            d.setContentText("Write Error occured");
                            d.show();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
//                            LogAction la = new LogAction();
//                            la.actionPerformed(null);
                        }
                    }.start();
                }

            }

            @Override
            public void onReadStreamException(Throwable e, InputStream fis) {
                LogController.getInstance().getLogger("GlobalIOErrors").log(e);
                if (reported.compareAndSet(false, true)) {
                    new Thread() {
                        public void run() {
//                            Dialog.getInstance().showExceptionDialog("Read Error occured", "An error occured while reading data", e);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
//                            LogAction la = new LogAction();
//                            la.actionPerformed(null);
                        }
                    }.start();
                }
            }

            @Override
            public void onCopyException(Throwable e, File in, File out) {

            }
        });

    }


    private static void copySVNtoHome() {
        try {
            if (!App.isJared(null) && App.getResourceURL("org/jdownloader/update/JDUpdateClient.class") == null) {

                File workspace = new File(Main.class.getResource("/").toURI()).getParentFile();
                File svnEntriesFile = new File(workspace, ".svn/entries");
                if (svnEntriesFile.exists()) {
                    long lastMod = svnEntriesFile.lastModified();
                    try {
                        lastMod = Long.parseLong(Regex.getLines(IO.readFileToString(svnEntriesFile))[3].trim());
                    } catch (IOException | NumberFormatException ignored) {}

                    long lastUpdate = -1L;
                    File lastSvnUpdateFile = Application.getResource("dev/lastSvnUpdate");
                    if (lastSvnUpdateFile.exists()) {
                        try {
                            lastUpdate = Long.parseLong(IO.readFileToString(lastSvnUpdateFile));
                        } catch (IOException | NumberFormatException ignored) {}
                    }
                    if (lastMod > lastUpdate) {
                        copyResource(workspace, "themes/themes", "themes");
                        copyResource(workspace, "ressourcen/jd", "jd");
                        copyResource(workspace, "ressourcen/tools", "tools");
                        copyResource(workspace, "translations/translations", "translations");
                        File jdJar = Application.getResource("JDownloader.jar");
                        jdJar.delete();
                        IO.copyFile(new File(workspace, "dev/JDownloader.jar"), jdJar);
                        lastSvnUpdateFile.delete();
                        lastSvnUpdateFile.getParentFile().mkdirs();
                        IO.writeStringToFile(lastSvnUpdateFile, String.valueOf(lastMod));
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private static void copyResource(File workspace, String from, String to) throws IOException {
        App.log.info("Copy SVN Resources " + new File(workspace, from) + " to " + Application.getResource(to));
        IO.copyFolderRecursive(new File(workspace, from), Application.getResource(to), true, pathname -> {
            if (pathname.getAbsolutePath().contains(".svn")) {
                return false;
            } else {
                App.log.info("Copy " + pathname);
                return true;
            }

        }, SYNC.NONE);
    }


    public static void main(String... args) {
        javafx.application.Application.launch();
    }

        public void start(Stage stage) {
            Circle circ = new Circle(40, 40, 30);
            Group root = new Group(circ);
            Scene scene = new Scene(root, 400, 300);

            stage.setTitle("My JavaFX Application");
            stage.setScene(scene);
            stage.show();
        }

}
