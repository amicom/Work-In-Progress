/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.ftpserver
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.ftpserver;

/**
 * @author thomas
 *
 */
public class FtpFile {

    private final long size;
    private final boolean isDirectory;
    protected String name;
    protected long lastModified;
    private String owner = "unknown";

    private String group = "unknown";

    /**
     * @param name
     * @param length
     * @param directory
     */
    public FtpFile(final String name, final long length, final boolean directory, final long lastMod) {
        this.name = name;
        this.size = length;
        this.isDirectory = directory;
        this.lastModified = lastMod;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(final String group) {
        this.group = group;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public String getName() {
        return this.name;
    }

    public String getOwner() {
        return this.owner;
    }

    /**
     * @param owner
     *            the owner to set
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public long getSize() {
        return this.size;
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

}
