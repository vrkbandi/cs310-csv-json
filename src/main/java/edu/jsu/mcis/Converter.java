package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import au.com.bytecode.opencsv.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Converter {
	
    /*
        Consider a CSV file like the following:
        
        ID,Total,Assignment 1,Assignment 2,Exam 1
        111278,611,146,128,337
        111352,867,227,228,412
        111373,461,96,90,275
        111305,835,220,217,398
        111399,898,226,229,443
        111160,454,77,125,252
        111276,579,130,111,338
        111241,973,236,237,500
        
        The corresponding JSON file would be as follows (note the curly braces):
        
        {
            "colHeaders":["Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160","111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }  
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        /* Create StringBuilder for JSON data */
        
        StringBuilder json = new StringBuilder();
        
        try{
            
            /* Create CSVReader and iterator */
            
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> csv = reader.readAll();
            Iterator<String[]> iterator = csv.iterator();
            
            JSONArray rHeaders = new JSONArray();
            JSONArray cHeaders = new JSONArray();
            JSONArray data = new JSONArray();
            String[] rows;

            /* Get first String[] array from CSV data (the column headers); add elements to "cHeaders" */
            
            cHeaders.addAll(Arrays.asList(iterator.next()));

            /* Iterate through remaining CSV rows */

            while (iterator.hasNext()){
                
                /* Create container for next row */
                
                JSONArray row = new JSONArray();
                
                /* Get next String[] array from CSV data */
                
                rows = iterator.next();
                
                /* Get first element (the row header); add element to "rHeaders" */
                
                rHeaders.add(rows[0]);
                
                /* Add remaining elements to "data" */
                
                for (int i = 1; i < rows.length; i++){
                    row.add(rows[i]);
                }
                
                /* Add row to "data" */
                
                data.add(row);

            }

            /* Construct JSON string (remember, this must be an *exact* match for the example!) */
            
            /* Add column and row headers */

            json.append("{\n    \"colHeaders\":").append(cHeaders.toString());
            json.append(",\n    \"rowHeaders\":").append(rHeaders.toString()).append(",\n");
            
            /* Split "data" rows */
            
            rows = data.toString().split("],");
            
            /* Add data */

            json.append("    \"data\":");

            for (int i = 0; i < rows.length; ++i){
                
                String s = rows[i];         /* Get next data row */

                s = s.replace("\"","");     /* Delete double-quotes */
                s = s.replace("]]","]");    /* Fix terminating square brackets */
                
                json.append(s);             /* Append row */
                
                 /* If this is not the last data row, close the row and begin a new one */
                
                if ((i % rows.length) != (rows.length - 1))
                    json.append("],\n            ");
                
            }
            
            /* Close JSON string */
            
            json.append("\n    ]\n}");
            
        }
        
        catch(IOException e) {
            System.err.println(e.toString());
        }
        
        /* Return JSON String */
        
        return json.toString();
        
    }
    
    public static String jsonToCsv(String jsonString) {
        
        /* Create StringWriter for CSV data */
        
        StringWriter writer = new StringWriter();
        
        try {
            
            /* Create json-simple JSON Parser */
            
            JSONParser parser = new JSONParser();
            
            /* Parse JSON Data */
            
            JSONObject jobject = (JSONObject) parser.parse(jsonString);
            JSONArray col = (JSONArray) jobject.get("colHeaders");
            JSONArray row = (JSONArray) jobject.get("rowHeaders");
            JSONArray data = (JSONArray) jobject.get("data");
            
            /* Create String[] arrays for OpenCSV */
            
            String[] csvcol = new String[col.size()];
            String[] csvrow = new String[row.size()];
            String[] csvdata = new String[data.size()];
            String[] rowdata;
            
            /* Copy column headers */

            for (int i = 0; i < col.size(); i++) {
                csvcol[i] = col.get(i) + "";
            }
            
            /* Copy row headers and row data */

            for (int i = 0; i < row.size(); i++) {
                
                csvrow[i] = row.get(i) + "";
                csvdata[i] = row.get(i) + "," +data.get(i) + "";

            }            
            /* Create OpenCSV Writer */

            CSVWriter csvWriter = new CSVWriter(writer,',','"',"\n");

            /* Write column headers */

            csvWriter.writeNext(csvcol);

            /* Write row headers and row data */

            for (int i = 0; i < csvdata.length; i++) {
                
                /* Strip square brackets from next row */
                
                csvdata[i] = csvdata[i].replace("[","");
                csvdata[i] = csvdata[i].replace("]","");
                csvdata[i] = csvdata[i].replace("\"", "");
                /* Split csvdata[i] into row elements (using comma as delimiter) */

                String[] elements = csvdata[i].split(",");
                
                /* Create String[] container for row data (sized at the number of row elements, plus one for row header) */
                
                rowdata = new String[elements.length + 1];
                rowdata = elements;
                
                csvWriter.writeNext(rowdata);
                
            }
            
        }
        
        catch(ParseException e) {
            
            System.err.println(e.toString());
            
        }
        
        /* Return CSV string */

        return writer.toString();
        
    }
	
}













