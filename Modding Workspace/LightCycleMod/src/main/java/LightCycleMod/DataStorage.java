package LightCycleMod;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/*
 * Stores data that is mod specific
 */

public class DataStorage{

	private final String json_path  = "lightcyclemod.txt";
	private final String error_path = "lasterror.txt";
	
	//Writes the light cycle time to a file.
	//TODO: Update to receive an object. Write the object.
	//TODO: Make error catching cleaner
	public void write_json(double inc_time_by)
	{
		try {
			File file = new File(json_path);
			FileWriter filewriter = new FileWriter(file);
			filewriter.write( Double.toString(inc_time_by) );
			filewriter.close();
		}
		catch(Exception e)
		{
		
			try {
				File file = new File(error_path);
				file.createNewFile();
				FileWriter filewriter = new FileWriter(file);
				filewriter.write("Cannot write JSON to file: " + e);
				filewriter.close();
			}
			catch(Exception ee)
			{
				
			}
			
		}
		
	}
	
	//Reads the light cycle time from a file named {json_path}
	//TODO: Make error catching cleaner
	public double read_json()
	{
		try
		{
			File file = new File(json_path);
			Scanner file_reader = new Scanner(file);
			
			while (file_reader.hasNextLine())
			{
				String data = file_reader.nextLine();
				return Double.parseDouble( data ); 
			}
			
		}
		catch (Exception e)
		{
			try {
				File file = new File(json_path);
				file.createNewFile();
			}
			catch(Exception ee)
			{
				
			}
			
		}
		return 0.0;
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
