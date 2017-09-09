package nl.robenanita.googlemapstest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Rob Verhoef on 12-1-14.
 */
public class TCPClient {

    private String serverMessage;
    /**
     * Specify the Server Ip Address here. Whereas our Socket Server is started.
     * */
    private String TAG = "GooglemapsTest";
    public int SERVERPORT = 5000;
    public String SERVERIP = "192.168.2.8";
    public String WELCOMEMESSAGE = "<root><Connect/></root>";

    public boolean isConnected()
    {
        return mRun;
    }

    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    private PrintWriter out = null;
    private BufferedReader in = null;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(final OnMessageReceived listener)
    {
        mMessageListener = listener;
    }

    private class sendAsync extends AsyncTask<String, String, String>
    {
        public String sendString;

        @Override
        protected String doInBackground(String... strings) {
            sendMessage(sendString);
            return null;
        }

        private void sendMessage(String message){
            if (out != null && !out.checkError()) {
                Log.i(TAG, "Sending async: " + message);
                out.print(message);
                out.flush();
            }
        }
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
//        if (out != null && !out.checkError()) {
//            Log.i(TAG, "Sending mainthread: " + message);
//            out.print(message);
//            out.flush();
//        }
        sendAsync send = new sendAsync();
        send.sendString = message;
        send.execute();
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            //Log.e(TAG, "SI: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket();//serverAddr, SERVERPORT);
            SocketAddress s = new InetSocketAddress(serverAddr, SERVERPORT);

            socket.connect(s, 2000);
            if (!socket.isConnected()) throw new Exception("Connection Timeout error");
            try {
                //Log.e(TAG, "SI: Sending: " + WELCOMEMESSAGE);

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.print(WELCOMEMESSAGE);
                out.flush();



                //Log.e(TAG, "SI: Message Send: " + WELCOMEMESSAGE);

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    //Log.i(TAG, "Receiving data from server");
                    serverMessage = in.readLine();
                    //int i = in.read();
                    //Log.i(TAG, "Received data from server : ");

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                        //Log.e(TAG, "S: Received Message: '" + serverMessage + "'");
                    }
                    serverMessage = null;
                }
            }
            catch (Exception e)
            {
                //Log.e(TAG, "SendReceive: Error", e);
                e.printStackTrace();
                if (mMessageListener != null) {
                    //call the method messageReceived from MyActivity class
                    serverMessage = "<ERROR>Send Receive Error</ERROR>";
                    mMessageListener.messageReceived(serverMessage);
                    //Log.e(TAG, "S: Received Message: '" + serverMessage + "'");
                }
            }
            finally
            {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {

            //Log.e(TAG, "ConnectError: Error", e);
            if (mMessageListener != null) {
                //call the method messageReceived from MyActivity class
                serverMessage = "<ERROR>Connection Error</ERROR>";
                mMessageListener.messageReceived(serverMessage);
                //Log.e(TAG, "S: Received Message: '" + serverMessage + "'");
            }
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }


    // Static helper methods
    static public boolean isServerReachable(String url) {
        try {
            InetAddress.getByName(url).isReachable(3000); //Replace with your name
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
