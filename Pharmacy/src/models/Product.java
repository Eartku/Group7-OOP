package models;

public abstract class Product implements Comparable<Product> {
    protected String PID;
    protected String Name;
    protected String unit;
    protected double price;
    protected Integer shelfLifeMonths; 

    public Product() {
        this.PID = "";
        this.Name = "";
        this.unit = "";
        this.price = 0.0;
        this.shelfLifeMonths = 0;
    }

    public Product(String PID, String Name, String unit, double price, int shelfLifeMonths) {
        this.PID = PID;
        this.unit = unit;
        this.Name = Name;
        this.price = price;
        this.shelfLifeMonths = shelfLifeMonths;
    }

    public Product(String PID, String Name, String unit, double price) {
        this.PID = PID;
        this.unit = unit;
        this.Name = Name;
        this.price = price;
        this.shelfLifeMonths = null;
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

    @Override
    public abstract String toString();

    @Override
    public int compareTo(Product p){
        return this.getPID().compareTo(p.getPID());

    }

}
