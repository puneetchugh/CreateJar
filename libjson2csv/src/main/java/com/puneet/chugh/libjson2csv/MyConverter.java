package com.puneet.chugh.libjson2csv;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MyConverter {
    private final String LOG_TAG = "json2Csv";

    /***********************************************************************************************
     *
     * @param inputFile - full file path of input file
     * @param outputFile - full file path of output file
     * @throws Exception
     **********************************************************************************************/
    public void json2Csv(File inputFile, File outputFile) throws Exception {

        try {
            JsonNode jsonTree = new ObjectMapper().readTree(inputFile);
            //System.out.printf("\n%s : jsonTree : %s",LOG_TAG, jsonTree);
            if(!jsonTree.isArray()){
                throw new Exception(new Throwable(String.format("%s : %s", LOG_TAG, "Its not a flat json file. Can't do the conversion")));
            }
            Set<String> keySet = new TreeSet<>();
            System.out.printf("\n%s : Its an array of JsonObjects : %s", LOG_TAG, (jsonTree.isArray() ? "yes":"no"));
            List<LinkedHashMap<String, String>> allKeyValues = new LinkedList<>();

            for (TreeNode node : jsonTree) {
                LinkedHashMap<String, String> oneRow = new LinkedHashMap<>();
                Iterator<String> iterator1 = node.fieldNames();
                //System.out.printf("\n%s : Number of fields - %d ", LOG_TAG, node.size());
                while (iterator1.hasNext()) {
                    //System.out.printf("\n%s : Inside iterator fields....", LOG_TAG);
                    String fieldName = iterator1.next();
                    oneRow.put(fieldName, node.get(fieldName).toString().replace("\"", ""));
                    //System.out.printf("\n%s : Adding field name - %s", LOG_TAG, fieldName);
                    //Adding field name
                    keySet.add(fieldName);
                }
                allKeyValues.add(oneRow);
                //System.out.printf("\n%s : %s", LOG_TAG, node.toString());
            }

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                throw new Exception(new Throwable(String.format("%s : %s", LOG_TAG, e.getMessage())));
            }

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            StringBuilder keySb = new StringBuilder();

            for (String key : keySet) {
                //System.out.printf("\n%s : key - %s", LOG_TAG, key);
                keySb.append(key);
                keySb.append(",");
            }
            keySb.deleteCharAt(keySb.length() - 1);
            bw.write(keySb.toString());
            bw.newLine();

            for (LinkedHashMap<String, String> eachRow : allKeyValues) {
                StringBuilder sb = new StringBuilder();
                //iterate through all keys
                for (String key : keySet) {
                    if (eachRow.containsKey(key)) {
                        sb.append(eachRow.get(key));
                    }
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                String stringToBeInserted = sb.toString().replace("Branch", "");
                try {
                    bw.write(stringToBeInserted);
                    bw.newLine();
                } catch (IOException e) {
                    throw new Exception(new Throwable(String.format("\n%s : %s", LOG_TAG, e.getMessage())));
                }
                //System.out.printf("\n%s : One Row - %s", LOG_TAG, sb.toString());
            }
            bw.close();
        } catch (IOException e) {
            throw new Exception(new Throwable(String.format("%s : %s", LOG_TAG, e.getMessage())));
        }
    }
}
