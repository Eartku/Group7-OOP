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
        C001|Aurora Le|01/01/2000|Hanoi|aurora01@example.com|0900000001|aurora01
        C002|Lan Anh|10/10/1992|Ho Chi Minh City|lan_anh92@example.com|09000002|lan_anh92
        C003|Shadow X|05/05/1995|Da Nang|shadowx@example.com|09000003|shadowx
        C004|Minh Trang|12/12/1998|Hanoi|minhtrang@example.com|0900000004|minhtrang
        C005|Beta Tester|03/03/1990|Hue|beta_tester@example.com|0900000005|beta_tester
        C006|Nguyen Rey|07/07/1993|Can Tho|vietnam_rey@example.com|0900000006|vietnam_rey
        C007|Guest One|15/08/2001|Hanoi|guest101@example.com|0900000007|guest101
        """ 
        ;
        
        String products =
        """
        P001|Paracetamol|Hop|50000.0|6|4 vien/ngay|Paracetamol 500mg|false|D
        P002|Amoxicillin|Hop|75000.0|12|3 vien/ngay|Amoxicillin 500mg|true|D
        P003|Vitamin C|Lo|30000.0|24|1 vien/ngay|Acid Ascorbic 1000mg|false|D
        P004|Cetirizine|Hop|45000.0|12|1 vien/ngay|Cetirizine Hydrochloride 10mg|false|D
        P005|Azithromycin|Hop|85000.0|18|1 vien/ngay|Azithromycin 250mg|true|D
        P006|Metformin|Hop|65000.0|24|2 vien/ngay|Metformin 500mg|true|D
        P007|Loratadine|Hop|50000.0|18|1 vien/ngay|Loratadine 10mg|false|D
        P008|Prednisolone|Hop|90000.0|12|1 vien/ngay|Prednisolone 5mg|true|D
        P009|DD NaCl 0,9%|Chai|20000.0|12|Rua mui, ve sinh vet thuong|Bidiphar|Ve sinh|N
        P010|Gel rua tay|Chai|45000.0|18|Diet khuan tay|Lifebuoy|Ve sinh|N
        P011|Khau trang|Hop|40000.0|6|Loc bui, bao ve ho hap|Nam Anh|Dung cu y te|N
        P012|Bong y te|Go|25000.0|48|Dung de sat trung vet thuong|Viet Duc|Dung cu y te|N
        P013|Nuoc sat trung|Chai|60000.0|24|Diet khuan nhanh tren tay|Green Cross|Ve sinh|N
        P014|Dau thao duoc|Chai|80000.0|18|Giam rung toc, lam mem toc|Thorakao|My pham|N
        """;

        String batches =
        """
        B001|P001|120|05/03/2025|true
        B002|P002|150|10/03/2025|true
        B003|P003|200|15/03/2025|true
        B004|P004|180|22/03/2025|true
        B005|P005|160|30/03/2025|false
        B006|P006|140|02/08/2024|true
        B007|P007|220|10/04/2025|true
        B008|P008|130|15/04/2025|false
        B009|P009|250|25/08/2024|true
        B0010|P0010|300|02/05/2025|true
        B0011|P0011|400|10/05/2023|true
        B0012|P012|350|18/05/2025|true
        B013|P0013|270|25/05/2025|true
        B0014|P0014|180|01/06/2025|true
        B0015|P002|190|15/06/2025|false
        """;

        String orders =
        """
        H001|C001|05/10/2025|Confirmed
        H002|C001|12/10/2025|Confirmed
        H003|C001|20/10/2025|Confirmed
        H004|C002|06/10/2025|Confirmed
        H005|C002|14/10/2025|Confirmed
        H006|C002|21/10/2025|Confirmed
        H007|C003|08/10/2025|Confirmed
        H008|C003|15/10/2025|Confirmed
        H009|C003|22/10/2025|Confirmed
        H010|C004|09/10/2025|Confirmed
        H011|C004|18/10/2025|Confirmed
        H012|C005|10/10/2025|Confirmed
        H013|C005|19/10/2025|Confirmed
        H014|C006|11/10/2025|Confirmed
        H015|C006|20/10/2025|Confirmed
        H016|C007|12/10/2025|Confirmed
        H017|C007|21/10/2025|Confirmed
        H018|C007|22/10/2025|Confirmed
        H019|C005|25/10/2025|Confirmed
        H020|C002|28/10/2025|Confirmed
        """;

        String orderItems =
        """
        H001|P001|2|P003|1|P007|3
        H002|P002|1|P005|2|P006|1
        H003|P003|3|P004|2
        H004|P005|2|P001|1|P009|1
        H005|P002|2|P006|3
        H006|P007|2|P008|1
        H007|P003|2|P004|2|P0010|1
        H008|P001|1|P005|1|P013|2
        H009|P006|3|P007|1
        H010|P008|2|P009|1
        H011|P010|2|P012|3
        H012|P011|3|P013|2
        H013|P002|2|P003|1|P014|1
        H014|P005|2|P006|2
        H015|P007|3|P009|2
        H016|P004|1|P001|2|P012|1
        H017|P001|2|P013|2
        H018|P002|1|P010|1|P014|1
        H019|P003|2|P007|3
        H020|P006|2|P008|1|P009|2
        """;

        createdata("resources/users.txt", users);
        createdata("resources/customers.txt", customers);
        createdata("resources/products.txt", products);
        createdata("resources/inventory.txt", batches);
        createdata("resources/orders.txt", orders);
        createdata("resources/orderitems.txt", orderItems);
    }
}
