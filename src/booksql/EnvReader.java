/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package booksql;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author zander
 */
public class EnvReader {
    private static String envPath = ".env";
    
    public static String getUsername(){
        return getValue("DBusername");
    }
    
    public static String getPassword(){
        return getValue("DBpassword");
    }
    
    private static String getValue(String key){
        String value = "";
        
        try{
            Scanner scan = new Scanner(new File(envPath));
            
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                if(line.matches(key + ".*")){
                    String[] lineValues = line.split("=");
                    value = lineValues[lineValues.length - 1];
                    break;
                }
            }
            
        } catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        return value;
    }
}
