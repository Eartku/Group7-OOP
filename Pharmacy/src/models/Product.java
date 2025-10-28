package models;

public abstract class Product implements Comparable<Product> {
    protected String PID;
    protected String Name;
    protected String unit;
    protected double price;
    protected Integer shelfLifeMonths;
    protected boolean status; 

    public Product() {
        this.PID = "";
        this.Name = "";
        this.unit = "";
        this.price = 0.0;
        this.shelfLifeMonths = 0;
        this.status = false;
    }

    public Product(String PID, String Name, String unit, double price, int shelfLifeMonths, boolean status) {
        this.PID = PID;
        this.unit = unit;
        this.Name = Name;
        this.price = price;
        this.shelfLifeMonths = shelfLifeMonths;
        this.status = status;
    }

    public Product(String PID, String Name, String unit, double price, boolean status) {
        this.PID = PID;
        this.unit = unit;
        this.Name = Name;
        this.price = price;
        this.shelfLifeMonths = null;
        this.status = status;
    }

    public void setPID(String PID){this.PID = PID;}
    public void setName(String Name){this.Name = Name;}
    public void setUnit(String unit){this.unit = unit;}
    public void setPrice(double price){this.price = price;}
    

    public void setShelfLifeMonths(Integer shelfLifeMonths) {
        if (shelfLifeMonths == null) {
            this.shelfLifeMonths = null;
        } else if (shelfLifeMonths > 0) {
            this.shelfLifeMonths = shelfLifeMonths;
        } else {
            throw new IllegalArgumentException("Thời hạn sử dụng phải > 0 hoặc null (vô thời hạn).");
        }
    }
    public String getShelfLifeInfo() {
        return (shelfLifeMonths == null) ? "Vô thời hạn" : shelfLifeMonths + " tháng";
    }


    public String getName(){return this.Name;}
    public String getPID(){return this.PID;}
    public String getUnit(){return this.unit;}
    public double getPrice(){return this.price;}
    public Integer getSLM(){return this.shelfLifeMonths;}

    public String getStatusString() { return this.status?"Avaiable ":"Unavaiable"; }
    public boolean getStatus() { return this.status;}
    public void setStatus(boolean status) { this.status = status; }


    @Override
    public abstract String toString();

    @Override
    public int compareTo(Product p){
        return this.getPID().compareTo(p.getPID());

    }

}
