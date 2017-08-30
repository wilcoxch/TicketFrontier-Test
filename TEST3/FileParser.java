/**
 * Created by Chris on 8/29/2017.
 */

import java.util.*;
import java.io.*;
import java.lang.*;


public class FileParser{

    // Class to hold a basic accident record and all relevant parsed information.
    private class AccidentRecord{
	public int tableNum;
	public int subTableNum;
	public int recordNum;
	public String day;
	public int date;
	public String month;
	public int year;
	public int runningDeathTotal;
	public int wounded;

	public AccidentRecord(int t, int s, int r, String d, int da, String m,
			      int y, int rdt, int w){
	    tableNum = t;
	    subTableNum = s;
	    recordNum = r;
	    day = d;
	    date = da;
	    month = m;
	    year = y;
	    runningDeathTotal = rdt;
	    wounded = w;
	}

    }

    // Class to hold a parsed record specific to a year.
    private class YearRecord{
	public int year;
	public int totalAccidents;
	public double averageWound;
	public int totalWound;
	public double averageDeath;
	public int totalDeath;

	public YearRecord(int y, int ta, double aw, int tw, double ad, int td){
	    year = y;
	    totalAccidents = ta;
	    averageWound = aw;
	    totalWound = tw;
	    averageDeath = ad;
	    totalDeath = td;
	}
    }

    // Class to hold a parsed record specific to a day.
    private class DayRecord{
	public String day;
	public int totalAccidents;
	public int totalWound;
	public int totalDeath;

	public DayRecord(String d, int a, int w, int td){
	    day = d;
	    totalAccidents = a;
	    totalWound = w;
	    totalDeath = td;
	}
    }

    // class to compare two Accident Records based on year.
    public class yearComparator implements Comparator<AccidentRecord>{

	public int compare(AccidentRecord a, AccidentRecord b){
	    return a.year - b.year;
	}
    }

    // class to compare two Accident Records based on day.
    public class dayComparator implements Comparator<AccidentRecord>{

	public int compare(AccidentRecord a, AccidentRecord b){
	    return enumerateWeekday(a) - enumerateWeekday(b);
	}
    }


    // ArrayLists to hold the compiled records, Y & W are neccessary to keep sorted lists separate as the weekday list relies on the year list for computing daily deaths.
    private ArrayList<AccidentRecord> compiledRecords = new ArrayList<AccidentRecord>();
    private ArrayList<AccidentRecord> compiledRecordsY = new ArrayList<AccidentRecord>();
    private ArrayList<AccidentRecord> compiledRecordsW = new ArrayList<AccidentRecord>();

    // Arraylists to hold the final compiled Yearly and Weekday records.
    private ArrayList<YearRecord> compiledYearlyRecords = new ArrayList<YearRecord>();
    private ArrayList<DayRecord> compiledWeekdayRecords = new ArrayList<DayRecord>();


    // Constructor that parses a given file name, then passes each line of that file to be parsed, sorted, and grouped.
    public FileParser(String fileName){

	try {
	    FileReader fileReader = new FileReader(fileName);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    while (bufferedReader.ready()) {
		// Read each line and parse it into a record.
		String record = bufferedReader.readLine();
		parseRecord(record);
	    }
	    bufferedReader.close();
	    //Sort and group each record.
	    groupByYear();
	    System.out.println();
	    groupByWeekday();
	}
	catch(FileNotFoundException ex) {
	    System.out.println("Unable to open highscore.txt file");
	}
	catch(IOException ex) {
	    System.out.println("Error reading highscore.txt file");
	}
    }


    // Parses a record line by line, and stores all relevant info into each compiled record list.
    private void parseRecord(String record){
	String[] sp = record.trim().split("\\s+");
	//        for (int i = 0; i < splited.length; i++){
	//            System.out.print(splited[i] + "-");
	//        }

	AccidentRecord toAdd = new AccidentRecord(Integer.parseInt(sp[0]),
						  Integer.parseInt(sp[1]),
						  Integer.parseInt(sp[2]),
						  sp[3],
						  Integer.parseInt(sp[4]),
						  sp[5],
						  Integer.parseInt(sp[6]),
						  Integer.parseInt(sp[7]),
						  Integer.parseInt(sp[9]));
	compiledRecords.add(toAdd);
	compiledRecordsY.add(toAdd);
	compiledRecordsW.add(toAdd);


    }


    // Function to enumerate the weekday of an accident record.
    public int enumerateWeekday(AccidentRecord a){
	if (a.day.equals("Sun"))
	    return 0;
	else if (a.day.equals("Mon"))
	    return 1;
	else if (a.day.equals("Tue"))
	    return 2;
	else if (a.day.equals("Wed"))
	    return 3;
	else if (a.day.equals("Thu"))
	    return 4;
	else if (a.day.equals("Fri"))
	    return 5;
	else return 6;
    }



