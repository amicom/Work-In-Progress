package utils.net.httpconnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface HTTPConnection {

    /**
     * establish a connection
     *
     * @throws IOException
     */
    void connect() throws IOException;

    /**
     * disconnect the connection
     */
    void disconnect();

    void finalizeConnect() throws IOException;

    int[] getAllowedResponseCodes();

    void setAllowedResponseCodes(int[] codes);

    /**
     * returns Charset
     *
     * @return
     */
    public String getCharset();

    public void setCharset(String charset);

    /**
     * always returns the complete length of the content. will also return the
     * complete filesize in range requests
     *
     * @return
     */
    long getCompleteContentLength();

    /**
     * returns length of current content, eg the complete file or the chunk that
     * got requested
     *
     * @return
     */
    long getContentLength();

    String getContentType();

    String getHeaderField(String string);

    /* WARNING: this returns a Case-Sensitive map */
    Map<String, List<String>> getHeaderFields();

    List<String> getHeaderFields(String string);

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    long[] getRange();

    RequestMethod getRequestMethod();

    void setRequestMethod(RequestMethod method);

    Map<String, String> getRequestProperties();

    String getRequestProperty(String string);

    public long getRequestTime();

    int getResponseCode();

    String getResponseMessage();

    URL getURL();

    boolean isConnected();

    boolean isContentDecoded();

    void setContentDecoded(boolean b);

    boolean isContentDisposition();

    boolean isOK();

    void setConnectTimeout(int connectTimeout);

    void setReadTimeout(int readTimeout);

    void setRequestProperty(String key, String string);

    boolean isSSLTrustALL();

    void setSSLTrustALL(boolean trustALL);

    public static enum RequestMethod {
        PUT,
        DELETE,
        OPTIONS,
        GET,
        POST,
        HEAD
    }

}
