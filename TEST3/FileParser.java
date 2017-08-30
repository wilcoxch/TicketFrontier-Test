/**
 * Created by Chris on 8/29/2017.
 */

import java.util.*;
import java.io.*;
import java.lang.*;


public class FileParser{

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

    public class yearComparator implements Comparator<AccidentRecord>{

	public int compare(AccidentRecord a, AccidentRecord b){
	    return a.year - b.year;
	}
    }

    public class dayComparator implements Comparator<AccidentRecord>{

	public int compare(AccidentRecord a, AccidentRecord b){
	    return enumerateWeekday(a) - enumerateWeekday(b);
	}
    }


    private ArrayList<AccidentRecord> compiledRecords = new ArrayList<AccidentRecord>();
    private ArrayList<AccidentRecord> compiledRecordsY = new ArrayList<AccidentRecord>();
    private ArrayList<AccidentRecord> compiledRecordsW = new ArrayList<AccidentRecord>();
    private ArrayList<YearRecord> compiledYearlyRecords = new ArrayList<YearRecord>();
    private ArrayList<DayRecord> compiledWeekdayRecords = new ArrayList<DayRecord>();

    public FileParser(String fileName){

	try {
	    FileReader fileReader = new FileReader(fileName);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    while (bufferedReader.ready()) {
		String record = bufferedReader.readLine();
		parseRecord(record);
	    }
	    bufferedReader.close();
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



    public void groupByYear(){
	Collections.sort(compiledRecordsY, new yearComparator());
	int yearCount = compiledRecordsY.get(0).year;
	int totalDistinctAccidents = 0;
	double averageWound = 0.0;
	int totalWound = 0;
	double averageKill = 0.0;
	int totalKill = 0;

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

    public void groupByWeekday(){
	Collections.sort(compiledRecordsW, new dayComparator());
	int totalDistinctAccidents = 0;
	int totalWound = 0;
	int totalDeath = 0;
	int index = 0;
	String d = "Sun";

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

    public int getDeathOfDay(AccidentRecord a){
	int x = 0;
	while (a.recordNum != compiledRecordsY.get(x).recordNum){
	    x++;
	}
	if (x == 0){
	    return a.runningDeathTotal;
	}
	else if (a.recordNum == compiledRecordsY.get(x).recordNum){
	    if (compiledRecordsY.get(x).year == compiledRecordsY.get(x-1).year)
		return compiledRecordsY.get(x).runningDeathTotal - compiledRecordsY.get(x-1).runningDeathTotal;
	    else return compiledRecordsY.get(x).runningDeathTotal;
	}


	else return 0;
    }

    public void printRecords(){
	for (int i = 0; i < compiledRecords.size(); i++){
	    System.out.println(compiledRecords.get(i).year + " --- " + compiledRecords.get(i).day);
	}
    }

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
