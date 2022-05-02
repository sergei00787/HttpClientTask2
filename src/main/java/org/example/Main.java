package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static final String API_NASA_URL = "https://api.nasa.gov/planetary/apod";
    public static final String API_NASA_KEY = "DbvtwtvNnannstOUlPFVJ1JybwOAuHWg2sHpPcP5";

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setRedirectsEnabled(false)
                        .setSocketTimeout(30000)
                        .setConnectTimeout(5000).build())
                .build()) {
            HttpGet requestNasa = new HttpGet(API_NASA_URL + "?api_key=" + API_NASA_KEY);

            HttpResponse response = httpClient.execute(requestNasa);
            HttpEntity entity = response.getEntity();

            NasaContent content = mapper.readValue(entity.getContent(), new TypeReference<>() {
            });

            String nameContent = content.getUrl().substring(content.getUrl().lastIndexOf('/') + 1, content.getUrl().lastIndexOf("."));
            String fileExtension = content.getUrl().substring(content.getUrl().lastIndexOf("."));

            File file = new File("D:\\" + nameContent + fileExtension);

            HttpGet requestNasaContent = new HttpGet(content.getUrl());
            HttpResponse responseNasaContent = httpClient.execute(requestNasaContent);

            HttpEntity entityNasaContent = responseNasaContent.getEntity();
            FileOutputStream writer = new FileOutputStream(file);

            int read = 0;
            byte[] buffer = new byte[1024];
            while ((read = entityNasaContent.getContent().read(buffer)) != -1) {
                writer.write(buffer, 0, read);
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

