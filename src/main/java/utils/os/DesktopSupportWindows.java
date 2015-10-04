/**
 * Copyright (c) 2009 - 2012 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.os
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.os;


import utils.Application;
import utils.Files;
import utils.IO;
import utils.logging.Log;
import utils.processes.ProcessBuilderFactory;
import utils.processes.ProcessOutput;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author daniel
 *
 */
public class DesktopSupportWindows extends DesktopSupportJavaDesktop {

    private static boolean isHibernateActivated() {
        ProcessOutput status;
        try {
            status = ProcessBuilderFactory.runCommand("powercfg", "-a");
            // we should add the return for other languages
            if (status.getStdOutString(Charset.defaultCharset().name()) != null) {
                if (status.getStdOutString(Charset.defaultCharset().name()).contains("Ruhezustand wurde nicht aktiviert")) {
                    return false;
                }
                if (status.getStdOutString(Charset.defaultCharset().name()).contains("Hibernation has not been enabled")) {
                    return false;
                }
                if (status.getStdOutString(Charset.defaultCharset().name()).contains("Hibernation")) {
                    return false;
                }
            }
        } catch (Exception e) {
            Log.exception(e);
        }

        return true;
    }

    @Override
    public void browseURL(final URL url) throws IOException, URISyntaxException {
        try {
            // it seems that unicode filenames cannot be opened with
            // "rundll32.exe", "url.dll,FileProtocolHandler". let's try
            // Desktop.open first
            Desktop.getDesktop().browse(url.toURI());
        } catch (final Throwable e) {
            ProcessBuilderFactory.create("rundll32.exe", "url.dll,FileProtocolHandler", url.toExternalForm()).start();
        }
    }

    @Override
    public boolean isBrowseURLSupported() {
        return true;
    }

    @Override
    public boolean isOpenFileSupported() {
        return true;
    }

    @Override
    public void openFile(final File file) throws IOException {

        // workaround for windows
        // see http://bugs.sun.com/view_bug.do?bug_id=6599987
        if (!file.exists()) {
            throw new IOException("File does not exist " + file.getAbsolutePath());
        }
        try {
            // it seems that unicode filenames cannot be opened with
            // "rundll32.exe", "url.dll,FileProtocolHandler". let's try

            // this call works for unicode paths as well.
            // the " " parameter is a dummy parameter to represent the window
            // name. without it, paths with space will fail
            ProcessBuilderFactory.create("cmd", "/c", "start", "/B", " ", file.getCanonicalPath()).start();
            // desktop.open might freeze in WDesktopPeer.open....bla on win7
            // java 1.7u25
            // Desktop.getDesktop().open(file);
        } catch (final Exception e) {
            ProcessBuilderFactory.create("rundll32.exe", "url.dll,FileProtocolHandler", file.getCanonicalPath()).start();
        }
    }

    @Override
    public boolean shutdown(boolean force) {
        switch (CrossSystem.OS) {
            case WINDOWS_2003:
            case WINDOWS_VISTA:
            case WINDOWS_XP:
            case WINDOWS_7:
            case WINDOWS_8:
            case WINDOWS_SERVER_2012:
            /* modern windows versions */
            case WINDOWS_2000:
            case WINDOWS_NT:
            case WINDOWS_SERVER_2008:
            /* not so modern windows versions */
                if (force) {
                /* force shutdown */
                    try {
                        ProcessBuilderFactory.runCommand("shutdown.exe", "-s", "-f", "-t", "01");

                    } catch (Exception e) {
                        Log.exception(e);
                    }
                    try {
                        ProcessBuilderFactory.runCommand("%windir%\\system32\\shutdown.exe", "-s", "-f", "-t", "01");
                    } catch (Exception e) {
                        Log.exception(e);
                    }
                } else {
                /* normal shutdown */
                    try {
                        ProcessBuilderFactory.runCommand("shutdown.exe", "-s", "-t", "01");
                    } catch (Exception e) {
                        Log.exception(e);
                    }
                    try {
                        ProcessBuilderFactory.runCommand("%windir%\\system32\\shutdown.exe", "-s", "-t", "01");
                    } catch (Exception e) {
                        Log.exception(e);
                    }
                }
                if (CrossSystem.OS == CrossSystem.OperatingSystem.WINDOWS_2000 || CrossSystem.OS == CrossSystem.OperatingSystem.WINDOWS_NT) {
                /* also try extra methods for windows2000 and nt */
                    try {

                        File f = Application.getTempResource("shutdown.vbs");
                        f.deleteOnExit();
                        IO.writeStringToFile(f, "set WshShell = CreateObject(\"WScript.Shell\")\r\nWshShell.SendKeys \"^{ESC}^{ESC}^{ESC}{UP}{ENTER}{ENTER}\"\r\n");
                        try {
                            ProcessBuilderFactory.runCommand("cmd", "/c", "start", "/min", "cscript", f.getAbsolutePath());
                        } finally {
                            Files.deleteRecursiv(f);
                        }
                    } catch (Exception e) {
                        Log.exception(e);
                    }
                }
                break;
            case WINDOWS_OTHERS:
            /* older windows versions */
                try {
                    ProcessBuilderFactory.runCommand("RUNDLL32.EXE", "user,ExitWindows");
                } catch (Exception e) {
                    Log.exception(e);
                }
                try {
                    ProcessBuilderFactory.runCommand("RUNDLL32.EXE", "Shell32,SHExitWindowsEx", "1");
                } catch (Exception e) {
                    Log.exception(e);
                }
                break;
        }
        return true;

    }

