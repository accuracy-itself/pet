package com.training.pet.service;

import com.training.pet.model.Prediction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class BirdsService {

    @Value("${roboflow.api-key}")
    private String ROBOFLOW_API_KEY;

    @Value("${roboflow.api-endpoint}")
    private String ROBOFLOW_API_ENDPOINT;

    public String identifyBird(MultipartFile file) {
        try {
            String encodedFile = Base64.getEncoder().encodeToString(file.getBytes());
            String urlString = ROBOFLOW_API_ENDPOINT + "?api_key=" + ROBOFLOW_API_KEY
                    + "&name=" + file.getName();

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(encodedFile.getBytes().length));


            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(encodedFile);
                outputStream.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return extractResult(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String extractResult (String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode predictionsNode = rootNode.path("predictions");

            List<Prediction> predictions = new ArrayList<>();
            for (JsonNode predictionNode : predictionsNode) {
                String className = predictionNode.path("class").asText();
                double confidence = predictionNode.path("confidence").asDouble();
                predictions.add(new Prediction(className, confidence));
            }

            StringBuilder output = new StringBuilder();
            for (Prediction prediction : predictions) {
                output.append(prediction.getClassName())
                        .append(" - ")
                        .append(String.format("%.0f%%", prediction.getConfidence() * 100))
                        .append(", ");
            }

            if (!output.isEmpty()) {
                output.setLength(output.length() - 2); // Remove last ", "
            }

            return "chances are: " + output;
        } catch (Exception e) {
            return "Sorry, we don't know this bird";
        }
    }

}
