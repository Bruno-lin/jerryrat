package util;

import java.text.SimpleDateFormat;
import java.util.*;

public class ResponseHeaders {
    //headers content
    String statusLine;
    String date;
    String server;
    String location;
    String wwwAuthenticate;
    String contentLength;
    String contentType;
    String lastModified;

    //headers
    List<String> headers;


    public ResponseHeaders() {
        headers = new ArrayList<>();
    }

    private void updateHeaders() {
        headers.add(statusLine);
        headers.add(date);
        headers.add(wwwAuthenticate);
        server = "Server: JerryRat/1.0";
        headers.add(server);
        headers.add(location);
        headers.add(contentLength);
        headers.add(contentType);
        headers.add(lastModified);
    }

    public void setWwwAuthenticate(String wwwAuthenticate) {
        this.wwwAuthenticate = "WWW-Authenticate: " + wwwAuthenticate;
    }

    public void setLocation(String location) {
        this.location = "Location: " + location;
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

    String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        updateHeaders();

        for (String header : headers) {
            if (header != null) {
                builder.append(header).append("\r\n");
            }
        }
        return builder.toString().trim();
    }
}
