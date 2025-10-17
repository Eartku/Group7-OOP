package models;

public class Admin extends User{

    public Admin(String username, String password) {
        super(username, password, 2);
    }

    @Override
    public boolean checkPassword(String inputPassword) {
        return getPassword().equals(inputPassword);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public int getRole() {
        return super.getRole();
    }

    @Override
    public void setRole(int role) {
        super.setRole(role);
    }

    @Override
    public String toString(){
        return username + "|" + password + "|" + role;
    }

}
