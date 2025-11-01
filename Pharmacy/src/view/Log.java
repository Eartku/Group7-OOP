package view;

public class Log {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String BLUE = "\u001B[94m";
    public static final String MAGENTA = "\u001B[35m";

    // [REQUEST] — Dành cho nhập dữ liệu
    public static void exit(String message) {
        System.out.print(CYAN + message + RESET + "\n");
    }

    public static void request(String message) {
        System.out.print(MAGENTA + "[REQUEST] " + message + RESET);
    }

    // [INFO] — Dành cho thông báo hệ thống / tiến trình
    public static void info(String msg) {
        System.out.println(BLUE + "[INFO] " + msg + RESET);
    }

    // [SUCCESS] — Dành cho hành động thành công
    public static void success(String msg) {
        System.out.println(GREEN + "[SUCCESS] " + msg + RESET);
    }

    // [WARNING] — Cảnh báo nhẹ
    public static void warning(String msg) {
        System.out.println(YELLOW + "[WARNING] " + msg + RESET);
    }

    // [ERROR] — Lỗi nghiêm trọng
    public static void error(String msg) {
        System.out.println(RED + "[ERROR] " + msg + RESET);
    }
}
