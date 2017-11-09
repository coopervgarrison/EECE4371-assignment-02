package example.com.tictactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;

public class TTTActivity extends AppCompatActivity {

    // TAG for logging
    private static final String TAG = "TTTActivity";

    // server to connect to
    protected static final int GROUPCAST_PORT = 20000;
    protected static final String GROUPCAST_SERVER = "10.0.2.2";

    // networking
    Socket mSocket = null;
    BufferedReader mIn = null;
    PrintWriter mOut = null;
    boolean mConnected = false;

    // UI elements
    Button mBoard[][] = new Button[3][3];
    Button mConnectButton = null;
    EditText mNameEditText = null;

    //Game details
    String playerName;
    String playerLetter = "O";
    int playerNum = 2;
    int playerColor = Color.BLUE;
    int numMoves;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttt);

        // find UI elements defined mIn xml
        mConnectButton = (Button) this.findViewById(R.id.bConnect);
        mNameEditText = (EditText) this.findViewById(R.id.etName);
        mBoard[0][0] = (Button) this.findViewById(R.id.b00);
        mBoard[0][1] = (Button) this.findViewById(R.id.b01);
        mBoard[0][2] = (Button) this.findViewById(R.id.b02);
        mBoard[1][0] = (Button) this.findViewById(R.id.b10);
        mBoard[1][1] = (Button) this.findViewById(R.id.b11);
        mBoard[1][2] = (Button) this.findViewById(R.id.b12);
        mBoard[2][0] = (Button) this.findViewById(R.id.b20);
        mBoard[2][1] = (Button) this.findViewById(R.id.b21);
        mBoard[2][2] = (Button) this.findViewById(R.id.b22);

        // hide login controls
        hideLoginControls();

        // make the mBoard non-clickable
        enableBoardClick();

        // hide the mBoard
        hideBoard();

        // assign OnClickListener to connect button
        mConnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = mNameEditText.getText().toString();
                // sanity check: make sure that the name does not start with an @ character
                if (name.startsWith("@")) {
                    Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
                }
                else {
                    playerName = name;
                    send("NAME," + mNameEditText.getText());
                    send("LIST,USERS");
                    try {
                        while (mConnected) {
                            String nameList = mIn.readLine();
                            if (nameList == null) { // other side closed the connection
                                break;
                            }
                            if (nameList.endsWith(playerName)) {
                                playerNum = 2;
                                playerLetter = "O";
                                playerColor = Color.BLUE;
                            }
                            else {
                                playerNum = 1;
                                playerLetter = "X";
                                playerColor = Color.RED;
                            }
                        }
                    } catch (UnknownHostException e1) {
                        Log.i(TAG, "UnknownHostException mIn receive task");
                    } catch (IOException e1) {
                        Log.i(TAG, "IOException mIn receive task");
                    }
                }
            }
        });


        // assign a common OnClickListener to all mBoard buttons
        View.OnClickListener boardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x, y;
                switch (v.getId()) {
                    case R.id.b00:
                        x = 0;
                        y = 0;
                        if (mBoard[0][0].getText().equals("")) {
                            mBoard[0][0].setText(playerLetter);
                            mBoard[0][0].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b01:
                        x = 0;
                        y = 0;
                        if (mBoard[0][1].getText().equals("")) {
                            mBoard[0][1].setText(playerLetter);
                            mBoard[0][1].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b02:
                        x = 0;
                        y = 0;
                        if (mBoard[0][2].getText().equals("")) {
                            mBoard[0][2].setText(playerLetter);
                            mBoard[0][2].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b10:
                        x = 0;
                        y = 0;
                        if (mBoard[1][0].getText().equals("")) {
                            mBoard[1][0].setText(playerLetter);
                            mBoard[1][0].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b11:
                        x = 0;
                        y = 0;
                        if (mBoard[1][1].getText().equals("")) {
                            mBoard[1][1].setText(playerLetter);
                            mBoard[1][1].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b12:
                        x = 0;
                        y = 0;
                        if (mBoard[1][2].getText().equals("")) {
                            mBoard[1][2].setText(playerLetter);
                            mBoard[1][2].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b20:
                        x = 0;
                        y = 0;
                        if (mBoard[2][0].getText().equals("")) {
                            mBoard[2][0].setText(playerLetter);
                            mBoard[2][0].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b21:
                        x = 0;
                        y = 0;
                        if (mBoard[2][1].getText().equals("")) {
                            mBoard[2][1].setText(playerLetter);
                            mBoard[2][1].setTextColor(playerColor);
                        }
                        break;
                    case R.id.b22:
                        x = 0;
                        y = 0;
                        if (mBoard[2][2].getText().equals("")) {
                            mBoard[2][2].setText(playerLetter);
                            mBoard[2][2].setTextColor(playerColor);
                        }
                        break;


                    default:
                        break;
                }
            }
        };

        // assign OnClickListeners to mBoard buttons
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                mBoard[x][y].setOnClickListener(boardClickListener);
            }
        }

        // start the AsyncTask that connects to the server
        // and listens to whatever the server is sending to us
        connect();
    }


    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy called");
        disconnect();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle menu click events
        if (item.getItemId() == R.id.exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ttt, menu);
        return true;
    }




    /***************************************************************************/
    /********* Networking ******************************************************/
    /***************************************************************************/

    /**
     * Connect to the server. This method is safe to call from the UI thread.
     */
    void connect() {
        new AsyncTask<Void, Void, String>() {
            String errorMsg = null;

            @Override
            protected String doInBackground(Void... args) {
                Log.i(TAG, "Connect task started");
                try {
                    mConnected = false;
                    mSocket = new Socket(GROUPCAST_SERVER, GROUPCAST_PORT);
                    Log.i(TAG, "Socket created");
                    mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    mOut = new PrintWriter(mSocket.getOutputStream());

                    mConnected = true;
                    Log.i(TAG, "Input and output streams ready");

                } catch (UnknownHostException e1) {
                    errorMsg = e1.getMessage();
                } catch (IOException e1) {
                    errorMsg = e1.getMessage();
                    try {
                        if (mOut != null) {
                            mOut.close();
                        }
                        if (mSocket != null) {
                            mSocket.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
                Log.i(TAG, "Connect task finished");
                return errorMsg;
            }

            @Override
            protected void onPostExecute(String errorMsg) {
                if (errorMsg == null) {
                    Toast.makeText(getApplicationContext(), "Connected to server", Toast.LENGTH_SHORT).show();

                    hideConnectingText();
                    showLoginControls();

                    // start receiving
                    receive();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    // can't connect: close the activity
                    finish();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Start receiving one-line messages over the TCP connection. Received lines are
     * handled mIn the onProgressUpdate method which runs on the UI thread.
     * This method is automatically called after a connection has been established.
     */

    void receive() {
        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                Log.i(TAG, "Receive task started");
                try {
                    while (mConnected) {
                        String msg = mIn.readLine();
                        if (msg == null) { // other side closed the connection
                            break;
                        }
                        publishProgress(msg);
                    }
                } catch (UnknownHostException e1) {
                    Log.i(TAG, "UnknownHostException mIn receive task");
                } catch (IOException e1) {
                    Log.i(TAG, "IOException mIn receive task");
                } finally {
                    mConnected = false;
                    try {
                        if (mOut != null) {
                            mOut.close();
                        }
                        if (mSocket != null) {
                            mSocket.close();
                        }
                    } catch (IOException e) {
                    }
                }
                Log.i(TAG, "Receive task finished");
                return null;
            }

            @Override
            protected void onProgressUpdate(String... lines) {
                // the message received from the server is
                // guaranteed to be not null
                String msg = lines[0];

                // TODO: act on messages received from the server
                if (msg.startsWith("+OK,NAME")) {
                    hideLoginControls();
                    showBoard();
                    return;
                }

                if (msg.startsWith("+ERROR,NAME")) {
                    Toast.makeText(getApplicationContext(), msg.substring("+ERROR,NAME,".length()), Toast.LENGTH_SHORT).show();
                    return;
                }

                // [ ... and so on for other kinds of messages]


                // if we haven't returned yet, tell the user that we have an unhandled message
                Toast.makeText(getApplicationContext(), "Unhandled message: " + msg, Toast.LENGTH_SHORT).show();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * Disconnect from the server
     */
    void disconnect() {
        new Thread() {
            @Override
            public void run() {
                if (mConnected) {
                    mConnected = false;
                }
                // make sure that we close the output, not the input
                if (mOut != null) {
                    mOut.print("BYE");
                    mOut.flush();
                    mOut.close();
                }
                // mIn some rare cases, mOut can be null, so we need to close the mSocket itself
                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch(IOException ignored) {}
                }


                Log.i(TAG, "Disconnect task finished");
            }
        }.start();
    }

    /**
     * Send a one-line message to the server over the TCP connection. This
     * method is safe to call from the UI thread.
     *
     * @param msg
     *            The message to be sent.
     * @return true if sending was successful, false otherwise
     */
    boolean send(String msg) {
        if (!mConnected) {
            Log.i(TAG, "can't send: not mConnected");
            return false;
        }

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... msg) {
                Log.i(TAG, "sending: " + msg[0]);
                mOut.println(msg[0]);
                return mOut.checkError();
            }

            @Override
            protected void onPostExecute(Boolean error) {
                if (!error) {
                    Toast.makeText(getApplicationContext(), "Message sent to server", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error sending message to server", Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg);

        return true;
    }

    /***************************************************************************/
    /***** UI related methods **************************************************/
    /***************************************************************************/

    /**
     * Hide the "connecting to server" text
     */
    void hideConnectingText() {
        findViewById(R.id.tvConnecting).setVisibility(View.GONE);
    }

    /**
     * Show the "connecting to server" text
     */
    void showConnectingText() {
        findViewById(R.id.tvConnecting).setVisibility(View.VISIBLE);
    }

    /**
     * Hide the login controls
     */
    void hideLoginControls() {
        findViewById(R.id.llLoginControls).setVisibility(View.GONE);
    }

    /**
     * Show the login controls
     */
    void showLoginControls() {
        findViewById(R.id.llLoginControls).setVisibility(View.VISIBLE);
    }

    /**
     * Hide the tictactoe mBoard
     */
    void hideBoard() {
        findViewById(R.id.llBoard).setVisibility(View.GONE);
    }

    /**
     * Show the tictactoe mBoard
     */
    void showBoard() {
        findViewById(R.id.llBoard).setVisibility(View.VISIBLE);
    }


    /**
     * Make the buttons of the tictactoe mBoard clickable if they are not marked yet
     */
    void enableBoardClick() {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if ("".equals(mBoard[x][y].getText().toString())) {
                    mBoard[x][y].setEnabled(true);
                }
            }
        }
    }

    /**
     * Make the tictactoe mBoard non-clickable
     */
    void disableBoardClick() {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                mBoard[x][y].setEnabled(false);
            }
        }
    }

    /*
     * Check if anyone has won
     * 0 for no one
     * 1 for player 1
     * 2 for player 2
     * 3 for cat game
     */
    int checkForEnd() {
        int x, y;
        int xCount = 0;
        int oCount = 0;
        //Checks for column wins
        for (x = 0; x < 3; x++) {
            for (y = 0; y < 3; y++) {
                if (mBoard[x][y].getText().equals("X"))
                    xCount++;
                if (mBoard[x][y].getText().equals("O"))
                    oCount++;
                if (xCount == 3)
                    return 1;
                if (oCount == 3)
                    return 2;
            }
            xCount = 0;
            oCount = 0;
        }
        //Checks for row wins
        for (y = 0; y < 3; y++) {
            for (x = 0; x < 3; x++) {
                if (mBoard[x][y].getText().equals("X"))
                    xCount++;
                if (mBoard[x][y].getText().equals("O"))
                    oCount++;
                if (xCount == 3)
                    return 1;
                if (oCount == 3)
                    return 2;
            }
            xCount = 0;
            oCount = 0;
        }
        //Checks for diagonal wins
        y = 0;
        for (x = 0; x < 3; x++) {
            if (mBoard[x][y].getText().equals("X"))
                xCount++;
            if (mBoard[x][y].getText().equals("O"))
                oCount++;
            if (xCount == 3)
                return 1;
            if (oCount == 3)
                return 2;
            y++;
        }
        y = 0;
        for (x = 2; x >= 0; x--) {
            if (mBoard[x][y].getText().equals("X"))
                xCount++;
            if (mBoard[x][y].getText().equals("O"))
                oCount++;
            if (xCount == 3)
                return 1;
            if (oCount == 3)
                return 2;
            y++;
        }
        //Check for cat game
        if (playerNum == 1 && numMoves == 5) {
            return 3;
        }
        if (playerNum == 2 && numMoves == 4) {
            return 3;
        }
        //Game hasn't ended yet
        return 0;
    }
}
