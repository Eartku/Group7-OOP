import java.util.Scanner;
import service.ProductManager;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String keyword = sc.nextLine().trim();
        ProductManager pm = new ProductManager();
        pm.selectProduct(keyword, sc);
    }
}
