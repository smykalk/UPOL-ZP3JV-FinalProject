package cz.upol.jj.finance_keeper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * A class handling user interface
 */
public class Client {
    private ListOfRecords records = new ListOfRecords();
    private ListOfRecords recordsToDisplay = new ListOfRecords();

    private Predicate<Record> filterPredicate = record -> true;

    private boolean filterActive = false;

    // Use newline as delimiter which allows spaces to be entered
    Scanner scanner = new Scanner(System.in).useDelimiter(System.lineSeparator());

    /**
     * Initializes the Client object by reading data from a 'saved_data' file into a ListOfRecords
     */
    public Client() {
        try {
            records.ReadFromFile("saved_data");
        } catch (IOException exception) {
        }
        records.OrderByDate(true);
        this.recordsToDisplay.addAll(records);
    }

    private void printMainMenu() {
        System.out.print(
                "Akce: 0 - konec, 1 - přidat příjem, 2 - přidat výdaj, 3 - odstranit záznam, 4 - seřadit záznamy, 5 - filtrovat záznamy");
        if(filterActive)
            System.out.println(", 6 - odstranit filtr(y)");
        else System.out.println();
        System.out.print("Vaše volba: ");
    }

    private void AddRecord(boolean expense) {
        LocalDate recordDate = null;
        String recordReason;
        BigDecimal recordAmount = null;

        boolean continueLoop = true;
        while (continueLoop) {
            System.out.print("Zadejte datum (1 - dnes, 2 - včera, D.M.RRRR - vlastní): ");
            String date = scanner.next();

            switch (date) {
                case "1":
                    recordDate = LocalDate.now();
                    continueLoop = false;
                    break;
                case "2":
                    recordDate = LocalDate.now().minusDays(1);
                    continueLoop = false;
                    break;
                default:
                    try {
                        recordDate = LocalDate.parse(date, PrintFormats.dateFormat);
                        continueLoop = false;
                    } catch (java.time.format.DateTimeParseException exception) {
                        System.out.println("Špatně zadané datum, zadejte znovu.");
                    }
                    break;
            }
        }
        System.out.print("Zadejte důvod: ");
        recordReason = scanner.next();

        continueLoop = true;
        while (continueLoop) {
            System.out.print("Zadejte částku: ");

            try {
                recordAmount = new BigDecimal(scanner.next());

                if (expense)
                    recordAmount = recordAmount.negate();

                continueLoop = false;
            } catch (NumberFormatException exception) {
                System.out.println("Špatně zadaná částka, zadejte znovu.");
            }
        }

        Record toBeAdded = new Record(recordDate, recordReason, recordAmount);

        records.add(toBeAdded);
        recordsToDisplay.add(toBeAdded);

        recordsToDisplay.Filter(filterPredicate);

        try {
            records.WriteToFile("saved_data");
        } catch (IOException exception) {
            System.out.println("Chyba při zápisu do souboru.");
        }
    }

    private void RemoveRecord() {
        int id = -1;

        boolean continueLoop = true;
        while (continueLoop) {
            recordsToDisplay.printWithIndices();
            System.out.print("Zadejte ID nebo -1 pro krok zpět: ");

            try {
                id = scanner.nextInt();
                if (id > recordsToDisplay.size() - 1)
                    throw new InputMismatchException("Invalid ID");
                continueLoop = false;
            } catch (InputMismatchException exception) {
                System.out.println(System.lineSeparator() + "Neexistující volba, zadejte znovu.");
            }
        }
        if (id == -1)
            return;

        Record toBeRemoved = recordsToDisplay.get(id);

        records.remove(toBeRemoved);
        recordsToDisplay.remove(toBeRemoved);

        try {
            records.WriteToFile("saved_data");
        } catch (IOException exception) {
            System.out.println("Chyba při zápisu do souboru.");
        }
    }

    private void OrderBy() {
        boolean continueLoop = true;

        while (continueLoop) {
            System.out.println(
                    "Seřadit podle (-1 - zpět, 1 - datum sestupně, 2 - datum vzestupně, 3 - částka sestupně, 4 - částka vzestupně)");

            switch (scanner.next()) {
                case "-1" -> {
                    return;
                }
                case "1" -> {
                    recordsToDisplay.OrderByDate(true);
                    records.OrderByDate(true);
                    continueLoop = false;
                }
                case "2" -> {
                    recordsToDisplay.OrderByDate(false);
                    records.OrderByDate(false);
                    continueLoop = false;
                }
                case "3" -> {
                    recordsToDisplay.OrderByAmount(true);
                    records.OrderByAmount(true);
                    continueLoop = false;
                }
                case "4" -> {
                    recordsToDisplay.OrderByAmount(false);
                    records.OrderByAmount(false);
                    continueLoop = false;
                }
                default -> System.out.println(System.lineSeparator() + "Neexistující volba, zadejte znovu.");
            }
        }
    }

