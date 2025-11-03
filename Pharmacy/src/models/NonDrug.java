package models;

public class NonDrug extends Product{ // class con kế thừa abstract class Product
    private String manufacturer;
    private String type;
    private String usage;

    //constructor mặc định
    public NonDrug() {
        this.manufacturer = "Unknown";
        this.type = "";
        this.usage = "";
    }

    //constructor tham số
    public NonDrug(String ID, String name, String unit, double price, int shelfLifeMonths, String man, String type, String usage, boolean status) {
        super(ID, name, unit, price, shelfLifeMonths, status);
        this. manufacturer = man;
        this.type = type;
        this.usage = usage;
    }

    //Method getter - setter
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

    @Override
    public String toString(){   // Định dạng chính trong file Sản phẩm + keyword "N" để phân biệt đây không là Thuốc
        return PID + "|" + Name + "|" + unit + "|"  + price + "|" + getShelfLifeInfo() + "|" + manufacturer + "|" + type + "|" + usage + "|" + "N";
    }
}


