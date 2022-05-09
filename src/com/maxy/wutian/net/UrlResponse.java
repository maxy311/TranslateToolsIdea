package com.maxy.wutian.net;


import com.maxy.wutian.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;


public class UrlResponse {
    private Map<String, List<String>> headers;

    private String content;
    private int statusCode;
    private String statusMessage;

    UrlResponse(HttpURLConnection conn) throws IOException {
        headers = conn.getHeaderFields();
        statusCode = conn.getResponseCode();
        statusMessage = conn.getResponseMessage();

        InputStream input = null;
        try {
            try {
                input = conn.getInputStream();
            } catch (IOException e) {
                input = conn.getErrorStream();
            }
            if (input != null)
                content = Utils.inputStreamToString(input, true);
        } finally {
            Utils.close(input);
        }
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UrlResponse [statusCode=").append(statusCode).append(", statusMessage=")
                .append(statusMessage).append(",content=").append(content).append("]");
        return builder.toString();
    }
}
