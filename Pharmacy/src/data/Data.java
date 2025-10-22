package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    public static void createdata(String file_path, String content){
            try {
                File file = new File(file_path);
                // 🔹 Tạo thư mục cha nếu chưa có (đây là phần quan trọng)
                file.getParentFile().mkdirs();

                try (FileWriter fw = new FileWriter(file, false)) {
                    fw.write(content);
                }
                System.out.println("Ghi file thành công: " + file.getPath());
            } catch(IOException e){
                System.out.println("Error writing file: " + e.getMessage());
            }
        }

    public static void initData(){
        String users =
        """
        admin|admindev|2\r
        aurora01|P@ssw0rd!23|1\r
        lan_anh92|lanA!992021|1\r
        shadowx|sh4d0wX777|1\r
        sysroot|r00tAcc3ss!|2\r
        minhtrang|MtR@ng2025|1\r
        beta_tester|B3taTest#1|1\r
        vietnam_rey|VnR3y_88|1\r
        guest101|guest_pass101|1
        """ 
        ;
        
        String customers =
        """
        C00001|Aurora Le|01/01/2000|Hanoi|aurora01@example.com|0900000001|aurora01
        C00002|Lan Anh|10/10/1992|Ho Chi Minh City|lan_anh92@example.com|0900000002|lan_anh92
        C00003|Shadow X|05/05/1995|Da Nang|shadowx@example.com|0900000003|shadowx
        C00004|Minh Trang|12/12/1998|Hanoi|minhtrang@example.com|0900000004|minhtrang
        C00005|Beta Tester|03/03/1990|Hue|beta_tester@example.com|0900000005|beta_tester
        C00006|Nguyen Rey|07/07/1993|Can Tho|vietnam_rey@example.com|0900000006|vietnam_rey
        C00007|Guest One|15/08/2001|Hanoi|guest101@example.com|0900000007|guest101
        """ 
        ;
        
        String products =
        """
        P00001|Paracetamol|Hop|50000.0|6|4 vien/ngay|Paracetamol 500mg|false|D
        P00002|Amoxicillin|Hop|75000.0|12|3 vien/ngay|Amoxicillin 500mg|true|D
        P00003|Vitamin C|Lo|30000.0|24|1 vien/ngay|Acid Ascorbic 1000mg|false|D
        P00004|Cetirizine|Hop|45000.0|12|1 vien/ngay|Cetirizine Hydrochloride 10mg|false|D
        P00005|Azithromycin|Hop|85000.0|18|1 vien/ngay|Azithromycin 250mg|true|D
        P00006|Metformin|Hop|65000.0|24|2 vien/ngay|Metformin 500mg|true|D
        P00007|Loratadine|Hop|50000.0|18|1 vien/ngay|Loratadine 10mg|false|D
        P00008|Prednisolone|Hop|90000.0|12|1 vien/ngay|Prednisolone 5mg|true|D
        P00009|Dung dich muoi sinh ly|Chai|20000.0|12|Rua mui, ve sinh vet thuong|Bidiphar|Ve sinh|N
        P00010|Gel rua tay|Chai|45000.0|18|Diet khuan tay|Lifebuoy|Ve sinh|N
        P00011|Khau trang y te|Hop|40000.0|6|Loc bui, bao ve ho hap|Nam Anh|Dung cu y te|N
        P00012|Bong y te|Go|25000.0|48|Dung de sat trung vet thuong|Viet Duc|Dung cu y te|N
        P00013|Nuoc sat trung|Chai|60000.0|24|Diet khuan nhanh tren tay|Green Cross|Ve sinh|N
        P00014|Dau goi dau thao duoc|Chai|80000.0|18|Giam rung toc, lam mem toc|Thorakao|My pham|N
        """;

        String batches =
        """
        B00001|P00001|120|05/03/2024|true
        B00002|P00002|150|10/03/2024|true
        B00003|P00003|200|15/03/2024|true
        B00004|P00004|180|22/03/2024|true
        B00005|P00005|160|30/03/2024|false
        B00006|P00006|140|02/04/2024|true
        B00007|P00007|220|10/04/2024|true
        B00008|P00008|130|15/04/2024|false
        B00009|P00009|250|25/04/2024|true
        B00010|P00010|300|02/05/2024|true
        B00011|P00011|400|10/05/2024|true
        B00012|P00012|350|18/05/2024|true
        B00013|P00013|270|25/05/2024|true
        B00014|P00014|180|01/06/2024|true
        B00015|P00002|190|15/06/2024|false
        """;

        String orders =
        """
        O00001|C00001|05/10/2025
        O00002|C00001|12/10/2025
        O00003|C00001|20/10/2025
        O00004|C00002|06/10/2025
        O00005|C00002|14/10/2025
        O00006|C00002|21/10/2025
        O00007|C00003|08/10/2025
        O00008|C00003|15/10/2025
        O00009|C00003|22/10/2025
        O00010|C00004|09/10/2025
        O00011|C00004|18/10/2025
        O00012|C00005|10/10/2025
        O00013|C00005|19/10/2025
        O00014|C00006|11/10/2025
        O00015|C00006|20/10/2025
        O00016|C00007|12/10/2025
        O00017|C00007|21/10/2025
        O00018|C00007|22/10/2025
        O00019|C00005|25/10/2025
        O00020|C00002|28/10/2025
        """;

        String orderItems =
        """
        O00001|P00001|2|P00003|1|P00007|3
        O00002|P00002|1|P00005|2|P00006|1
        O00003|P00003|3|P00004|2
        O00004|P00005|2|P00001|1|P00009|1
        O00005|P00002|2|P00006|3
        O00006|P00007|2|P00008|1
        O00007|P00003|2|P00004|2|P00010|1
        O00008|P00001|1|P00005|1|P00013|2
        O00009|P00006|3|P00007|1
        O00010|P00008|2|P00009|1
        O00011|P00010|2|P00012|3
        O00012|P00011|3|P00013|2
        O00013|P00002|2|P00003|1|P00014|1
        O00014|P00005|2|P00006|2
        O00015|P00007|3|P00009|2
        O00016|P00004|1|P00011|2|P00012|1
        O00017|P00001|2|P00013|2
        O00018|P00002|1|P00010|1|P00014|1
        O00019|P00003|2|P00007|3
        O00020|P00006|2|P00008|1|P00009|2
        """;

        createdata("resources/users.txt", users);
        createdata("resources/customers.txt", customers);
        createdata("resources/products.txt", products);
        createdata("resources/inventory.txt", batches);
        createdata("resources/orders.txt", orders);
        createdata("resources/orderitems.txt", orderItems);
    }
}