    private LocalDate[] GetFromToDatesFromUser() throws DateTimeParseException {
        LocalDate[] dates = new LocalDate[2];

        System.out.print("od (D.M.RRRR, případně prázdné): ");
        String dateFrom = scanner.next();

        if (dateFrom.equals(""))
            dates[0] = LocalDate.MIN;
        else
            dates[0] = LocalDate.parse(dateFrom, PrintFormats.dateFormat).minusDays(1);


        System.out.print("do (D.M.RRRR, případně prázdné): ");
        String dateTo = scanner.next();

        if (dateTo.equals(""))
            dates[1] = LocalDate.MAX;
        else
            dates[1] = LocalDate.parse(dateTo, PrintFormats.dateFormat).plusDays(1);

        return dates;
    }

    private BigDecimal[] GetFromToAmountsFromUser() throws NumberFormatException {
        BigDecimal[] amounts = new BigDecimal[2];

        System.out.print("od (případně prázdné): ");
        String amountFrom = scanner.next();

        if (amountFrom.equals(""))
            amounts[0] = new BigDecimal(-Double.MAX_VALUE);
        else
            amounts[0] = new BigDecimal(amountFrom).subtract(BigDecimal.ONE);

        System.out.print("od (případně prázdné): ");
        String amountTo = scanner.next();

        if (amountTo.equals(""))
            amounts[1] = new BigDecimal(Double.MAX_VALUE);
        else
            amounts[1] = new BigDecimal(amountTo).add(BigDecimal.ONE);

        return amounts;
    }

    private Predicate<Record> Filter() {
        boolean continueLoop = true;

        while (continueLoop) {
            System.out.print(
                    "Filtrovat podle (-1 - zpět, 1 - datum (od-do), 2 - částka(od-do), 3 - pouze příjmy, 4 - pouze výdaje): ");

            switch (scanner.next()) {
                case "-1" -> {
                    return record -> true;
                }
                case "1" -> {
                    LocalDate[] dates = new LocalDate[0];
                    try {
                        dates = GetFromToDatesFromUser();
                        //continueLoop = false;
                    } catch (DateTimeParseException exception) {
                        System.out.println("Špatně zadané datum, zadejte znovu.");
                    }

                    filterActive = true;
                    return recordsToDisplay.FilterByDate(dates[0], dates[1]);
                }
                case "2" -> {
                    BigDecimal[] amounts = new BigDecimal[0];
                    try {
                        amounts = GetFromToAmountsFromUser();
                        //continueLoop = false;
                    } catch (NumberFormatException exception) {
                        System.out.println("Špatně zadaná částka, zadejte znovu.");
                    }

                    filterActive = true;
                    return recordsToDisplay.FilterByAmount(amounts[0], amounts[1]);
                }
                case "3" -> {
                    filterActive = true;
                    return recordsToDisplay.FilterByAmount(BigDecimal.ZERO, new BigDecimal(Double.MAX_VALUE));
                }
                case "4" -> {
                    filterActive = true;
                    return recordsToDisplay.FilterByAmount(new BigDecimal(-Double.MAX_VALUE), BigDecimal.ZERO);
                }
                default -> System.out.println(System.lineSeparator() + "Neexistující volba, zadejte znovu.");
            }
        }
        return record -> true;
    }

    private void RemoveFilters() {
        filterPredicate = record -> true;
        filterActive = false;
        recordsToDisplay.clear();
        recordsToDisplay.addAll(records);
    }

    /**
     * Starts the main loop with user interface
     */
    public void Loop() {
        boolean continueLoop = true;

        while (continueLoop) {
            recordsToDisplay.print();
            recordsToDisplay.printTotalAmount();

            printMainMenu();

            switch (scanner.next()) {
                case "0" -> {
                    continueLoop = false;
                    try {
                        records.WriteToFile("saved_data");
                    } catch (IOException exception) {
                        System.out.println("Chyba při zápisu do souboru.");
                    }
                }
                case "1" -> AddRecord(false);
                case "2" -> AddRecord(true);
                case "3" -> RemoveRecord();
                case "4" -> OrderBy();
                case "5" -> filterPredicate = filterPredicate.and(Filter());
                case "6" -> RemoveFilters();
                default -> System.out.println(System.lineSeparator() + "Neexistující volba, zadejte znovu.");
            }

            System.out.println();
        }
    }
}
