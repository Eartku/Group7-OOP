package models;

public class NonDrug extends Product{ 
    private String manufacturer;
    private String type;
    private String usage;

    public NonDrug() {
        this.manufacturer = "Unknown";
        this.type = "";
        this.usage = "";
    }
    public NonDrug(String ID, String name, String unit, double price, int shelfLifeMonths, String man, String type, String usage) {
        super(ID, name, unit, price, shelfLifeMonths);
        this. manufacturer = man;
        this.type = type;
        this.usage = usage;
    }
    public void setManufacturer(String man){
        this.manufacturer = man;
    }
    public String getManufacturer(){
        return this.manufacturer;
    }

    public void setType(String type){
            this.type = type;
    }
    public String getType(){
        return this.type;
    }

    public void setUsage(String usage){
            this.usage = usage;
    }
    public String getUsage(){
        return this.usage;
    }

    public void info(){
        System.out.println("[nD] - " + PID + " \nTen: " + Name + "\nDon vi: " + unit + "\nGia: " + price + "\nNhaSX: " + manufacturer + "\nType: " + type + "\nCong dung: " + usage + "\nThoi han: " + getShelfLifeInfo());
    }
    @Override
    public String toString(){
        return PID + "|" + Name + "|" + unit + "|"  + price + "|" + getShelfLifeInfo() + "|" + manufacturer + "|" + type + "|" + usage + "|" + "1";
    }
}


