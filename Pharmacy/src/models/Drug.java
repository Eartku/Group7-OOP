package models;

public class Drug extends Product{ // class con
    private String dosage;
    private String Ingredient;
    private boolean prescriptionRequired;

    public Drug() {
        this.dosage = "";
        this.Ingredient = "";
        this.prescriptionRequired = false;
    }

    public Drug(String ID, String name, String unit, double price, int shelfLifeMonths, String Dosage, String ingredient, boolean pR, boolean status) {
        super(ID, name, unit, price, shelfLifeMonths, status);
        this.dosage = Dosage;
        this.Ingredient = ingredient;
        this.prescriptionRequired = pR;
    }
    
    public void setDosage(String dosage){this.dosage = dosage;}
    public String getDosage(){return this.dosage;}

    public void setIngredient(String ingre){this.Ingredient = ingre;}
    public String getIngredient(){return this.Ingredient;}

    public void setpR(boolean pR){this.prescriptionRequired = pR;}
    public boolean getpR(){return this.prescriptionRequired;}

    @Override
    public String toString(){
        return PID + "|" + Name + "|" + unit + "|"  + price + "|" + getShelfLifeInfo() + "|" + dosage + "|" + Ingredient + "|" + prescriptionRequired + "|" + "0";
    }
}
