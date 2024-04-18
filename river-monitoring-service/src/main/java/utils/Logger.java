package utils;

public class Logger {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[34m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";


    public static void info(String message) {
        System.out.println("\t" + ANSI_CYAN + "[*] " + message + ANSI_RESET);
    }

    public static void error(String message) {
        System.out.println(ANSI_RED + "[-] " + message + ANSI_RESET);
    }

    public static void success(String message) {
        System.out.println(ANSI_GREEN + "[+] " + message + ANSI_RESET);
    }

    public static void warning(String message) {
        System.out.println(ANSI_YELLOW + "[!] " + message + ANSI_RESET);
    }
}
