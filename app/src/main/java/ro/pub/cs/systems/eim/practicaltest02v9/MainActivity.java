package ro.pub.cs.systems.eim.practicaltest02v9;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText serverPortEditText;
    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText wordEditText;
    private EditText minLettersEditText;
    private Button serverConnectButton;
    private Button getAnagramsButton;
    private TextView anagramsTextView;
    private ServerThread serverThread;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Could not create server thread!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread.start();
        }
    }

    private final GetAnagramsButtonClickListener getAnagramsButtonClickListener = new GetAnagramsButtonClickListener();
    private class GetAnagramsButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String word = wordEditText.getText().toString();
            String minLetters = minLettersEditText.getText().toString();
            if (word.isEmpty() || minLetters.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Word and Min Letters parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            ClientThread clientThread = new ClientThread(
                    clientAddress,
                    Integer.parseInt(clientPort),
                    word,
                    minLetters,
                    anagramsTextView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v9_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        wordEditText = findViewById(R.id.client_word_edit_text);
        minLettersEditText = findViewById(R.id.min_letters_edit_text);

        serverConnectButton = findViewById(R.id.connect_button);
        serverConnectButton.setOnClickListener(connectButtonClickListener);

        getAnagramsButton = findViewById(R.id.get_anagrams_button);
        getAnagramsButton.setOnClickListener(getAnagramsButtonClickListener);

        anagramsTextView = findViewById(R.id.anagrams_text_view);
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}