import java.net.*;
import java.io.*;

class stageUno {

    public static void main(String args[]) throws Exception {

        //Create socket and connect to ds-server
        Socket s = new Socket("Localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String serverMessage;
        String username = System.getProperty("user.name");

        //Begin HANDSHAKE method
        //CLIENT SEND HELO
        dout.write(("HELO\n").getBytes());
        dout.flush();
        //Display content in terminal
        serverMessage = dis.readLine();
        System.out.println("Server = " + serverMessage);

        //SERVER SENDS OK, CLIENT SENDS AUTH
        dout.write(("AUTH " + username + "\n").getBytes());
        dout.flush();
        //Display content in terminal
        serverMessage = dis.readLine();
        System.out.println("Server = " + serverMessage);

        //SERVER SENDS OK, CLIENT SENDS REDY
        dout.write(("REDY\n").getBytes());
        dout.flush();
        //Display content in terminal
        serverMessage = dis.readLine();
        System.out.println("Server = " + serverMessage);


        dout.write(("GETS ALL\n").getBytes());
        dout.flush();
        serverMessage = dis.readLine();
        System.out.println("Server = " + serverMessage);



        //Was off due to COVID, so i'm a bit lost. Hopefully can catch up soon :/



         dout.write(("OK\n").getBytes());
         dout.flush();

       //End communication and close channel
         dis.close();
         dout.close();
         s.close();
    }
}