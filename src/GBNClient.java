import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static jdk.nashorn.internal.objects.NativeMath.min;

/**
 * Created by Bob on 2016-02-06.
 * a- Use Scanner to read the number of packets from the user. This can be done as
 * Scanner scr = new Scanner(System.in);
 * Use nextInt() method of scanner to read the number of packets from the user, and store it in a variable, noPackets.
 * b- Send that number to the server
 * c- Define another variable, sent, that keeps track of the packet in the sliding window. Initially, sent is set to 1
 * d- Send the packet number (sent) to the server and wait for an acknowledgment.
 * e- Read the acknowledgement from the server, if the received number is equal to sent, slide the window by one position, i.e. sent = sent + 1.
 * f- Continue the process until all packets have been sent.
 */
public class GBNClient {
    static int lastAck = 0;


    public static void setLastAck(int lastAck) {
        GBNClient.lastAck = lastAck;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
        AckListenerThread ackListenerThread = new AckListenerThread(socket);
        ackListenerThread.start();
        Scanner scr = new Scanner(System.in);
        //read user input
        int noPackets = scr.nextInt();
        int probError = scr.nextInt();
        int wSize = scr.nextInt();
        int timeOut = scr.nextInt();

        long timer[] = new long[wSize];


        writer.write(noPackets);
        writer.write(probError);
        System.out.println("Number of packet is " + noPackets);
        //Q: at least
        //if the server reply with ack, then the window size increase
        int sent = 1;
        /*for (int i = 0; i <= min(wSize+lastAck, noPackets); i++) {
            timer[(sent-1)%wSize] = System.currentTimeMillis();
            writer.write(sent);
            sent++;
            //String temp;
            //temp=reader.readLine();
            //System.out.println("recieved: "+temp);
            //while(Integer.parseInt(temp)!=packet) {
            //    temp=reader.readLine();
            //    System.out.println("recieved retry: "+temp);
            //}

        }*/
        while (sent < noPackets){
            if(sent - lastAck <= wSize){
                timer[(sent-1)%wSize] = System.currentTimeMillis();
                writer.write(sent);
                sent++;
            }
            long currentTime = System.currentTimeMillis();
            if(currentTime - timer[lastAck%wSize] > timeOut){//sent - 1 is lastAck
                writer.write(lastAck+1);
            }
        }
        while (true){
            if(lastAck == noPackets){
                socket.close();
                ackListenerThread.join();
            }
        }


    }

}

class AckListenerThread extends Thread {
    Socket socket = null;

    AckListenerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!socket.isClosed()) {
                int ack = bufferedReader.read();
                GBNClient.setLastAck(ack);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}