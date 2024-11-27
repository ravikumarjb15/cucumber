package cucumberFramework.testRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Scanner;

public class JenkinsRunner {

//    private static final String JENKINS_URL = "http://localhost:8080";
//    private static final String JOB_NAME = "git";
//    private static final String USER = "admin";
//    private static final String TOKEN = "testtoken";

//	public static void main(String[] args) throws IOException, InterruptedException
//	{
//		String url="http://localhost:8080/job/git/build?token=testtoken";
//
//		String url="http://localhost:8080/buildByToken/build?job=git&token=testtoken";
//		URL obj=new URL(url);
//		HttpURLConnection con=(HttpURLConnection) obj.openConnection();
//		Thread.sleep(1000);
//		System.out.println("Job status is " +con.getResponseCode());
//		System.out.println("Job status is " +con.getLastModified());
//		
//		System.out.println("execution ended");
//		con.disconnect();
//		 HttpClient client = HttpClient.newHttpClient();
//		    HttpRequest request = HttpRequest.newBuilder()
//		      .uri(URI.create(url)).build();
//		    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
//		    System.out.println(response.toString());
//		    
//	}

	public static void main(String[] args) {
		try {
			String jobName = "git";
			String token = "testtoken";
			String user = "ravi";
			String apiToken = "112ff7ac9beadc342df1c8d1f0a11b89c0";
			String jenkinsUrl = "http://localhost:8080";
			int buildNumber = triggerJenkinsJob(jenkinsUrl, jobName, token, user, apiToken);
//			buildNumber =buildNumber-1;
			System.out.println("Triggered Job Build Number: " + buildNumber);
			monitorJobStatus(jenkinsUrl, jobName, buildNumber, user, apiToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private static int triggerJenkinsJob(String jenkinsUrl, String jobName, String token, String user, String apiToken) throws IOException, InterruptedException {
        String url = jenkinsUrl + "/buildByToken/build?job=" + jobName + "&token=" + token;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        String auth = user + ":" + apiToken;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        con.setRequestProperty("Authorization", "Basic " + encodedAuth);

        // Ensure the job gets triggered
        Thread.sleep(1000);

        int responseCode = con.getResponseCode();
        if (responseCode != 201 && responseCode != 200) {
            throw new RuntimeException("Failed to trigger job: " + responseCode);
        }

        // Get queue item URL
        String queueUrl = con.getHeaderField("Location") + "api/json"; // Ensure full URL
        System.out.println("Queue URL: " + queueUrl);
        return getBuildNumberFromQueue(jenkinsUrl, queueUrl, user, apiToken);
    }

    private static int getBuildNumberFromQueue(String jenkinsUrl, String queueUrl, String user, String apiToken) throws IOException, InterruptedException {
        HttpURLConnection con;
        int buildNumber = -1;

        while (buildNumber == -1) {
            URL obj = new URL(jenkinsUrl + queueUrl); // Include base Jenkins URL
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            String auth = user + ":" + apiToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            con.setRequestProperty("Authorization", "Basic " + encodedAuth);

            Scanner scanner = new Scanner(con.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parse the JSON response to get the build number
            if (response.contains("\"executable\"")) {
                int startIndex = response.indexOf("\"number\":") + 9;
                int endIndex = response.indexOf(",", startIndex);
                buildNumber = Integer.parseInt(response.substring(startIndex, endIndex).trim());
            } else {
                Thread.sleep(2000);  // Wait for 2 seconds before retrying
            }
        }

        return buildNumber;
    }

    private static void monitorJobStatus(String jenkinsUrl, String jobName, int buildNumber, String user, String apiToken) throws IOException, InterruptedException {
        String buildStatusUrl = jenkinsUrl + "/job/" + jobName + "/" + buildNumber + "/api/json";
        boolean isBuilding = true;

        while (isBuilding) {
            URL obj = new URL(buildStatusUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            String auth = user + ":" + apiToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            con.setRequestProperty("Authorization", "Basic " + encodedAuth);

            Scanner scanner = new Scanner(con.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            if (response.contains("\"building\":false")) {
                isBuilding = false;
                if (response.contains("\"result\":\"SUCCESS\"")) {
                    System.out.println("Build " + buildNumber + " completed successfully.");
                } else {
                    System.out.println("Build " + buildNumber + " failed.");
                }
            } else {
                System.out.println("Build " + buildNumber + " is still in progress...");
                Thread.sleep(5000);  // Wait for 5 seconds before checking again
            }
        }
    }

}
