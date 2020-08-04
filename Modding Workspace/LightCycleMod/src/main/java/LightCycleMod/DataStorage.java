package LightCycleMod;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Stores data that is mod specific
 */

public class DataStorage
{

    private static final String json_path       = "config/lightcyclemod/daylength.txt";
    private static final String error_path      = "config/lightcyclemod/lasterror.txt";
    private static final Logger logger          = LogManager.getLogger();
    public  static final int DEFAULT_CYCLE_TIME = LightCycleFunctions.instance.DEFAULT_CYCLE_TIME;

    //These will just be a single line txt file since I don't need to store more than a single value
    public static void write_json( double new_length )
    {            
        File file = new File( json_path );
        if ( !file.exists() )
            create_files(file);
        
        try
        {
            FileWriter filewriter = new FileWriter( file );
            filewriter.write( Double.toString( new_length ) );
            filewriter.close();
        }
        catch ( Exception e )
        {
            logger.info( "Could not print to file: " + e );
        }
    }
    
    public static double read_json()
    {
        try
        {
            File    file        = new File( json_path );
            Scanner file_reader = new Scanner( file );
            String  data        = file_reader.nextLine();
            file_reader.close();
            return Double.parseDouble( data );
        }
        catch ( Exception e )
        {
            logger.info( "Could not read " + json_path );
        }
        write_json( DEFAULT_CYCLE_TIME );
        return DEFAULT_CYCLE_TIME;
    }
    
    private static void create_files( File file )
    {
        try
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        catch ( Exception e)
        {
            logger.info( "Could not create new files to write data." );
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
