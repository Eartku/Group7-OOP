package models;

public class Admin extends User{

    public Admin(String username, String password) {
        super(username, password, 2);
    }

    @Override
    public String toString(){
        return username + "|" + password + "|" + role;
    }

}
