/**
 * Project name: ProcessWeatherDataCSVs
 * Description
 * 
 * @author Annie Grubb 
 * @version 0.1 3/24/24
 */
import edu.duke.*;
import java.io.File;
import java.time.LocalTime;
import org.apache.commons.csv.*;

public class WeatherData {

    /**
     * coldestHourInFile that has one parameter, a CSVParser named parser. 
     * This method returns the CSVRecord 
     * with the coldest temperature in the file 
     *
     * @param  CSVParser named parser
     * @return CSVRecord with the coldest temperature in the file
     */
    public CSVRecord coldestHourInFile(CSVParser parser) {
        CSVRecord coldestRecord = null;
        double currentTemp;
        double lowestTemp = 0;

        //For each row (currentRow) in the CSV File
        for (CSVRecord currentRecord : parser)
        {
            currentTemp = Double.parseDouble(currentRecord.get("TemperatureF"));
            //If largestSoFar is nothing or currentRow’s temperature < coldestRecord so far
            if ((coldestRecord == null)||(currentTemp < lowestTemp))
            {
                // check that temperature is valid
                if (currentTemp != -9999)
                {
                    coldestRecord = currentRecord;
                    lowestTemp = currentTemp;  
                }
            }
        }
        return coldestRecord;
    }

    /**
     * fileWithColdestTemperature that has no parameters. 
     * This method should return a string that is the name of the file 
     * from selected files that has the coldest temperature
     *
     * @param  none
     * @return string that is the name of the file 
     * from selected files that has the coldest temperature
     */
    public String fileWithColdestTemperature() {
        CSVRecord coldestRecord = null;
        CSVRecord currentRecord = null;
        CSVParser coldestParser = null;
        double currentTemp;
        double lowestTemp = 9999;
        File coldestFile = null;
        String coldestFileNameString = "";

        DirectoryResource searchDirectory = new DirectoryResource();
        for (File currentFile : searchDirectory.selectedFiles())
        {
            FileResource currentFileResource = new FileResource(currentFile);
            currentRecord = coldestHourInFile(currentFileResource.getCSVParser());
            currentTemp = Double.parseDouble(currentRecord.get("TemperatureF"));
            if(currentTemp < lowestTemp){
                lowestTemp = currentTemp;
                coldestFile = currentFile;
                coldestRecord = currentRecord;
                coldestFileNameString = currentFile.getName();

                //System.out.println("Coldest temp so far = "  + lowestTemp 
                //                + " observed at: " + coldestRecord.get("DateUTC")
                //                + " in file " + coldestFileNameString);
            }

        }

        // ***
        System.out.println("File with coldest temperature is: " + coldestFileNameString);

        System.out.println("*** coldest temperature on that day was: " + lowestTemp);
        System.out.println("All the Temperatures on the coldest day were: ");

        // print out all hourly temp values for file with coldest temp
        FileResource coldestFileResource = new FileResource(coldestFile);
        coldestParser = coldestFileResource.getCSVParser();
        // ****
        for (CSVRecord currentRecordParser : coldestParser)
        {
            currentTemp = Double.parseDouble(currentRecordParser.get("TemperatureF"));
            // check that temperature is valid
            if (currentTemp != -9999)
            {
                System.out.println(currentRecordParser.get("DateUTC") + ": " + currentTemp);
            }

        }

        return coldestFileNameString;
    }

    /**
     *  lowestHumidityInFile that has one parameter, a CSVParser named parser. 
     *  This method returns the CSVRecord that has the lowest humidity. 
     *  If there is a tie, then return the first such record that was found.
     *
     * @param  CSVParser parser
     * @return CSVRecord that has the lowest humidity.
     */
    public CSVRecord lowestHumidityInFile(CSVParser parser) {
        CSVRecord lowestHumidityRecord = null;
        String curHumidityString;
        double currentHumidity;
        double lowestHumidity = 0;

        //For each row (currentRow) in the CSV File
        for (CSVRecord currentRecord : parser)
        {
            curHumidityString = currentRecord.get("Humidity");
            if (!curHumidityString.equals("N/A")){
                currentHumidity = Double.parseDouble(curHumidityString);
                //If largestSoFar is nothing or currentRow’s humidity < coldestRecord so far
                if ((lowestHumidityRecord == null)||(currentHumidity < lowestHumidity))
                {
                    // check that humidity is valid
                    // if (lowestHumidityRecord != -9999)
                    {
                        lowestHumidityRecord = currentRecord;
                        lowestHumidity = currentHumidity;  
                    }
                }

            }
            else
            {
                System.out.println(" ***** skipping invalid entry!!!");
            }
        }
        //System.out.println("Lowest humidity is : " + lowestHumidity);
        return lowestHumidityRecord;
    }

