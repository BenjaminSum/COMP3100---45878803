import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class stageUno {
    public static void main(String args[]) throws IOException {
        // set up connection
        Socket s = new Socket("localhost", 50000);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String response = ""; //store incoming messages from server
        String[] responseArray = {}; // store split server response as components
        ArrayList<Server> allServers = new ArrayList<Server>(); //store current available servers
        ArrayList<Server> largestServers = new ArrayList<Server>(); //store only largest servers
        Job currentJob = new Job(); //store current job
        Server chosenServer = new Server(); //store server selected for current job

        // hand-shaking 
        response = send("HELO", dout, br);
        response = send("AUTH " + System.getProperty("user.name"), dout, br);
        response = send("REDY", dout, br);

        // main loop - schedule all jobs
        while (true) {
            if(response.contains("NONE")){ // no more jobs - quit
                break;
            }

            // recieved job - store job and get capable servers
            else if (response.contains("JOBN") || response.contains("JOBP")) {
                responseArray = response.split("\s");
                currentJob = new Job(responseArray[1], responseArray[2], responseArray[3], responseArray[4],
                        responseArray[5], responseArray[6]);
                
                // get all server data initially
                if(allServers.isEmpty()){ 
                    response = send("GETS All", dout, br);
                    response = send("OK", dout, br); //to recieve all server data
                    response = send("OK", dout, br); //to recieve '.' at end
                
                    // inner loop - store all server data
                    while(true){
                        String[] server = response.split("\s");
                        if(server.length>1){ // ensure no more data message '.' is not attempted to be added
                            allServers.add(new Server(server[0], server[1], server[3], server[4], server[5], server[6]));
                        }
                        response = br.readLine();
                        if(response.contains(".")){
                            break;
                        }
                    }

                    // get all largest servers for LRR
                    for (Server server : allServers) {
                        if(largestServers.isEmpty()){
                            largestServers.add(server);
                        }
                        else if(server.cores > largestServers.get(0).cores){
                            largestServers.clear();
                            largestServers.add(server);
                        }
                        else if(server.cores == largestServers.get(0).cores){
                            largestServers.add(server);
                        }
                    }
                }

                // choose server for current job 
                chosenServer = largestRoundRobin(currentJob, largestServers);

                //schedule current job and get next job
                response = send("SCHD "+currentJob.id+" "+chosenServer.type+" "+chosenServer.id, dout, br);
                response = send("REDY", dout, br);
            }

            //recieved completed job or server status info - client still ready
            else if(response.contains("JCPL") || response.contains("RESF") || response.contains("RESR")){
                response = send("REDY", dout, br);
            }
            else break; // recieved unhandled message from server
        }

        // quit server and close connection
        response = send("QUIT", dout, br);
        dout.close();
        s.close();

    }

    public static String send(String message, DataOutputStream dout, BufferedReader br) throws IOException{
        // handles all sending messages to server and recieveing responces
        dout.write((message + "\n").getBytes());
        String response = br.readLine();
        System.out.println("C: "+message);
        System.out.println("S: "+response);
        return response;
    }

    public static Server largestRoundRobin(Job job, ArrayList<Server> servers){
        // implements 'Largest Round Robin' by scheduling each job on the modulo of its id and the ammount of servers
        return servers.get(job.id%servers.size());
    }
}