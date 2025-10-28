package models;

import interfaces.IAuthenticable;
import interfaces.IStatus;

abstract class User implements IAuthenticable, IStatus{
    protected String username;
    protected String password;
    protected int role;
    protected boolean status;
    
    public User(){
        this.username = "";
        this.password = "";
        this.role = -1;
    }

    public User(String username, String password, int role, boolean status) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public int getRole() { return role; }

    @Override
    public void setRole(int role) { this.role = role; }
    
    @Override
    public boolean checkPassword(String inputPassword) {
        return getPassword().equals(inputPassword);
    }

    @Override
    public String getStatusString() { return this.status?"Active":"Block"; }
    @Override
    public boolean getStatus() { return this.status;}

    @Override
    public void setStatus(boolean status) { this.status = status; }

    @Override
    public abstract String toString(); // hàm trừu tượng
    
}