    // Groups accident records by year and then compiles them into a considated year record.
    public void groupByYear(){
	// Sort on year.
	Collections.sort(compiledRecordsY, new yearComparator());
	// Set up variables to count aggregates.
	int yearCount = compiledRecordsY.get(0).year;
	int totalDistinctAccidents = 0;
	double averageWound = 0.0;
	int totalWound = 0;
	double averageKill = 0.0;
	int totalKill = 0;


	// For each record, compile aggregates while the year stays the same. Once the year is incremented, create a year record and continue.
	for (int i = 0; i < compiledRecordsY.size(); i++){
	    while (yearCount == compiledRecordsY.get(i).year){
		totalDistinctAccidents++;
		totalWound += compiledRecordsY.get(i).wounded;
		totalKill = compiledRecordsY.get(i).runningDeathTotal;
		averageWound = totalWound/totalDistinctAccidents;
		averageKill = totalKill/totalDistinctAccidents;
		i++;
	    }
	    YearRecord toAdd = new YearRecord(yearCount, totalDistinctAccidents, averageWound, totalWound, averageKill, totalKill);
	    compiledYearlyRecords.add(toAdd);
	    yearCount = compiledRecordsY.get(i).year;
	    totalDistinctAccidents = 1;
	    totalWound = compiledRecordsY.get(i).wounded;
	    averageWound = compiledRecordsY.get(i).wounded;
	    averageKill = compiledRecordsY.get(i).runningDeathTotal;
	    totalKill = compiledRecordsY.get(i).runningDeathTotal;
	}
	YearRecord toAdd = new YearRecord(yearCount, totalDistinctAccidents, averageWound, totalWound, averageKill, totalKill);
	compiledYearlyRecords.add(toAdd);
	printYearlyRecords();

    }

    // Groups accident records by weekday and then compiles them into a consolidated weekday record.
    public void groupByWeekday(){
	// Sort and set up initial aggregate variables
	Collections.sort(compiledRecordsW, new dayComparator());
	int totalDistinctAccidents = 0;
	int totalWound = 0;
	int totalDeath = 0;
	int index = 0;
	String d = "Sun";

	// For each day of the week, compile aggregates and store them.
	for (int i = 0; i < 7; i++){
	    while(index < compiledRecordsW.size() && enumerateWeekday(compiledRecordsW.get(index)) == i){
		totalDistinctAccidents++;
		totalWound += compiledRecordsW.get(index).wounded;
		totalDeath += getDeathOfDay(compiledRecordsW.get(index));
		d = compiledRecordsW.get(index).day;
		index++;
	    }
	    DayRecord toAdd = new DayRecord(d, totalDistinctAccidents, totalWound, totalDeath);
	    compiledWeekdayRecords.add(toAdd);
	    totalDistinctAccidents = 0;
	    totalWound = 0;
	    totalDeath = 0;
	}
	printWeekdayRecords();

    }


    // Since there is no record of daily deaths, deaths must be calculated by finding the specific
    // record in the Compiled yearly records, then subtracting all aggregate deaths before that day in the same year.
    public int getDeathOfDay(AccidentRecord a){
	int x = 0;
	// Find the record we are searching for in the yearly records
	while (a.recordNum != compiledRecordsY.get(x).recordNum){
	    x++;
	}

	// if first record, return that total.
	if (x == 0){
	    return a.runningDeathTotal;
	}

	// if record is found, check to see if there are dates before the given record, and subtract the running death total of the given day from the previous day.
	else if (a.recordNum == compiledRecordsY.get(x).recordNum){
	    if (compiledRecordsY.get(x).year == compiledRecordsY.get(x-1).year)
		return compiledRecordsY.get(x).runningDeathTotal - compiledRecordsY.get(x-1).runningDeathTotal;
	    // otherwise return the deaths for that given day.
	    else return compiledRecordsY.get(x).runningDeathTotal;
	}

	else return 0;
    }

    // Function to print the basic Accident Records
    public void printRecords(){
	for (int i = 0; i < compiledRecords.size(); i++){
	    System.out.println(compiledRecords.get(i).year + " --- " + compiledRecords.get(i).day);
	}
    }

    // Function to print Yearly Records
    public void printYearlyRecords(){
	System.out.println("BY YEAR");
	for (int i = 0; i < compiledYearlyRecords.size(); i++){
	    System.out.println(compiledYearlyRecords.get(i).year + "\t"
			       + compiledYearlyRecords.get(i).totalAccidents + "\t"
			       + compiledYearlyRecords.get(i).averageWound + "\t"
			       + compiledYearlyRecords.get(i).totalWound + "\t"
			       + compiledYearlyRecords.get(i).averageDeath + "\t"
			       + compiledYearlyRecords.get(i).totalDeath);
	}
	System.out.println("-------------------");
    }

    // Function to print Weekday Records
    public void printWeekdayRecords(){
	System.out.println("BY WEEKDAY");
	for (int i = 0; i < compiledWeekdayRecords.size(); i++){
	    System.out.println(compiledWeekdayRecords.get(i).day + "\t"
			       + compiledWeekdayRecords.get(i).totalAccidents + "\t"
			       + compiledYearlyRecords.get(i).totalWound + "\t"
			       + compiledWeekdayRecords.get(i).totalDeath);
	}
	System.out.println("-------------------");
    }

    // Main Function to take a command line argument file name and parse it.
    public static void main(String[] args){
	if (args.length == 1){
	    FileParser f = new FileParser(args[0]);
	}

	else{
	    System.out.println("Specify a file at command line");
	    return;
	}


    }
}
