package view;

import java.io.IOException;
import java.util.Scanner;

public class Extension {
    public static int readIntInRange(String msg, int min, int max, Scanner sc) {
        if (sc == null) throw new IllegalArgumentException("Scanner cannot be null");
        while (true) {
            System.out.print(msg);
            try {
                int n = Integer.parseInt(sc.nextLine());
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) {}
            System.out.println("Nhap so tu " + min + " den " + max + "!");
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
            System.out.println("Không thể xóa màn hình: " + e.getMessage());
        }
    }
        
    public static void pause(Scanner sc) {
        System.out.println("Nhan Enter de tiep tuc...");
        sc.nextLine();
    }

    public static void dotAnimation(String message, int durationMillis, String end) {
        long endTime = System.currentTimeMillis() + durationMillis;
        int dotCount = 0;

        while (System.currentTimeMillis() < endTime) {
            System.out.print("\r" + message + " " + ".".repeat(dotCount));
            dotCount++;
            
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.print("\r" + " ".repeat(message.length() + 2) + "\r");
        System.out.println(end);
    }
    public static void printTableHeader(String... headers) {
        int colWidth = 20;
        printLine(headers.length, colWidth);
        // In tên cột
        for (String h : headers) {
            System.out.printf(" %-20s", h);
        }
        System.out.println();

        printLine(headers.length, colWidth);
    }

    // In từng hàng dữ liệu có viền
    public static void printTableRow(Object... values) {
        System.out.print("|");
        for (Object v : values) {
            System.out.printf(" %-20s", v);
        }
        System.out.println();
        printLine(values.length, 20);
    }

    // Hàm phụ in dòng kẻ
    private static void printLine(int colCount, int colWidth) {
        System.out.print("|");
        for (int i = 0; i < colCount; i++) {
            System.out.print("-".repeat(colWidth));
        }
        System.out.println();
    }

}
