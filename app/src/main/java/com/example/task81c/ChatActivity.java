package com.example.task81c;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    LinearLayout messageContainer;
    EditText userInput;
    Button sendButton;
    String username;

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");
        messageContainer = findViewById(R.id.messageContainer);
        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);

        addBotMessage("Welcome " + username + "!");

        sendButton.setOnClickListener(v -> {
            String message = userInput.getText().toString().trim();
            if (!message.isEmpty()) {
                addUserMessage(message);
                userInput.setText("");
                sendMessageToBot(message);
            }
        });
    }

    private void sendMessageToBot(String userMessage) {
        String url = "http://10.0.2.2:5000/chat";

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d(TAG, "Bot Response: " + response);
                    addBotMessage(response);
                },
                error -> {
                    error.printStackTrace();
                    addBotMessage("Error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userMessage", userMessage);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    private void addUserMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("You: " + message);
        textView.setBackgroundColor(0xFFD0F0C0);
        textView.setPadding(16, 8, 16, 8);
        messageContainer.addView(textView);
    }

    private void addBotMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText("Bot: " + message);
        textView.setBackgroundColor(0xFFE0E0E0);
        textView.setPadding(16, 8, 16, 8);
        messageContainer.addView(textView);
    }
}
