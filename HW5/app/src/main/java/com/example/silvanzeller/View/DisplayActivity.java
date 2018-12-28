package com.example.silvanzeller.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.silvanzeller.hangmanclient.R;

import java.io.IOException;

import com.example.silvanzeller.Controller.ClientController;
import com.example.silvanzeller.Net.OutputHandler;

public class DisplayActivity extends AppCompatActivity {

    public int SERVER_PORT = 8080;
    public String START = "start";
    public String EMPTY = "";
    public String WORD = "word";
    public String SPACE = " ";
    public String EXIT = "exit";
    public String SEPARATOR = "#";

    public Button send1, send2, start, retry, exit;
    public EditText editText1, editText2;
    public TextView textView;
    public String messageOnDevice;

    private boolean gameStarted = false;


    //onCreate is the first method called
    //it is called with a saved state of the instance
    //sets up the first view and starts the game
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        startGame();
    }

    //startGame creates a new Thread for the ClientInterpreter
    //this allows for a responsive UI while network connections are handled, e.g. by exchanging messages
    private void startGame() {
        Thread UIThread = new Thread(new ClientInterpreter(SERVER_PORT));
        UIThread.start();
    }

    //ClientInterpreter implements the Runnable interface for handling two sorts of activities in parallel:
    //1) Socket handling is handled by the ClientController instance
    //2) Displaying messages and handling user interaction is handled in the ConsoleOutput instance

    private class ClientInterpreter implements Runnable {

        private final int serverPort;
        private boolean threadRunning = false;
        private ClientController controller;
        private ConsoleOutput screenHandler;

        //ClientInterpreter is responsible for user actions
        //Is called with a port which connects to a ServerSocket
        public ClientInterpreter(int serverPort) {
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            threadRunning = true;
            controller = new ClientController();
            screenHandler = new ConsoleOutput();

            controller.connect(serverPort, screenHandler);

            //First, custom buttons and text fields are set up
            //Thereafter, buttons are configured with their associated actions
            while (threadRunning) {
                send1 = findViewById(R.id.gameButtonSend1);
                send2 = findViewById(R.id.gameButtonSend2);
                retry = findViewById(R.id.gameButtonRetry);
                exit = findViewById(R.id.gameButtonExit);
                start = findViewById(R.id.startButton);
                editText1 = findViewById(R.id.editTextGame1);
                editText2 = findViewById(R.id.editTextGame2);


                //The start button in the app
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            controller.sendMessage(START);
                            start.setVisibility(View.GONE);
                            gameStarted = true;
                        } catch (IOException e) {
                            System.out.println("Could not send message " + START);
                            e.printStackTrace();
                        }
                    }
                });


                //The "send guess (char)" button in the app
                send1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (gameStarted) {
                                String message = editText1.getText().toString().toLowerCase();
                                if (message.length() == 1) {
                                    char guessChar = message.charAt(0);
                                    if (Character.isLetter(guessChar)) {
                                        controller.sendMessage(message);
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "You can only enter letters!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else if (message.length() == 0) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter a letter!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter one single letter!",
                                            Toast.LENGTH_LONG).show();
                                }
                                editText1.setText(EMPTY); //clears the text field
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                //The "send complete word (String)" button in the app
                send2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (gameStarted) {
                                String message = editText2.getText().toString().toLowerCase();
                                if (message.indexOf(SPACE) != -1) {
                                    Toast.makeText(getApplicationContext(),
                                            "The words searched do not contain whitespace, only letters!",
                                            Toast.LENGTH_LONG).show();
                                } else if (message.length() == 0) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter a word!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    message = WORD + SPACE + message;
                                    controller.sendMessage(message);
                                }
                                editText2.setText(EMPTY); //clears the text field
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                //Starts a new game
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (gameStarted) {
                                controller.sendMessage(START);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                //Exits the game
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (gameStarted) {
                                controller.sendMessage(EXIT);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        threadRunning = false; //exits the while loop
                        gameStarted = false;
                        getStartView();
                    }
                });
            }

        }
    }

    private void getStartView(){
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Responsible for the display of messages in corresponding fields in the app
    private class ConsoleOutput implements OutputHandler{

        @Override
        public void messageOnScreen(String message) {
            printMessage(message);
        }

        synchronized void printMessage(String message) {
            if(!message.isEmpty()){
                messageOnDevice = message;
                //Runs a new thread on the UI
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String[] wholeMessage = messageOnDevice.split(SEPARATOR);
                        if(wholeMessage[0].startsWith(WORD)){
                            String word = wholeMessage[0];
                            String attempt = wholeMessage[1];
                            String score = wholeMessage[2];
                            textView = findViewById(R.id.textViewGameWord);
                            textView.setText(word);
                            textView = findViewById(R.id.textViewGameAttempt);
                            textView.setText(attempt);
                            textView = findViewById(R.id.textViewGameScore);
                            textView.setText(score);
                        } else if (wholeMessage.length == 2){
                            String notify = wholeMessage[0];
                            String score = wholeMessage[1];
                            textView = findViewById(R.id.textViewGameWord);
                            textView.setText(notify);
                            textView = findViewById(R.id.textViewGameAttempt);
                            textView.setText(EMPTY);
                            textView = findViewById(R.id.textViewGameScore);
                            textView.setText(score);
                        } else {
                            System.out.println("Unknown message received");
                        }
                    }
                });
            }
        }
    }
}
