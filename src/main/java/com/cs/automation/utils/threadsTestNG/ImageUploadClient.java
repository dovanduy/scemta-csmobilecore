package com.cs.automation.utils.threadsTestNG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.testng.Reporter;

/**
 * The is used to upload test image to reporting server
 */
public class ImageUploadClient {

    private static final String LINE_FEED = "\r\n";
    private static final int BUFFER_SIZE = 4096;
    private static final String CHARSET = "UTF-8";

    private PrintWriter writer;
    private String boundary;
    private String testImageUploadURL;

    public ImageUploadClient(String testImageUploadURL) {
        this.testImageUploadURL = testImageUploadURL;
    }

    /**
     * Adds a form field to the http post request
     * @param name
     * @param value
     */
    private void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + CHARSET).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * All the parameters are mandatory
     * @param contenttype - value can be image/png or image/jpeg
     * @param inputStream
     * @return returns the server returned JSON response as string
     * @throws IOException
     */
    public String upload(String contenttype, InputStream inputStream) throws IOException {

        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(testImageUploadURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "Java");
        OutputStream outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

        // generate file name
        UUID uuid = UUID.randomUUID();
        String filename = uuid.toString();
        // log file name to TestNG
        Reporter.log("IMG : " + filename);
        // add form fields
        addFormField("filename", filename);
        addFormField("contenttype", contenttype);

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + "file" + "\"; filename=\"" + filename
                        + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(filename)).append(
                LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();

        StringBuilder response = new StringBuilder();

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response.toString();
    }

}