    @Override
    public boolean standby() {
        switch (CrossSystem.OS) {
            case WINDOWS_2003:
            case WINDOWS_VISTA:
            case WINDOWS_XP:
            case WINDOWS_7:
            case WINDOWS_8:
            /* modern windows versions */
            case WINDOWS_2000:
            case WINDOWS_NT:
            /* not so modern windows versions */
                if (isHibernateActivated()) {
                    // hibernate activated? -> disable it
                    String path = CrossSystem.is64BitOperatingSystem() ? Application.getResource("tools\\Windows\\elevate\\Elevate64.exe").getAbsolutePath() : Application.getResource("tools\\Windows\\elevate\\Elevate32.exe").getAbsolutePath();
                    try {
                        ProcessBuilderFactory.runCommand(path, "powercfg", "-hibernate", "off");

                    } catch (Throwable e) {
                        Log.exception(e);
                    }
                }
                try {
                    ProcessBuilderFactory.runCommand("powercfg.exe", "hibernate off");
                } catch (Exception e) {
                    try {
                        ProcessBuilderFactory.runCommand("%windir%\\system32\\powercfg.exe", "hibernate off");
                    } catch (Exception ex) {
                        Log.exception(ex);
                    }
                }
                try {
                    ProcessBuilderFactory.runCommand("RUNDLL32.EXE", "powrprof.dll,SetSuspendState");
                } catch (Exception e) {
                    try {
                        ProcessBuilderFactory.runCommand("%windir%\\system32\\RUNDLL32.EXE", "powrprof.dll,SetSuspendState");
                    } catch (Exception e1) {
                        Log.exception(e);
                    }
                }
                break;
            case WINDOWS_OTHERS:
            /* older windows versions */
                Log.L.info("no standby support, use shutdown");
                return false;

        }
        return true;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.appwork.utils.os.DesktopSupport#hibernate()
     */
    @Override
    public boolean hibernate() {
        switch (CrossSystem.OS) {
            case WINDOWS_2003:
            case WINDOWS_VISTA:
            case WINDOWS_XP:
            case WINDOWS_7:
            case WINDOWS_8:
            /* modern windows versions */
            case WINDOWS_2000:
            case WINDOWS_NT:
            /* not so modern windows versions */
                if (!DesktopSupportWindows.isHibernateActivated()) {
                    // enable hibernate
                    String path = CrossSystem.is64BitOperatingSystem() ? Application.getResource("tools\\Windows\\elevate\\Elevate64.exe").getAbsolutePath() : Application.getResource("tools\\Windows\\elevate\\Elevate32.exe").getAbsolutePath();
                    try {
                        ProcessBuilderFactory.runCommand(path, "powercfg", "-hibernate", "on");

                    } catch (Throwable e) {
                        Log.exception(e);
                    }
                }
                try {
                    ProcessBuilderFactory.runCommand("powercfg.exe", "hibernate on");
                } catch (Exception e) {
                    try {
                        ProcessBuilderFactory.runCommand("%windir%\\system32\\powercfg.exe", "hibernate on");
                    } catch (Exception ex) {
                        Log.exception(ex);
                    }
                }
                try {
                    ProcessBuilderFactory.runCommand("RUNDLL32.EXE", "powrprof.dll,SetSuspendState");
                } catch (Exception e) {
                    Log.exception(e);
                    try {
                        ProcessBuilderFactory.runCommand("%windir%\\system32\\RUNDLL32.EXE", "powrprof.dll,SetSuspendState");
                    } catch (Exception ex) {
                        Log.exception(ex);
                    }
                }
                break;
            case WINDOWS_OTHERS:
            /* older windows versions */
                Log.L.info("no hibernate support, use shutdown");
                return false;
        }
        return true;

    }

}
