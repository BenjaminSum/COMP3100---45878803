package clientTest;

import java.io.*;
import java.net.*;

public class App {
public static void main(String[] args) {
	try {      
		Socket s = new Socket("127.0.0.1", 50000);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				
		//Connection
		dout.write(("HELO\n").getBytes());
		String str = (String)in.readLine();
		System.out.println(str);
		
		//Authentication
		dout.write(("AUTH user.name\n").getBytes());
		str = (String)in.readLine();
		System.out.println(str);
		
		dout.write(("REDY\n").getBytes());
		str = (String)in.readLine();
		System.out.println(str);
		
		dout.write(("GETS Capable 3 500 1000\n").getBytes());
		str = (String)in.readLine();
		System.out.println(str);
							
		dout.write(("OK\n").getBytes());
		str = (String)in.readLine();
		System.out.println(str);
		
		for(int JobID = 0; JobID < 10; JobID++) {
			if(str.equals("NONE")) {
				break;
			}
			dout.write(("OK\n").getBytes());
			str = (String)in.readLine();
			System.out.println(str);
			String[] split = str.split(" ");

			dout.write(("SCHD " + JobID + " super-silk" + "\n").getBytes());
			str = (String)in.readLine();
			System.out.println(str);
			
			dout.write(("REDY\n").getBytes());
			str = (String)in.readLine();
		}
		
		dout.write(("QUIT\n").getBytes());
		str = (String)in.readLine();

		dout.flush();
		dout.close();
		s.close();
		}
		catch(Exception e){System.out.println(e);}
	}
}
