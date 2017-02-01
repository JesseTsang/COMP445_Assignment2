import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Localizable;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

public class CmdLineValues
{
	@Argument (required = false, index = 0)
	private String command = "";
	@Option(name = "-d", forbids={"-u"})
	private String incomingFile;
	@Option(name = "-u", forbids={"-d"})
	private String outgoingFile;
	/*@Option (name = "-help", help = true, usage = "Help message.")
	private boolean helpTerm;*/
	
	
	public CmdLineValues (String... args)
	{
		CmdLineParser parser = new CmdLineParser(this, ParserProperties.defaults().withUsageWidth(80));

        try 
        {
        	parser.parseArgument(args);

            if ((!command.equalsIgnoreCase("GET") && 
            	 !command.equalsIgnoreCase("POST") && 
            	 !command.equalsIgnoreCase("INDEX")) ||
            	  command == null)
            {	
                throw new CmdLineException(parser, new Message("Error: Valid command is INDEX || GET -d [file] || POST -u [file]"));
            }
            
            /*
             * 1. GET without -d [file]
             * 2. GET with -u [file]
             */
            if((command.equalsIgnoreCase("GET") && incomingFile == null) ||
               (command.equalsIgnoreCase("GET") && outgoingFile != null))
            {
            	throw new CmdLineException(parser, new Message("Error: Valid command is GET -d [file]"));        	
            }
            
            /*
             * 1. POST without -u [file]
             * 2. POST with -d [file]
             */
            if((command.equalsIgnoreCase("POST") && outgoingFile == null) ||
               (command.equalsIgnoreCase("POST") && incomingFile != null))
            {
            	throw new CmdLineException(parser, new Message("Error: Valid command is POST -u [file]"));        	
            }
            
            /*
             * 1. INDEX with -u [file]
             * 2. INDEX with -d [file]
             */
            if((command.equalsIgnoreCase("INDEX") && outgoingFile != null) ||
               (command.equalsIgnoreCase("INDEX") && incomingFile != null))
            {
            	throw new CmdLineException(parser, new Message("Error: Valid command is INDEX"));        	
            }
            
        } 
        catch (CmdLineException e)
        {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
		
	}
	
	static class Message implements Localizable 
	{ 
        private String message; 
 
        public String formatWithLocale(Locale locale, Object... args) 
        { 
            return format(args); 
        } 
 
        public String format(Object... args) 
        { 
            return String.format(message, args); 
        } 
 
        public Message(String message) 
        { 
            this.message = message; 
        }
	}

	//Getters methods --------------------------------
	public String getCommand() 
	{
		return command;
	}

	public String getIncomingFile() 
	{
		return incomingFile;
	}

	public String getOutgoingFile() 
	{
		return outgoingFile;
	}

	/*public boolean isHelpTerm() 
	{
		return helpTerm;
	}*/
}
