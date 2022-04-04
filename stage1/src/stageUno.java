import java.io.*;
import java.net.*;

public class stageUno {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1", 50000);
            BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            String sendSTR = "", recieveSTR = ""; // Initialises the String objects used to hold sending and recieved
                                                  // messages

            handshake(sendSTR, recieveSTR, din, dout); // Handshake

            // Initialising variables
            String lType = ""; // Largest Server Name
            int lCore = 0; // Largest Server Core size
            int sCount = 0; // Count of largest server
            boolean checked = false; // Checks if the client has already requested server information

            int job = 0; // jobID always start at 0
            int serverID = 0; // ServerID always start from 0

            while (!recieveSTR.equals("NONE")) {
                // Requesting job information
                sendSTR = "REDY\n";
                dout.write((sendSTR).getBytes());
                dout.flush();

                recieveSTR = din.readLine();
                System.out.println("Server says:" + recieveSTR);

                String[] jobCheck = recieveSTR.split(" ");

                if (checked == false) {
                    // Request server information
                    sendSTR = "GETS All\n";
                    dout.write((sendSTR).getBytes());
                    dout.flush();

                    recieveSTR = din.readLine();
                    System.out.println("Server says:" + recieveSTR);

                    // Finding amount of servers
                    String[] dataArr = recieveSTR.split(" ");
                    int nRecs = Integer.parseInt(dataArr[1]);

                    okACK(sendSTR, recieveSTR, din, dout);

                    // Cataloging Server sizes
                    for (int i = 0; i < nRecs; ++i) {
                        recieveSTR = din.readLine();
                        String[] serverINFO = recieveSTR.split(" ");

                        if (lCore < Integer.parseInt(serverINFO[4])) {
                            lCore = Integer.parseInt(serverINFO[4]);
                            lType = serverINFO[0];
                        }

                        if (lType.equals(serverINFO[0])) {
                            sCount = 1 + Integer.parseInt(serverINFO[1]);
                        }

                        System.out.println("Server says:" + recieveSTR);
                    }
                    System.out.println("Largest Server Type: " + lType + " Cores: " + lCore + " Count " + sCount);

                    okACK(sendSTR, recieveSTR, din, dout);

                    recieveSTR = din.readLine();
                    System.out.println("Server says:" + recieveSTR);

                    checked = true;

                }

                // Reading job message and schedule
                if (jobCheck[0].equals("JOBN")) {
                    // schedule job
                    schdAction(sendSTR, recieveSTR, din, dout, job, lType, serverID);

                    job++; // increments job and server to next appropriate job/server
                    serverID++;
                    if (serverID >= sCount) {
                        serverID = 0;
                    }
                }
            }
            // Quitting and disconnecting from server
            quitAction(sendSTR, recieveSTR, din, dout, s); // quits and closes socket
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // completes handshake to connect with server
    static void handshake(String send, String recieve, BufferedReader din, DataOutputStream dout) {
        try {
            send = "HELO\n";
            dout.write((send).getBytes());
            dout.flush();

            recieve = din.readLine();
            System.out.println("Server says:" + recieve);

            send = "AUTH benjamin\n";
            dout.write((send).getBytes());
            dout.flush();

            recieve = din.readLine();
            System.out.println("Server says:" + recieve);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // sends a quit message and closes socket
    static void quitAction(String send, String recieve, BufferedReader din, DataOutputStream dout, Socket s) {
        try {
            send = "QUIT\n";
            dout.write((send).getBytes());
            dout.flush();

            recieve = din.readLine();
            System.out.println("Server says:" + recieve);

            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // sends a scheduling message
    static void schdAction(String send, String recieve, BufferedReader din, DataOutputStream dout, int job,
            String lType, int serverID) {
        try {
            send = "SCHD " + job + " " + lType + " " + serverID + "\n";
            dout.write((send).getBytes());
            dout.flush();

            recieve = din.readLine();
            System.out.println("Server says:" + recieve);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Sends an OK acknowledment
    static void okACK(String send, String recieve, BufferedReader din, DataOutputStream dout) {
        try {
            send = "OK\n";
            dout.write((send).getBytes());
            dout.flush();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}