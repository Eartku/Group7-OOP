package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Data {
    public static int initCounter(String path){
        int count = 0;
        java.io.File file = new File(path);
        if(!file.exists()){
            return count; 
        }
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine())!=null){
                if(line.length() < 5) continue;
                String[] parts = line.split("\\|");
                int index = Integer.parseInt(parts[0].substring(1));
                if(index > count){
                    count = index; 
                }
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return count;
    }
    public static String generateNewID(String path, char C) {
        int current = initCounter(path); 
        int next = current + 1;       
        return String.format( "%c%05d", C, next); 
    }

    public static void changeRole(String username, String path, int role){
        java.io.File file = new File(path);
        List<String> lines = new ArrayList<>();
        if(!file.exists()){
            return;
        }
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine())!=null){
                String[] parts = line.split("\\|");
                if(parts.length >= 3 && parts[0].equals(username)){
                    parts[2] = String.valueOf(role);
                    line = String.join("|", parts);
                }
            lines.add(line);
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        try(FileWriter fw = new FileWriter(file, false)){
            for(String line : lines){
                fw.write(line + "\n");
            }
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
