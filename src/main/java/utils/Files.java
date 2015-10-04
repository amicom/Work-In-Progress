/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils;


import utils.os.CrossSystem;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.*;

public class Files {
    /**
     * delete all files/folders that are given
     */
    public static void deleteRecursiv(final File file) throws IOException {
        Files.deleteRecursiv(file, true);
    }

    public static void deleteRecursiv(final File file, final boolean breakOnError) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (final File f : files) {
                    Files.deleteRecursiv(f, breakOnError);
                }
            }
        }
        System.out.println(" Delete file " + file);
        final boolean fd = file.delete();
        if (file.exists() && !fd && breakOnError) {
            throw new IOException("Could not delete " + file);
        }

    }

    public static LinkedList<String> getDirectories_NonRecursive(final File startDirectory, final boolean includeStart) throws IOException {
        final LinkedList<String> done = new LinkedList<String>();
        File current = null;
        File[] currents = null;
        final List<File> todo = new ArrayList<File>();
        todo.add(startDirectory);
        while (todo.size() > 0) {
            current = todo.remove(0);
            currents = current.listFiles();
            done.add(current.getCanonicalPath());
            if (currents != null) {
                for (int index = currents.length - 1; index >= 0; index--) {
                    if (currents[index].isDirectory()) {
                        final String temp = currents[index].getCanonicalPath();
                        if (!done.contains(temp)) {
                            todo.add(currents[index]);
                        }
                    }
                }
            }
        }
        /* remove startdirectory if wished */
        if (!includeStart && done.size() > 0) {
            done.remove(0);
        }
        return done;
    }

    /**
     * returns File if it exists (case (In)Sensitive). returns null if file does not exist
     */
    public static File getExistingFile(final File file, final boolean caseSensitive) {
        if (file == null) {
            return null;
        }
        if (caseSensitive) {
            if (file.exists()) {
                return file;
            }
            return null;
        }
        /* get list of files in current directory */
        final String lowerCaseFileName = file.getName().toLowerCase();
        final File parent = file.getParentFile();
        if (parent != null) {
            final File[] list = parent.listFiles();
            if (list != null) {
                for (final File ret : list) {
                    if (ret.getName().equalsIgnoreCase(lowerCaseFileName)) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the fileextension for a file with the given name
     *
     * @see #getFileNameWithoutExtension(String)
     * @param name
     * @return
     */
    public static String getExtension(final String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        final int index = name.lastIndexOf(".");
        if (index < 0 || index + 1 >= name.length()) {
            return null;
        }
        return name.substring(index + 1).toLowerCase(Locale.ENGLISH);
    }

    public static String getFileNameWithoutExtension(final String filename) {

        final int index = filename.lastIndexOf(".");
        if (index < 0) {
            return filename;
        }
        return filename.substring(0, index);
    }

    /**
     * return all files ( and folders if includeDirectories is true ) for the given files
     *
     * @param includeDirectories
     * @param files
     * @return
     */
    public static List<File> getFiles(final boolean includeDirectories, final boolean includeFiles, final File... files) {
        return Files.getFiles(new FileFilter() {

            @Override
            public boolean accept(final File pathname) {
                if (includeDirectories && pathname.isDirectory()) {
                    return true;
                }
                if (includeFiles && pathname.isFile()) {
                    return true;
                }
                return false;
            }
        }, files);
    }

    public static List<File> getFiles(final FileFilter filter, final File... files) {
        final List<File> ret = new ArrayList<File>();
        if (files != null) {
            for (final File f : files) {
                if (!f.exists()) {
                    continue;
                }
                if (filter == null || filter.accept(f)) {
                    ret.add(f);
                }

                if (f.isDirectory()) {

                    ret.addAll(Files.getFiles(filter, f.listFiles()));
                }
            }
        }
        return ret;
    }

    /**
     * Returns the mikmetype of the file. If unknown, it returns Unknown/extension
     *
     * @param name
     * @return
     */
    public static String getMimeType(final String name) {
        if (name == null) {
            return null;
        }
        final FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String ret = fileNameMap.getContentTypeFor(name);
        if (ret == null) {
            ret = "unknown/" + Files.getExtension(name);
        }
        return ret;
    }

    /**
     * Returns the relative path of file based on root.
     */
    public static String getRelativePath(final File root, final File file) {
        return Files.getRelativePath(root.getAbsolutePath(), file.getAbsolutePath());
    }

    public static String getRelativePath(String root, final String file) {

        final String rootPath, filePath;
        if (CrossSystem.isWindows() || CrossSystem.isOS2()) {
            root = root.replace("/", "\\");
            if (!root.endsWith("\\")) {
                root += "\\";
            }
            rootPath = root.toLowerCase(Locale.ENGLISH);
            filePath = file.toLowerCase(Locale.ENGLISH).replace("/", "\\");
            if (rootPath.equals(filePath + "\\")) {
                return "";
            }
        } else {
            if (!root.endsWith("/")) {
                root += "/";
            }
            rootPath = root;
            filePath = file;
            if (rootPath.equals(filePath + "/")) {
                return "";
            }
        }
        if (!filePath.startsWith(rootPath)) {
            return null;
        }
        if (rootPath.equals(filePath)) {
            return "/";
        }

        if (CrossSystem.isWindows() || CrossSystem.isOS2()) {
            return file.substring(rootPath.length()).replace("\\", "/");
        } else {
            return file.substring(rootPath.length());
        }
    }

    public static <T extends Exception> void internalWalkThroughStructure(final Handler<T> handler, final File f) throws T {
        if (!f.exists()) {
            return;
        }

        handler.onFile(f);
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            if (files == null) {
                return;
            }
            for (final File sf : files) {
                Files.internalWalkThroughStructure(handler, sf);
            }
        }
    }

    public static <T extends Exception> void internalWalkThroughStructureReverse(final Handler<T> handler, final File f) throws T {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            if (files == null) {
                return;
            }
            for (final File sf : files) {
                Files.walkThroughStructureReverse(handler, sf);
            }
        }
        handler.onFile(f);

    }

    public static <T extends Exception> void walkThroughStructure(final Handler<T> handler, final File f) throws T {
        handler.intro(f);
        try {
            Files.internalWalkThroughStructure(handler, f);
        } finally {
            handler.outro(f);
        }

    }

    public static <T extends Exception> void walkThroughStructureReverse(final Handler<T> handler, final File f) throws T {
        handler.intro(f);
        try {
            Files.internalWalkThroughStructureReverse(handler, f);
        } finally {
            handler.outro(f);
        }

    }

    public static File getCommonParent(File a, File b) {
        List<File> filesA = new LinkedList<File>();
        List<File> filesB = new LinkedList<File>();
        Files.fill(filesA, a);
        Files.fill(filesB, b);
        File last = null;
        String nameA, nameB;
        for (int i = 0; i < Math.min(filesA.size(), filesB.size()); i++) {
            if (CrossSystem.isWindows()) {
                if (i == 0) {
                    nameA = filesA.get(i).getPath().toLowerCase(Locale.ENGLISH);
                    nameB = filesB.get(i).getPath().toLowerCase(Locale.ENGLISH);
                } else {
                    nameA = filesA.get(i).getName().toLowerCase(Locale.ENGLISH);
                    nameB = filesB.get(i).getName().toLowerCase(Locale.ENGLISH);
                }

            } else {
                if (i == 0) {
                    nameA = filesA.get(i).getPath();
                    nameB = filesB.get(i).getPath();
                } else {
                    nameA = filesA.get(i).getName();
                    nameB = filesB.get(i).getName();
                }

            }
            if (!nameA.equals(nameB)) {
                break;
            }
            last = filesA.get(i);
        }
        return last;
    }

    /**
     * @param filesA
     * @param a
     */
    private static void fill(List<File> filesA, File a) {
        HashSet<File> dupe = new HashSet<File>();
        while (a != null) {
            if (!dupe.add(a)) {
                return;
            }
            filesA.add(0, a);
            File aTmp = a.getParentFile();
            if (aTmp == null || aTmp.equals(a)) {
                break;
            }
            a = aTmp;
        }
    }

    public static interface Handler<T extends Exception> {

        public void intro(File f) throws T;

        /**
         * @param f
         * @throws IOException
         */
        public void onFile(File f) throws T;

        public void outro(File f) throws T;
    }

    public static abstract class AbstractHandler<T extends Exception> implements Handler<T> {

        public void intro(final File f) {

        }

        /**
         * @param f
         * @throws IOException
         */
        abstract public void onFile(File f) throws T;

        public void outro(final File f) {

        }
    }

}