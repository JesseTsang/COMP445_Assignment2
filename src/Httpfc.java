
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Httpfc 
{
	private final static int CONNECTING_PORT = 8007;
	private final static String SERVER_ADDRESS = "localhost";
	
	private String command;     
	private String incomingFile; //Download file name
	private String outgoingFile; //Upload file name
	
	
    public static void main(String[] args) throws Exception 
    {
    	System.out.println("Httpfc: Client starting ...");
		new Httpfc().doMain(args);
    	    
    }
    
    public void doMain(String[] args) throws Exception
    {
    	CmdLineValues values = new CmdLineValues(args);
    	CmdLineParser parser = new CmdLineParser(values);

        try 
        {
            parser.parseArgument(args);
        } 
        catch (CmdLineException e) 
        {
            System.exit(1);
        }
        
        command = values.getCommand();
        incomingFile = values.getIncomingFile();
        outgoingFile = values.getOutgoingFile();
        
        /*System.out.println("command is : " + command);
        System.out.println("incomingFile is : " + incomingFile);
        System.out.println("outgoingFile is : " + outgoingFile);*/
        
        runClient(SERVER_ADDRESS, CONNECTING_PORT);    
    }

	private void runClient(String host, int port) throws Exception 
	{	
		//Opening connection to server
		Socket connection;
		TextReader incomingStream = null;
		PrintWriter outgoingStream = null;
		
		connection = new Socket(host, port);
		incomingStream = new TextReader(connection.getInputStream());
		outgoingStream = new PrintWriter(connection.getOutputStream());
	
		sendCommand(host, port, outgoingStream);
		readResponse(incomingStream, outgoingStream);
		
		connection.close();
	}

	private void sendCommand(String host, int port, PrintWriter outgoingStream) throws Exception 
	{
		String commandStream;
		/*
		 * 1. Open Connection
		 * 2. Depends on the COMMAND, do stuff (with separate methods)
		 */
		if (command.equalsIgnoreCase("INDEX"))
		{
			commandStream = command;
			outgoingStream.println(command);
			outgoingStream.flush();
			
			System.out.println("Httpfc: Sending out command: " + commandStream);
		}
			
		if (command.equalsIgnoreCase("GET"))
		{
			commandStream = command + incomingFile;
			outgoingStream.println(commandStream);
			outgoingStream.flush();
			
			System.out.println("Httpfc: Sending out command: " + commandStream);
		}
			
		if (command.equalsIgnoreCase("POST"))
		{
			commandStream = command + outgoingFile;
			outgoingStream.println(commandStream);
			outgoingStream.flush();
				
			System.out.println("Httpfc: Sending out command: " + commandStream);
			
			
			File file = new File(outgoingFile);
			
	        if ( (!file.exists()) || file.isDirectory() ) 
	        {
	        	outgoingStream.println("Httpfc: Error [404] - File do not exist. Unable to send.");
	        }
	        else 
	        {
	        	System.out.println("Httpfc: [POST] File exists. Sending file to server ...");
	        	     	
	        	FileReader fr = new FileReader(file); 
	        	BufferedReader br = new BufferedReader(fr);
	        	
	        	String s; 
	        	
	        	while((s = br.readLine()) != null) 
	        	{
	        		outgoingStream.println(s);
	        	} 
	        	fr.close(); 
	        	
	        	outgoingStream.flush(); 
	        	outgoingStream.close();
	        	
	        	System.out.println("Httpfc: [POST] File transfered.");
	        }
	          
	        if (outgoingStream.checkError())
	        {
	        	throw new Exception("Error while transmitting data.");    	
	        }
		}
	}
	
	private void readResponse(TextReader incomingStream, PrintWriter outgoingStream) throws Exception 
	{
		if (command.equalsIgnoreCase("INDEX"))
		{
			System.out.println("Httpfc: Below is the current file list from server ---");
            
			while (incomingStream.eof() == false) 
			{
               String line = incomingStream.getln();
               System.out.println("   " + line);
            }
					
		}
		
		if (command.equalsIgnoreCase("GET"))
		{
			System.out.println("Httpfc: Awaiting [GET] response from server ...");
			
			String message = incomingStream.getln();
            
			if (!message.equals("ok")) 
			{
               System.out.println("File not found on server.");
               return;
            }
			
			System.out.println(message);

            System.out.println("Httpfc: Receiving file: " + incomingFile);
        
            while (incomingStream.eof() == false) 
			{
               String line = incomingStream.getln();
              
               File file = new File(incomingFile);
               FileWriter writer = new FileWriter(file, true);
               PrintWriter output = new PrintWriter(writer);
               output.print(line);
               output.close();            
            }

            System.out.println("Httpfc: File downloaded. File name: " + incomingFile);

		}
		
		if (command.equalsIgnoreCase("POST"))
		{
			System.out.println("Httpfc: Awaiting [POST] response from server ...");
			
			System.out.println("File transmitted to server sucessfully.");
			
			//String message = incomingStream.getln();
            
			//System.out.println(message);
			
			/*if (message.equals("received")) 
			{
               System.out.println("File transmitted to server sucessfully.");
               return;
            }*/
		}
		
		
		
	}
    
}
