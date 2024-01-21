package org.example;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramSender {
    private static HttpURLConnection con;
    private static String tgToken = "6303379959:AAH8PxbMiRtOm4O6CiaN7kn5nxIDQeip8yo";
    private static String urlToken = "https://api.telegram.org/bot"+tgToken+"/sendMessage";
    private static String urlTokenlLoc = "https://api.telegram.org/bot"+tgToken+"/sendLocation";

    public static void sendViaTelegram(Long chatId, String txt) throws IOException {
        String urlParameters = "chat_id="+chatId+"&text="+txt;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        try {
            URL url = new URL(urlToken);
            con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }
            StringBuilder content;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

        } finally {
            con.disconnect();
        }
    }

    public static void sendChatMsgToClient(Long chatId, String body) {
        if(chatId != null && chatId>1000000) {
            try {
                sendViaTelegram(chatId, body);
                System.out.println("message was sent");
            } catch (IOException e) {
                System.out.println("error");
            }
        }
    }

    public static void sendLocation(Long chatId, double latitude, double longitude) throws IOException {
        String urlParameters = "chat_id=" + chatId + "&latitude=" + latitude + "&longitude=" + longitude;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection con = null;

        try {
            URL url = new URL(urlTokenlLoc);
            con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }

            StringBuilder content;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
