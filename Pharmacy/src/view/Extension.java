package view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Extension {
    public static int readIntInRange(String msg, int min, int max, Scanner sc) {
        if (sc == null) throw new IllegalArgumentException("Scanner cannot be null");
        while (true) {
            Log.request(msg);
            try {
                int n = Integer.parseInt(sc.nextLine());
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) {}
            Log.warning("Nhap so tu " + min + " den " + max + "!");
        }
    }
     public static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                // Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
       
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error cleaning screen: " + e.getMessage());
        }
    }
        
    public static void pause(Scanner sc) {
        Log.info("Nhan Enter de tiep tuc...");
        sc.nextLine();
    }


    public static void printTableHeader(String... headers) {
    int colWidth = 20; // <-- chỉnh ở đây
    printLine(headers.length, colWidth);

    System.out.print("|");
    for (String h : headers) {
        System.out.printf(" %-18s |", h); // 18 + 2 khoảng trắng + | = 20
    }
    System.out.println();

    printLine(headers.length, colWidth);
}

public static void printTableRow(Object... values) {
    int colWidth = 20; 
    System.out.print("|");
    for (Object v : values) {
        String str = v == null ? "" : v.toString();
        if (str.length() > 18) str = str.substring(0, 15) + "..."; 
        System.out.printf(" %-18s |", str);
    }
    System.out.println();
    printLine(values.length, colWidth);
}

private static void printLine(int colCount, int colWidth) {
    System.out.print("|");
    for (int i = 0; i < colCount; i++) {
        System.out.print("-".repeat(colWidth));
        System.out.print("|");
    }
    System.out.println();
}

    public static String maskPassword(String password, String mode) {
        if (password == null) return null;
        return mode.repeat(password.length());
    }

    public static void printInBox(Runnable r) {
        // Bước 1: redirect output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        System.setOut(ps);


        r.run();

        System.out.flush();
        System.setOut(oldOut);

        // Bước 4: in khung
        String[] lines = baos.toString().split("\r?\n");
        int boxWidth = 33;
        String border = "+" + "-".repeat(boxWidth+2) + "+";
        System.out.println(border);
        for (String line : lines) {
            if (line.length() > boxWidth) {
                // Tự động wrap dòng dài
                for (int i = 0; i < line.length(); i += boxWidth) {
                    String sub = line.substring(i, Math.min(i + boxWidth, line.length()));
                    System.out.printf("| %-33s |\n", sub);
                }
            } else {
                System.out.printf("| %-33s |\n", line);
            }
        }
        System.out.println(border);
    }

}
