package ro.pub.cs.systems.eim.practicaltest02v9;

import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String word;
    private final String minLetters;
    private final TextView anagramTextView;
    private Socket socket;

    public ClientThread(String address, int port, String word, String minLetters, TextView anagramTextView) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.minLetters = minLetters;
        this.anagramTextView = anagramTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            printWriter.println(word + "," + minLetters);
            printWriter.flush();

            String anagram;
            anagramTextView.post(() -> anagramTextView.setText(""));

            while ((anagram = bufferedReader.readLine()) != null) {
                final String finalizedAnagram = anagram;
                anagramTextView.post(() -> anagramTextView.append(finalizedAnagram + "\n"));
            }
        } catch (IOException e) {
            Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
                }
            }
        }
    }
}