package service;
import interfaces.Authenticable;
import interfaces.Management;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Admin;
import models.Customer;
import models.Guest;


public class UserManager implements Management<Authenticable>{
    private static final String FILE_PATH = "resources/users.txt";
    private final List<Authenticable> users;

    public UserManager() {
        users = loadUsers();
    }

    // lấy user từ file
    private static List<Authenticable> loadUsers(){
        List<Authenticable> list = new ArrayList<>();
        java.io.File file = new File(FILE_PATH);
        if(!file.exists()) return list;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = br.readLine())!= null){
                String parts[] = line.split("\\|");
                int role = Integer.parseInt(parts[2]);
                switch(role){
                    case 0 -> list.add(new Guest(parts[0], parts[1]));
                    case 1 -> list.add(new Customer(parts[0], parts[1]));
                    case 2 -> list.add(new Admin(parts[0], parts[1]));                
                }
            }
        }
        catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
        return list;
    }

    // kiểm tra tồn tại Username
    @Override
    public boolean exists(String username) {
        for (Authenticable u : users) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Authenticable get(String username){
        for (Authenticable u : users) {
            if (u.getUsername().equals(username))
                return u;
        }
        return null;
    }

    public void appendCustomer(String path, Customer u) {
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.append(u.toStringProfile()).append("\n");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void upgradeGuestToCustomer(Guest g, Customer c) {
        users.removeIf(u -> u.getUsername().equals(g.getUsername()));
        users.add(c);
    }


    @Override
    public void add(Authenticable user){
        users.add(user);
    }  

    @Override
    public void delete(String username){
        users.removeIf(u -> u.getUsername().equals(username));
    } 

    public void updateWithRole(Authenticable newUser, String username){
        for(int i =0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(username)){
                users.set(i, newUser);
            }
        }
    }

    @Override
    public void showList(){
        for (Authenticable u : users) {
            System.out.println(u.getUsername() + "|" + u.getRole());
        }
    }

    @Override
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Authenticable user : users) {
                bw.write(user.toString()+ "\n");
            }
        } catch (IOException e) {
            System.out.println("Loi khi luu customers: " + e.getMessage());
        }
    }

    @Override
    public String report(){
        int count1 = 0, count2 = 0, count3 = 0;
        for (Authenticable user : users) {
            if (user instanceof Guest) count1++;
            else if (user instanceof Customer) count2++;
            else if (user instanceof Admin) count3++;
        }
        return "Tong so luong tai khoan trong he thong: " + users.size() + "\n"
        +  "So tai khoan Guest: " +  count1 + "\n"
        +  "So tai khoan khach hang (profile updated): " +  count2 + "\n"
        +  "So tai khoan Admin: " + count3;
    } 
}

