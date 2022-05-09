package com.maxy.wutian.net;

import com.maxy.wutian.utils.Utils;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtils {
    public static UrlResponse get(String urlStr, Map<String, String> params, int connectTimeout, int readTimeout) throws IOException {
        return get(urlStr, null, params, connectTimeout, readTimeout);
    }

    public static UrlResponse get(String urlStr, Map<String, String> headers, Map<String, String> params, int connectTimeout, int readTimeout) throws IOException {
        UrlResponse response = null;

        StringBuilder builder = new StringBuilder(urlStr);
        if (params != null && params.size() > 0) {
            if (!urlStr.contains("?")) {
                builder.append("?");
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (builder.toString().contains("=")) {
                    builder.append("&");
                }
                builder.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
            }
        }

        URL url = new URL(builder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setInstanceFollowRedirects(true);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            response = new UrlResponse(conn);
        } finally {
            conn.disconnect();
        }
        return response;
    }

    public static UrlResponse post(String urlStr, Map<String, String> headers, Map<String, String> params, int connectTimeout, int readTimeout) throws IOException {
        Writer writer = null;
        UrlResponse response = null;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setInstanceFollowRedirects(true);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (params != null && params.size() > 0) {
                StringBuilder builder = new StringBuilder();
                writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                boolean isfirst = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (isfirst) {
                        isfirst = false;
                    } else {
                        writer.write("&");
                        builder.append("&");
                    }

                    writer.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
                    builder.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
                }
                writer.flush();
            }

            response = new UrlResponse(conn);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
            conn.disconnect();
        }

        return response;
    }


    public static UrlResponse uploadFile(String urlStr, File file, int connectTimeout, int readTimeout) throws IOException {
        final String RN = "\r\n";
        OutputStream out = null;
        Writer writer = null;
        UrlResponse response = null;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            String boundary = "----Java" + "ZnGpCtePMx0KrHw_G0Xl9Yefer8JZlRJSXe";

            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setChunkedStreamingMode(64 * 1024);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setInstanceFollowRedirects(true);

            String name = "upload";
            if (file != null && file.length() > 0) {
                StringBuilder builder = new StringBuilder();
                out = conn.getOutputStream();
                writer = new OutputStreamWriter(out, "UTF-8");
                boundary = "--" + boundary;

                writer.append("Content-Disposition: form-data; name=\"")
                        .append(name).append("\"; filename=\"")
                        .append(file.getName()).append("\"").append(RN);
                writer.append("Content-Type: application/octet-stream").append(RN).append(RN);

                builder.append("Content-Disposition: form-data; name=\"")
                        .append(name).append("\"; filename=\"")
                        .append(file.getName()).append("\"").append(RN);
                builder.append("Content-Type: image/").append(FileUtils.getExtension(file.getName()))
                        .append(RN).append(RN);
                builder.append("[FILE]");

                writer.flush();
                writeFileToStream(file, out);
                out.flush();

                writer.append(boundary).append("--").append(RN);
                writer.flush();

                builder.append(boundary).append("--").append(RN);
            }

            response = new UrlResponse(conn);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
            conn.disconnect();
        }

        return response;
    }


    // url encode a string with UTF-8 encoding
    public static String urlEncode(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static void writeFileToStream(File file, OutputStream output) throws IOException {
        RandomAccessFile mRandomAccessFile = null;
        try {
            mRandomAccessFile = new RandomAccessFile(file, "r");
            byte[] b = new byte[1024 * 4];
            int r;
            while ((r = mRandomAccessFile.read(b)) != -1) {
                output.write(b, 0, r);
            }
            output.flush();
        } finally {
            Utils.close(mRandomAccessFile);
        }
    }
}
