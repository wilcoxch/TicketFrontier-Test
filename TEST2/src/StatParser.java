import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.*;

public class StatParser
{
    // Basic Data Structure for use with the final ArrayList called parsedList
    private class statElement{
        public String tableInfo;
        public String tableNum;
        public String tableURL;

        // Basic constructor that sets all relevant information from the html scrape
        public statElement(String num, String info, String url){
            tableInfo = info;
            tableNum = num;
            tableURL = url;
        }

    }

    // Final ArrayList for completed search
    private ArrayList<statElement> parsedList = new ArrayList<statElement>();

    public StatParser()
    {

        // First start by initializing the given webpage, a simple duplicate constructor with a String parameter would allow for any webpage.
        // although this program is written specifically to parse the given webpage in the second problem.
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www2.stat.duke.edu/courses/Spring01/sta114/data/andrews.html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Gather the relevant table from the webpage
        Element table = doc.select("TABLE").get(1);

        // Create a list of elemenets based on each row from the table we are looking for.
        Elements rows = table.select("TR");

        // Create an empty Element list to store all rows that contain months
        Elements rowsParsed = new Elements();

	    for (int i = 0; i < rows.size(); i++){
	        if (rows.get(i).text().contains("January") ||
		    rows.get(i).text().contains("February") ||
		    rows.get(i).text().contains("March") ||
		    rows.get(i).text().contains("April") ||
		    rows.get(i).text().contains("May") ||
		    rows.get(i).text().contains("June") ||
		    rows.get(i).text().contains("July") ||
		    rows.get(i).text().contains("August") ||
		    rows.get(i).text().contains("September") ||
		    rows.get(i).text().contains("October") ||
		    rows.get(i).text().contains("November") ||
		    rows.get(i).text().contains("December"))
		    {
            // Add all rows containing month references
		    rowsParsed.add(rows.get(i));
		    }
	    }

	    // postParse the rows with months to be stored into the final ArrayList
        postParse(rowsParsed);

    }

    public StatParser(String lookup){

        // Initialize the HTML page as we did in the main constructor
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www2.stat.duke.edu/courses/Spring01/sta114/data/andrews.html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the relevant table and break it down by row as before.
        Element table = doc.select("TABLE").get(1);
        Elements rows = table.select("TR");
        Elements rowsParsed = new Elements();
        int i = 0;

        // Search through the rows for the given lookup table number parameter.
        while (i < rows.size() || rowsParsed.size() == 0){
            if (rows.get(i).text().contains(lookup)){
                rowsParsed.add(rows.get(i));
            }
            i++;
        }

        // If the given table is found, post parse that row to be stored.
        if (rowsParsed.size() > 0){
            System.out.println("Found");
            postParse(rowsParsed);
        }
    }


    private void postParse(Elements toPP){

	for (int i = 0; i < toPP.size(); i++)
	    {
            // Based on the passed collection of elements, find each column of every row for parsing.
		    Element row = toPP.get(i);
		    Elements cols = row.select("TD");

            // Find the absolute URL of each table link
            Element link = cols.select("a").first();
            String absHref = link.attr("abs:href");

            // Find the title of each Table, removing duplicate white space.
            String title = cols.get(0).text();
            if (title.length() > 10){
                title = "Table " + title.substring(8);
            }

            // Add all relevant information to a statElement, then add to final ArrayList.
            statElement toAdd = new statElement(title, cols.get(1).text(), absHref);
            parsedList.add(toAdd);
	    }
    }

    public void printElements(){

        // Print all elements of the final arrayList in the directed format.
        for (int i = 0; i < parsedList.size(); i++){
            System.out.println(parsedList.get(i).tableNum + "\t" + parsedList.get(i).tableURL);
        }
    }

    public static void main (String [] args)
    {
        // Run the basic constructor and print all found elements.
        StatParser p = new StatParser();
        p.printElements();


        // Run the lookup constructor and print the found element (if any).
        System.out.println();
        System.out.println("Searching for Table 48.3");
        StatParser l = new StatParser("48.3");
        l.printElements();
    }

}