    /**
     * lowestHumidityInManyFiles 
     * that has no parameters. 
     * This method returns a CSVRecord that has the lowest humidity over all the files
     *
     * @param  none
     * @return CSVRecord that has the lowest humidity over all the files
     */
    public CSVRecord lowestHumidityInManyFiles() {
        CSVRecord lowestHumidityManyFilesCSVRecord = null;
        CSVRecord currentCSVRecord;
        String currentHumidityString;
        double currentHumidity = 9999;
        double lowestHumidity = 9999;

        // select files to look at
        DirectoryResource searchDirectory = new DirectoryResource();
        // for each file
        for (File currentFile : searchDirectory.selectedFiles())
        {
            FileResource currentFileResource = new FileResource(currentFile);
            currentCSVRecord = lowestHumidityInFile(currentFileResource.getCSVParser());
            currentHumidityString = currentCSVRecord.get("Humidity");
            if (!currentHumidityString.equals("N/A"))
            {
                currentHumidity = Double.parseDouble(currentHumidityString);
                if((currentHumidity < lowestHumidity)){
                    lowestHumidity = currentHumidity;
                    lowestHumidityManyFilesCSVRecord = currentCSVRecord;
    
                    System.out.println("Lowest humidity so far = "  + lowestHumidity 
                        + " observed at: " + lowestHumidityManyFilesCSVRecord.get("DateUTC"));
                }
            }
            else
            {
                System.out.println(" *****-****** skipping invalid entry!!!");          
            }

        }

        System.out.println("*** lowest humidity: " + lowestHumidity);

        return lowestHumidityManyFilesCSVRecord;
    }

    /**
     * averageTemperatureInFile that has one parameter, a CSVParser named parser. 
     * This method returns a double that represents the average temperature in the file.
     *
     * @param  CSVParser parser
     * @return double that represents the average temperature in the file
     */
    public double averageTemperatureInFile(CSVParser parser) {
        double averageTempInFile = 9999;
        double runningTotal = 0;
        int numberOfRecords = 0;
        double currentTemp;

        //For each row (currentRow) in the CSV File
        for (CSVRecord currentRecord : parser)
        {
            currentTemp = Double.parseDouble(currentRecord.get("TemperatureF"));
            
            // check that temperature is valid
            if (currentTemp != -9999)
            {
                runningTotal = runningTotal + currentTemp;
                numberOfRecords++;  
            }
        }        

        return (runningTotal / numberOfRecords);

    }
    /**
     * averageTemperatureWithHighHumidityInFile that has two parameters: 
     * a CSVParser named parser and an integer named value. 
     * This method returns a double that represents the average temperature 
     * of only those temperatures when the humidity was greater than or equal to value.
     *
     * @param  
     *          CSVParser parser
     *          integer value
     * @return  
     *          double average temperature of only those temperatures when 
     *          the humidity was greater than or equal to value.
     */
    public double averageTemperatureWithHighHumidityInFile(CSVParser parser, int value) {
        double calculatedValue = -9999;
        double runningTotal = 0;
        int numberOfRecords = 0;
        double currentTemp,currentHumidity;

        //For each row (currentRow) in the CSV File
        for (CSVRecord currentCSVRecord : parser)
        {
            currentTemp = Double.parseDouble(currentCSVRecord.get("TemperatureF"));
            currentHumidity = Double.parseDouble(currentCSVRecord.get("Humidity"));
            
            // check that temperature is valid and humidity is > value
            if ((currentTemp != -9999) &&  (currentHumidity >= value))
            {
                runningTotal = runningTotal + currentTemp;
                numberOfRecords++;  
            }
        }        
        if (numberOfRecords == 0)
        {
            System.out.println("No temperatures with that humidity");   
        }
        return(runningTotal/numberOfRecords);

    }

    /**
     * tester: create your CSVParser and call each of the methods. 
     * Contains test cases with calls to methods.
     *
     * @param  none
     * @return void
     */
    public void tester () {
        double averageTempInFile;
        CSVRecord coldestRecord = null;
        CSVRecord lowestHumidityManyFiles = null;
        CSVParser coldestFileParser = null;
        String coldestFileName = "";

        FileResource file = new FileResource();
        CSVRecord coldestTemp = coldestHourInFile(file.getCSVParser());
        System.out.println("Coldest temp in file = " + Double.parseDouble(coldestTemp.get("TemperatureF")) 
                            + " observed at: " + coldestTemp.get("DateUTC"));

//        coldestFileName = fileWithColdestTemperature();

        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
//        CSVRecord csv = lowestHumidityInFile(parser);
//        System.out.println("Lowest humidity in file = " + Double.parseDouble(csv.get("Humidity")) 
//            + " observed at: " + csv.get("DateUTC"));

        // find lowest humidity in multiple files
        lowestHumidityManyFiles = lowestHumidityInManyFiles();

        fr = new FileResource();
        parser = fr.getCSVParser();
        averageTempInFile = averageTemperatureInFile(parser);
        System.out.println("Average temperature in file = " + averageTempInFile );

        fr = new FileResource();
        parser = fr.getCSVParser();
        averageTempInFile = averageTemperatureWithHighHumidityInFile(parser , 80);
        System.out.println("Average temperature when humdity > 80 is = " + averageTempInFile );

        return;
    }  

    /**
     * main
     *
     * @param  none
     * @return void
     */
    public static void main () {
        LocalTime time = LocalTime.now();
        WeatherData mainRunner = new WeatherData();

        // print out blank line and header for each iteration of this test
        System.out.println("  ");
        System.out.println("main of class WeatherData ****** " + time);

        // Run test cases
        mainRunner.tester();

    }

}
