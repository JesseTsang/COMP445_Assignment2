import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ConcurrentConnection extends Thread
{
	private File serverDirectory;
	Socket serverConnection;

	public ConcurrentConnection(File directory, Socket connection) 
	{
		serverDirectory = directory;
		serverConnection = connection;
		start();
	}
	
	//Each thread will get the command from its client.
	public void run()
	{
		String command = "";
		try 
		{
			System.out.println("CC: Thread Created. Extracting command...");
			
			TextReader incomingStream = new TextReader(serverConnection.getInputStream());
			PrintWriter outgoingStream = new PrintWriter(serverConnection.getOutputStream());
			command = inputStreamToString (incomingStream, command);
	
			System.out.println("CC: Command received. Command is : " + command);
			
			runCommand(command, incomingStream, outgoingStream);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}
	
	private void runCommand(String command, TextReader inStream, PrintWriter outStream) throws Exception 
	{
		command = command.toLowerCase();
		String fileName = "";
		
		if (command.equals("index"))
		{
			getIndex(outStream);	
		}
		
		if (command.startsWith("get"))
		{
			fileName = command.substring(3).trim();
			
			System.out.println("CC: [GET] request received. File name is : " + fileName);
			
			sendFile(fileName, outStream);			
		}
		
		if (command.startsWith("post"))
		{
			fileName = command.substring(4).trim();
			
			System.out.println("CC: [POST] request received. Incoming file name is : " + fileName);
			
			getFile(fileName, inStream, outStream);	
		}	
	}

	private void getIndex(PrintWriter outStream) throws Exception 
	{	
		String[] fileList = serverDirectory.list();
        for (int i = 0; i < fileList.length; i++)
        {
        	outStream.println(fileList[i]);     	
        }      	
        outStream.flush();
        outStream.close();
        
        if (outStream.checkError())
        {
        	throw new Exception("CC: Error while transmitting data.");
        }
	}
	
	//For GET
	private void sendFile(String fileName, PrintWriter outStream) throws Exception 
	{
		File file = new File(serverDirectory,fileName);
	
        if ( (!file.exists()) || file.isDirectory() ) 
        {
        	outStream.println("CC: Error [404] - Requested file do not exist.");
        }
        else 
        {
        	outStream.println("ok");
        	
        	//getContentType(fileName, outStream);
        	
        	System.out.println("CC: [GET] File exists. Sending file to client ...");
        	     	
        	FileReader fr = new FileReader(file); 
        	BufferedReader br = new BufferedReader(fr);
        	
        	String s; 
        	
        	while((s = br.readLine()) != null) 
        	{
        		outStream.println(s);
        	} 
        	fr.close(); 
        	
        	outStream.flush(); 
            outStream.close();
        	
        	System.out.println("CC: [GET] File transfered.");
        }
          
        if (outStream.checkError())
        {
        	throw new Exception("Error while transmitting data.");    	
        }		
	}
	
	private void getContentType(String fileName, PrintWriter outStream) throws Exception 
	{
		/*String extension = "";

		int index = fileName.lastIndexOf('.');
		
		if (index > 0) 
		{
		    extension = fileName.substring(index + 1);
		}
		
		if (extension.equalsIgnoreCase("txt"))
		{
			String[] header = new String[2];
	        for (int i = 0; i < header.length; i++)
	        {
	        	if(i == 1)
	        	{
	        		header[i] = "Content-Type : text/html; charset=utf-8";     		
	        	}
	        	
	        	if(i == 2)
	        	{
	        		header[i] = "Content-Disposition: attachment; filename= " + fileName;     		
	        	}

	        	outStream.println(header[i]);     	
	        }
		}
		  
        outStream.flush();
        outStream.close();
        
        if (outStream.checkError())
        {
        	throw new Exception("CC: Error while transmitting data.");
        }*/	
	}

	//For POST
	private void getFile(String fileName, TextReader inStream, PrintWriter outStream) throws Exception
	{
		System.out.println("CC: [POST] Receiving file: " + fileName);
	
        while (inStream.eof() == false) 
		{
           String line = inStream.getln();
          
           File file = new File(serverDirectory,fileName);
           FileWriter writer = new FileWriter(file, true);
           PrintWriter output = new PrintWriter(writer);

           output.print(line);
           output.close();            
        }
        
        outStream.println("ok");
        outStream.flush(); 
        outStream.close();

        System.out.println("CC: [POST] File uploaded. File name: " + fileName);
        
        if (outStream.checkError())
        {
        	throw new Exception("Error while transmitting data.");    	
        }
	}

	private String inputStreamToString(TextReader incomingStream, String outputString) 
	{	
		try 
		{
			outputString = incomingStream.getln();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return outputString;
	}
}
