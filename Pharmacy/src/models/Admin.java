package models;

public class Admin extends User{
    // constructor 
    public Admin(String username, String password, boolean status) {
        super(username, password, 2, status);
    }

    // định dạng trong file
    @Override
    public String toString(){
        return username + "|" + password + "|" + role + "|" + status;
    }

}
