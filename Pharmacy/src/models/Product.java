package models;

import interfaces.IStatus;

public abstract class Product implements Comparable<Product>,IStatus {
    protected String PID;
    protected String Name;
    protected String unit;
    protected double price;
    protected Integer shelfLifeMonths;
    protected boolean status; 

//Mục đích tạo ra class con Drug và NonDrug là vì Nhà thuốc có thế bán cả thuốc và cả vật dụng y tế không phải thuốc
//Các methods sẽ được class con kế thừa và sử dụng 

// Constructor mặc định
    public Product() {
        this.PID = "";
        this.Name = "";
        this.unit = "";
        this.price = 0.0;
        this.shelfLifeMonths = 0;
        this.status = false;
    }

    // Constructor có tham số đủ
    public Product(String PID, String Name, String unit, double price, Integer shelfLifeMonths, boolean status) {
        this.PID = PID;
        this.unit = unit;
        this.Name = Name;
        this.price = price;
        this.shelfLifeMonths = shelfLifeMonths;
        this.status = status;
    }

    //Method getter - setter
    public void setPID(String PID){this.PID = PID;}
    public void setName(String Name){this.Name = Name;}
    public void setUnit(String unit){this.unit = unit;}
    public void setPrice(double price){this.price = price;}
    

    public void setShelfLifeMonths(Integer shelfLifeMonths) {
        if (shelfLifeMonths == null || shelfLifeMonths == 0) {
            this.shelfLifeMonths = null;
        } else if (shelfLifeMonths > 0) {
            this.shelfLifeMonths = shelfLifeMonths;
        } else {
            throw new IllegalArgumentException("han dung phai > 0.");
        }
    }
    public String getShelfLifeInfo() {
        return (shelfLifeMonths == null || shelfLifeMonths == 0) ? "Khong co" : shelfLifeMonths + " thang";
    }
    public String getName(){return this.Name;}
    public String getPID(){return this.PID;}
    public String getUnit(){return this.unit;}
    public double getPrice(){return this.price;}
    public Integer getSLM(){return this.shelfLifeMonths;}

    //implement IStatus
    @Override
    public String getStatusString() { return this.status?"Available ":"Unavailable"; }
    @Override
    public boolean getStatus() { return this.status;}
    @Override
    public void setStatus(boolean status) { this.status = status; }


    // hàm trừu tượng
    @Override
    public abstract String toString();

    //implement Comparable
    @Override
    public int compareTo(Product p){
        return this.getPID().compareTo(p.getPID());

    }

}
