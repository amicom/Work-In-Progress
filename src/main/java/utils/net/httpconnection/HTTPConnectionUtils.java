package utils.net.httpconnection;

import utils.Regex;
import utils.StringUtils;
import utils.encoding.Base64;
import utils.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;

public class HTTPConnectionUtils {

    public final static byte R = (byte) 13;
    public final static byte N = (byte) 10;

    public static String getFileNameFromDispositionHeader(final String contentdisposition) {
        // http://greenbytes.de/tech/tc2231/
        if (!StringUtils.isEmpty(contentdisposition)) {
            if (contentdisposition.matches("(?i).*(;| |^)filename\\*.+")) {
                /* RFC2231 */
                final String encoding = new Regex(contentdisposition, "(?:;| |^)filename\\*\\s*=\\s*(.+?)''").getMatch(0);
                if (encoding == null) {
                    Log.L.severe("Missing encoding: " + contentdisposition);
                    return null;
                }
                final String filename = new Regex(contentdisposition, "(?:;| |^)filename\\*\\s*=\\s*.+?''(.*?)($|;\\s*|;$)").getMatch(0);
                if (filename == null) {
                    Log.L.severe("Broken/Unsupported: " + contentdisposition);
                    return null;
                } else {
                    try {
                        String ret = URLDecoder.decode(filename.trim(), encoding.trim()).trim();
                        ret = ret.replaceFirst("^" + Matcher.quoteReplacement("\\") + "+", Matcher.quoteReplacement("_"));
                        return ret;
                    } catch (final Exception e) {
                        Log.L.severe("Decoding Error: " + filename + "|" + encoding + "|" + contentdisposition);
                        return null;
                    }
                }
            } else if (contentdisposition.matches("(?i).*(;| |^)(filename|file_name|name).+")) {
                final String special[] = new Regex(contentdisposition, "(?:;| |^)(?:filename|file_name|name)\\s*==\\?(.*?)\\?B\\?([a-z0-9+/=]+)\\?=").getRow(0);
                if (special != null) {
                    try {
                        final String base64 = special[1] != null ? special[1].trim() : null;
                        final String encoding = special[0] != null ? special[0].trim() : null;
                        String ret = URLDecoder.decode(new String(Base64.decode(base64), encoding), encoding).trim();
                        ret = ret.replaceFirst("^" + Matcher.quoteReplacement("\\") + "+", Matcher.quoteReplacement("_"));
                        return ret;
                    } catch (final Exception e) {
                        Log.L.severe("Decoding(Base64) Error: " + contentdisposition);
                        return null;
                    }
                }
                final String filename = new Regex(contentdisposition, "(?:;| |^)(filename|file_name|name)\\s*=\\s*(\"|'|)(.*?)(\\2$|\\2;$|\\2;.)").getMatch(2);
                if (filename == null) {
                    Log.L.severe("Broken/Unsupported: " + contentdisposition);
                } else {
                    String ret = filename.trim();
                    ret = ret.replaceFirst("^" + Matcher.quoteReplacement("\\") + "+", Matcher.quoteReplacement("_"));
                    return ret;
                }
            }
            if (contentdisposition.matches("(?i).*xfilename.*")) {
                return null;
            }
            Log.L.severe("Broken/Unsupported: " + contentdisposition);
        }
        return null;
    }

    public static ByteBuffer readheader(final InputStream in, final boolean readSingleLine) throws IOException {
        ByteBuffer bigbuffer = ByteBuffer.wrap(new byte[4096]);
        final byte[] minibuffer = new byte[1];
        int position;

        while (in.read(minibuffer) >= 0) {
            if (bigbuffer.remaining() < 1) {
                final ByteBuffer newbuffer = ByteBuffer.wrap(new byte[bigbuffer.capacity() * 2]);
                bigbuffer.flip();
                newbuffer.put(bigbuffer);
                bigbuffer = newbuffer;
            }
            bigbuffer.put(minibuffer);
            if (readSingleLine) {
                if (bigbuffer.position() >= 1) {
                    /*
                     * \n only line termination, for fucking buggy non rfc servers
                     */
                    position = bigbuffer.position();
                    if (bigbuffer.get(position - 1) == HTTPConnectionUtils.N) {
                        break;
                    }
                    if (bigbuffer.position() >= 2) {
                        /* \r\n, correct line termination */
                        if (bigbuffer.get(position - 2) == HTTPConnectionUtils.R && bigbuffer.get(position - 1) == HTTPConnectionUtils.N) {
                            break;
                        }
                    }
                }
            } else {
                if (bigbuffer.position() >= 2) {
                    position = bigbuffer.position();
                    if (bigbuffer.get(position - 2) == HTTPConnectionUtils.N && bigbuffer.get(position - 1) == HTTPConnectionUtils.N) {
                        /*
                         * \n\n for header<->content divider, or fucking buggy non rfc servers
                         */
                        break;
                    }
                    if (bigbuffer.position() >= 4) {
                        /* \r\n\r\n for header<->content divider */
                        if (bigbuffer.get(position - 4) == HTTPConnectionUtils.R && bigbuffer.get(position - 3) == HTTPConnectionUtils.N && bigbuffer.get(position - 2) == HTTPConnectionUtils.R && bigbuffer.get(position - 1) == HTTPConnectionUtils.N) {
                            break;
                        }
                    }
                }
            }
        }

        bigbuffer.flip();
        return bigbuffer;
    }

    public static InetAddress[] resolvHostIP(String host) throws IOException {
        if (StringUtils.isEmpty(host)) {
            throw new UnknownHostException("Could not resolve: -empty host-");
        }
        /* remove spaces....so literal IP's work without resolving */
        host = host.trim();
        InetAddress hosts[] = null;
        for (int resolvTry = 0; resolvTry < 2; resolvTry++) {
            try {
                /* resolv all possible ip's */
                hosts = InetAddress.getAllByName(host);
                return hosts;
            } catch (final UnknownHostException e) {
                try {
                    Thread.sleep(500);
                } catch (final InterruptedException e1) {
                    break;
                }
            }
        }
        throw new UnknownHostException("Could not resolve: -" + host + "-");
    }
}
