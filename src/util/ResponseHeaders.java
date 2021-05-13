package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ResponseHeaders {
    //headers content
    String statusLine;
    String date;
    String server;
    String contentLength;
    String contentType;
    String lastModified;

    //headers
    List<String> headers;


    public ResponseHeaders() {
        headers = new ArrayList<>();
        this.server = "Server: JerryRat";
        this.statusLine = "HTTP/1.0 200 OK";
    }

    private void collectHeaders() {
        headers.add(statusLine);
        headers.add(date);
        headers.add(server);
        headers.add(contentLength);
        headers.add(contentType);
        headers.add(lastModified);
    }

    public void setDate(Date date) {
        this.date = "Date: " + getDate(date);
    }

    public void setServer(String server) {
        this.server = "Server: " + server;
    }

    public void setStatusLine(String statusCode) {
        this.statusLine = "HTTP/1.0 " + statusCode;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = "Content-Length: " + contentLength;
    }

    public void setContentType(String contentType) {
        this.contentType = "Content-Type: " + contentType;
    }

    public void setLastModified(Date date) {
        this.lastModified = "Last-Modified: " + getDate(date);
    }

    public String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        collectHeaders();

        for (String header : headers) {
            if (header != null) {
                builder.append(header).append("\r\n");
            }
        }
        return builder.toString().trim();
    }
}
