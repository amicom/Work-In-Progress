package utils.net.BasicHTTP;

import utils.net.httpconnection.HTTPConnection;

import java.io.IOException;


public class BasicHTTPException extends IOException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final HTTPConnection connection;

    public BasicHTTPException(final HTTPConnection connection, final Exception e) {
        super(e);
        this.connection = connection;

    }

    public HTTPConnection getConnection() {
        return connection;
    }

}
