package models;

public class Drug extends Product{ // class con kế thừa abstract class Product

// thuộc tính (Attributes)
    private String dosage;
    private String Ingredient;
    private boolean prescriptionRequired;

    // Constructor mặc định
    public Drug() {
        this.dosage = "";
        this.Ingredient = "";
        this.prescriptionRequired = false;
    }

    // Constructor có tham số
    public Drug(String ID, String name, String unit, double price, int shelfLifeMonths, String Dosage, String ingredient, boolean pR, boolean status) {
        super(ID, name, unit, price, shelfLifeMonths, status);
        this.dosage = Dosage;
        this.Ingredient = ingredient;
        this.prescriptionRequired = pR;
    }
    
    // Method getter - setter
    public void setDosage(String dosage){this.dosage = dosage;}
    public String getDosage(){return this.dosage;}

    public void setIngredient(String ingre){this.Ingredient = ingre;}
    public String getIngredient(){return this.Ingredient;}

    public void setpR(boolean pR){this.prescriptionRequired = pR;}
    public boolean getpR(){return this.prescriptionRequired;}

    // Định dạng chính trong file Sản phẩm + keyword "D" để phân biệt đây là Thuốc
    @Override
    public String toString(){
        return PID + "|" + Name + "|" + unit + "|"  + price + "|" + getShelfLifeInfo() + "|" + dosage + "|" + Ingredient + "|" + prescriptionRequired + "|" + "D";
    }
}
