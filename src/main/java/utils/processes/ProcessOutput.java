/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.processes
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.processes;

import java.io.UnsupportedEncodingException;

/**
 * @author Thomas
 *
 */
public class ProcessOutput {

    private int exitCode;
    private byte[] stdOutData;
    private byte[] errOutData;

    /**
     * @param waitFor
     * @param byteArray
     * @param byteArray2
     */
    public ProcessOutput(int exitCode, byte[] stdOut, byte[] errOut) {
        this.exitCode = exitCode;
        this.stdOutData = stdOut;
        this.errOutData = errOut;

    }

    public String getStdOutString(String charset) throws UnsupportedEncodingException {
        return new String(getStdOutData(), charset);
    }

    public String getErrOutString(String charset) throws UnsupportedEncodingException {
        return new String(getErrOutData(), charset);
    }

    public int getExitCode() {
        return exitCode;
    }

    public byte[] getStdOutData() {
        return stdOutData;
    }

    public byte[] getErrOutData() {
        return errOutData;
    }

}
