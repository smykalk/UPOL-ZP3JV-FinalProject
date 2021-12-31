package cz.upol.jj.finance_keeper;

import java.time.format.DateTimeFormatter;

/**
 * Contains formats used for printing the user interface in the 'Client' class
 */
public class PrintFormats {
    public static final String headerFormat = ConsoleColors.WHITE_BACKGROUND + ConsoleColors.BLACK_BOLD + " %10s%30s%23s " + ConsoleColors.RESET + System.lineSeparator();
    public static final String headerWithIdFormat = ConsoleColors.WHITE_BACKGROUND + ConsoleColors.BLACK_BOLD + " %4s%10s%30s%23s " + ConsoleColors.RESET + System.lineSeparator();

    public static final String currency = "Kč";
    public static final String recordFormatPositive = " %10s%30s" + ConsoleColors.GREEN_BOLD + "%20s " + currency + " " + ConsoleColors.RESET + System.lineSeparator();
    public static final String recordFormatNegative = " %10s%30s" + ConsoleColors.RED_BOLD + "%20s " + currency + " " + ConsoleColors.RESET + System.lineSeparator();

    public static final String totalFormatPositive = ConsoleColors.WHITE_BACKGROUND + ConsoleColors.BLACK_BOLD + "%s" + ConsoleColors.RESET + ConsoleColors.GREEN_BOLD + " %.2f" + ConsoleColors.RESET + System.lineSeparator();
    public static final String totalFormatNegative = ConsoleColors.WHITE_BACKGROUND + ConsoleColors.BLACK_BOLD + "%s" + ConsoleColors.RESET + ConsoleColors.RED_BOLD + " %.2f" + ConsoleColors.RESET + System.lineSeparator();

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy");


    public static final int horizontalLineLength = 65;
    public static final int horizontalLineWithIdsLength = 69;

    public static void PrintHorizontalLine(int length) {
        for (int i = 0; i < length; i++)
            System.out.print("-");
        System.out.println();
    }
}
