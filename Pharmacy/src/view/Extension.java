package view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

// Chủ yếu sử dụng class này như hàm tiện ích - cải thiện phần hiển thị trong console (không ảnh hưởng phần chính)

public class Extension {

    // ===================== NHÓM INPUT & HỖ TRỢ CƠ BẢN =====================
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

    public static String maskPassword(String password, String mode) {
        if (password == null) return null;
        return mode.repeat(password.length());
    }

    // ===================== NHÓM HỖ TRỢ TABLE =====================

    private static final String ESC = String.valueOf((char) 27);

    // Loại bỏ mã ANSI để lấy chiều dài hiển thị thật
    private static String stripAnsi(String s) {
        if (s == null) return "";
        return s.replaceAll(ESC + "\\[[;\\d]*m", "");
    }

    // Gán màu và căn độ rộng theo ký tự hiển thị thực tế
    private static String colorizeAndPad(String text, int contentWidth, String color) {
        String visible = text == null ? "" : text;
        if (visible.length() > contentWidth) {
            visible = visible.substring(0, Math.max(0, contentWidth - 3)) + "...";
        }
        String colored = (color == null || color.isEmpty())
                ? visible
                : color + visible + Log.RESET;

        int pad = contentWidth - stripAnsi(visible).length();
        if (pad < 0) pad = 0;
        return " " + colored + " ".repeat(pad) + " ";
    }

    // ---- In header bảng ----
    public static void printTableHeader(String... headers) {
        int colWidth = 20;
        int contentWidth = colWidth - 2;
        printLine(headers.length, colWidth);

        System.out.print("|");
        for (String h : headers) {
            String cell = colorizeAndPad(h, contentWidth, Log.BLUE);
            System.out.print(cell + "|");
        }
        System.out.println();

        printLine(headers.length, colWidth);
    }

    // ---- In dòng bảng ----
    public static void printTableRow(Object... values) {
        int colWidth = 20;
        int contentWidth = colWidth - 2;
        System.out.print("|");

        for (Object v : values) {
            String str = v == null ? "" : v.toString().trim();
            String color = "";

            if (str.equalsIgnoreCase("Active")) color = Log.GREEN;
            else if (str.equalsIgnoreCase("Block")) color = Log.RED;
            else if (str.contains("Sap het hang")) color = Log.YELLOW;
            else if (str.contains("Het hang")) color = Log.RED;
            else if (str.contains("Qua han")) color = Log.RED;
            else if (str.contains("Active - Con hang")) color = Log.GREEN;
            else if (str.contains("Inactive - Con hang")) color = Log.RED;
            else if (str.contains("Confirmed")) color = Log.GREEN;
            else if (str.contains("Customer blocked")) color = Log.YELLOW;
            else if (str.contains("Canceled")) color = Log.RED;
            else if (str.contains("Available") || str.contains("Avaiable")) color = Log.GREEN;
            else if (str.contains("Unavailable") || str.contains("Unavaiable")) color = Log.RED;

            String cell = colorizeAndPad(str, contentWidth, color);
            System.out.print(cell + "|");
        }

        System.out.println();
        printLine(values.length, colWidth);
    }

    // ---- In đường kẻ bảng ----
    private static void printLine(int colCount, int colWidth) {
        System.out.print("|");
        for (int i = 0; i < colCount; i++) {
            System.out.print("-".repeat(colWidth));
            System.out.print("|");
        }
        System.out.println();
    }

    // ===================== NHÓM PRINT BOX =====================

    public static void printInBox(Runnable r) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        System.setOut(ps);

        r.run();

        System.out.flush();
        System.setOut(oldOut);

        String[] lines = baos.toString().split("\r?\n");
        int boxWidth = 33;
        String border = "+" + "-".repeat(boxWidth + 2) + "+";
        System.out.println(border);
        for (String line : lines) {
            if (line.length() > boxWidth) {
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
