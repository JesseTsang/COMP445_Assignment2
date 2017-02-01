import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;


public class Httpfs 
{
	private final static int LISTENING_PORT = 8007;
	private final static File DIRECTORY = new File("C:\\Users\\Jesse\\workspace\\COMP445 - Assignment 2\\resources");

	public static void main(String[] args) throws Exception 
	{
		Httpfs server = new Httpfs();
		server.listenAndServe(LISTENING_PORT);	
	}

	private void listenAndServe(int port) throws Exception 
	{
		ServerSocket listener = new ServerSocket(port);
		Socket connection;
			
		System.out.println("Httpfs: Listening on port : " + port + " and awaiting for clients.");
			
		while(true)
		{
			connection = listener.accept();
				
			System.out.println("Httpfs: Client connected. Creating separating thread.");
			
			new ConcurrentConnection (DIRECTORY, connection);
		}		
	}
}
