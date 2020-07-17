package LightCycleMod;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Stores data that is mod specific
 */

public class DataStorage{

	private final String json_path          = "lightcyclemod.txt";
	private final String error_path         = "lasterror.txt";
	private final Logger logger             = LogManager.getLogger();
	public  final int    DEFAULT_CYCLE_TIME = LightCycleFunctions.instance.DEFAULT_CYCLE_TIME;
	
	//Writes the light cycle time to a file.
	public void write_json(double new_length)
	{
		try {
			File file = new File(json_path);
			FileWriter filewriter = new FileWriter(file);
		    filewriter.write( Double.toString( new_length ) );
		    filewriter.close();
		}
		catch(Exception e)
		{
		    logger.info( "Could not print to file: " + e );
			try {
			    File file = new File(error_path);
			    file.createNewFile();
				FileWriter filewriter = new FileWriter(file);
                filewriter.write("Cannot write JSON to file: " + e);
				filewriter.close();
			}
			catch(Exception ee)
			{
				logger.info( "Could not write error message for file writer: " + ee);
			}
		}
	}

	
	//Reads the light cycle time from a file named {json_path}
	//Only reads one line that contains the day length stored as a double
	public double read_json()
	{
	    //Try to read the file
		try
		{
			File file = new File(json_path);
			Scanner file_reader = new Scanner(file);
			String data = file_reader.nextLine();
            file_reader.close();
            
			return Double.parseDouble( data );
		}
		//If it fails, either the file doesn't exist, the formatting was wrong, or there was another error somewhere else
		//I should return the default value on a failure
		catch (Exception e)
		{
		    logger.info( "Could not read JSON, catching error: " + e );
			try {
				
				//If the file doesn't exist, create a new file and write the default value to it
				if ( e.getCause().equals( new NullPointerException() ) )
				{
	                File file = new File(json_path);
	                file.createNewFile();
                    logger.info("Could not read file, sending default time to server.");
	                this.write_json( DEFAULT_CYCLE_TIME );
				}
				
			}
			catch(Exception ee)
			{
	            logger.info( "Could not print error message for file deletion & creation: " + e );
                logger.info("Sending default time to server.");
                this.write_json( DEFAULT_CYCLE_TIME );
			}
            return DEFAULT_CYCLE_TIME;
		}
	}
	
	public String get_json_path()
	{
		return json_path;
	}
	
	public String get_error_path()
	{
		return error_path;
	}
	
}
