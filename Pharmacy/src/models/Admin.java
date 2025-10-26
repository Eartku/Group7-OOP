package models;

public class Admin extends User{

    public Admin(String username, String password, boolean status) {
        super(username, password, 2, status);
    }

    @Override
    public String toString(){
        return username + "|" + password + "|" + role + "|" + status;
    }

}
