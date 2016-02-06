import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Bob on 2016-02-06.
 * a- Use Scanner to read the number of packets from the user. This can be done as
 Scanner scr = new Scanner(System.in);
 Use nextInt() method of scanner to read the number of packets from the user, and store it in a variable, noPackets.
 b- Send that number to the server
 c- Define another variable, sent, that keeps track of the packet in the sliding window. Initially, sent is set to 1
 d- Send the packet number (sent) to the server and wait for an acknowledgment.
 e- Read the acknowledgement from the server, if the received number is equal to sent, slide the window by one position, i.e. sent = sent + 1.
 f- Continue the process until all packets have been sent.

 */
public class SAWClient {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 9876);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

        Scanner scr = new Scanner(System.in);
        int noPackets = scr.nextInt();
        int packet;

        for(packet=1;packet<=noPackets;packet++)
        {
            writer.write(packet);
            String temp;
            temp=reader.readLine();
            while(Integer.parseInt(temp)!=packet) {
                temp=reader.readLine();
            }

        }

    }

}
