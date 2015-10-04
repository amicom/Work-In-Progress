package remoteapi.exceptions;


import storage.Storable;

public class ErrorResponse implements Storable {

    private String type;

    private Object data;

    public ErrorResponse(/* Storable */) {
    }

    public ErrorResponse(final String error, final Object data) {

        this.type = error;
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

}
