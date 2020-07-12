package LightCycleMod;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/*
 * Stores data that is mod specific
 */

public class DataStorage{

	String json_path = "lightcyclemod.txt";
	String error_path = "lasterror.txt";
	
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
		return (long)0;
	}
	
}
