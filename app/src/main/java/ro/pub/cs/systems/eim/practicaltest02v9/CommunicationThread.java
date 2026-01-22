package ro.pub.cs.systems.eim.practicaltest02v9;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] Error receiving data from client!");
                return;
            }

            String[] parts = line.split(",");
            if (parts.length < 2) {
                Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] Invalid data format!");
                return;
            }

            String word = parts[0];
            int minLetters = Integer.parseInt(parts[1]);

            String result = getData(word, minLetters);
            printWriter.println(result);
            printWriter.flush();

            socket.close();
        } catch (IOException e) {
            Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
                }
            }
        }
    }

    private String getData(String word, int minLetters) {
        try {
            OkHttpClient httpclient = new OkHttpClient();
            String url = "http://www.anagramica.com/all/" + word;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = httpclient.newCall(request).execute();

            String anagrams = response.body().string();
            JSONObject jsonObject = new JSONObject(anagrams);
            JSONArray anagramsArray = jsonObject.getJSONArray("all");
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < anagramsArray.length(); i++) {
                String currentAnagram = anagramsArray.getString(i);
                if (currentAnagram.length() >= minLetters) {
                    result.append(currentAnagram).append("\n");
                }
            }

            return result.toString();
        } catch (Exception e) {
            Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
            return "";
        }
    }
}