import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Bob on 2016-02-06.
 */
public class SAWServer {
    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt( args[0]));
        new HandleThread(serverSocket.accept()).start();
    }

}

class HandleThread extends Thread{
    Socket socket = null;
    int lastAck;
    public HandleThread(Socket socket){
        this.socket = socket;
        this.lastAck = 0;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            int noPacket = Integer.parseInt(bufferedReader.readLine());
            for(int i = 0; i < noPacket; i++){
                int sent = Integer.parseInt(bufferedReader.readLine());
                if(sent == lastAck + 1){
                    dataOutputStream.write(sent);
                    lastAck++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
