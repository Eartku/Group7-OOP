package view;

public class Log {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String BLUE = "\u001B[94m";
    public static final String MAGENTA = "\u001B[35m";

    // [EXIT] — Dành cho thông báo kết thúc
    public static void exit(String message) {
        System.out.print(CYAN + message + RESET + "\n");
    }

    public static void request(String message) {
        System.out.print(MAGENTA + message + RESET);
    }

    // [INFO]
    public static void info(String msg) {
        System.out.println(BLUE + msg + RESET);
    }

    // [SUCCESS]
    public static void success(String msg) {
        System.out.println(GREEN + msg + RESET);
    }

    // [WARNING]
    public static void warning(String msg) {
        System.out.println(YELLOW + msg + RESET);
    }

    // [ERROR]
    public static void error(String msg) {
        System.out.println(RED + msg + RESET);
    }


    public static String toInfo(String msg) {
        return BLUE + msg + RESET;
    }

    public static String toSuccess(String msg) {
        return GREEN + msg + RESET;
    }

    public static String toWarning(String msg) {
        return YELLOW + msg + RESET;
    }

    public static String toError(String msg) {
        return RED + msg + RESET;
    }

    public static String toRequest(String msg) {
        return MAGENTA + msg + RESET;
    }
}
