package cz.upol.jj.finance_keeper;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A collection of records that get displayed to the user
 */
public class ListOfRecords extends ArrayList<Record> {

    /**
     * Prints the whole collection with a header
     */
    public void print() {
        System.out.printf(PrintFormats.headerFormat, "Datum", "Důvod", "Částka");

        PrintFormats.PrintHorizontalLine(PrintFormats.horizontalLineLength);

        for (Record r : this) {
            r.print();
        }

        PrintFormats.PrintHorizontalLine(PrintFormats.horizontalLineLength);
    }

    /**
     * Prints the whole collection with an added 'ID' column (IDs match indices in collection)
     */
    public void printWithIndices() {
        System.out.printf(PrintFormats.headerWithIdFormat, "ID", "Datum", "Důvod", "Částka");

        PrintFormats.PrintHorizontalLine(PrintFormats.horizontalLineWithIdsLength);

        for (int i = 0; i < size(); i++) {
            System.out.printf("%4s", i);
            this.get(i).print();
        }

        PrintFormats.PrintHorizontalLine(PrintFormats.horizontalLineWithIdsLength);
    }

    /**
     * Prints the total amount with formatting
     */
    public void printTotalAmount() {
        double totalAmount = this.stream().mapToDouble(record -> record.getAmount().doubleValue()).sum();
        if(totalAmount > 0.0)
            System.out.printf(PrintFormats.totalFormatPositive, "Bilance:", totalAmount);
        else
            System.out.printf(PrintFormats.totalFormatNegative, "Bilance:", totalAmount);
    }

    /**
     * Allows to order the records in the collection by date
     *
     * @param desc Descending/ascending order
     */
    public void OrderByDate(boolean desc) {
        if (desc)
            Collections.sort(this, Comparator.comparing(Record::getDate, Comparator.reverseOrder()));
        else
            Collections.sort(this, Comparator.comparing(Record::getDate));

    }

    /**
     * Allows to order the records in the collection by amount
     *
     * @param desc Descending/ascending order
     */
    public void OrderByAmount(boolean desc) {
        if (desc)
            Collections.sort(this, Comparator.comparing(Record::getAmount, Comparator.reverseOrder()));
        else
            Collections.sort(this, Comparator.comparing(Record::getAmount));
    }

    /**
     * Allows to filter the records in the collection with a given predicate
     *
     * @param predicate A predicate to use while filtering
     */
    public void Filter(Predicate<Record> predicate) {
        Stream<Record> recordStream = this.stream().filter(predicate);
        List<Record> filteredList = recordStream.toList();
        this.clear();
        this.addAll(filteredList);
    }

    /**
     * Allows to filter the records in the collection by date
     *
     * @param from A from date (inclusive)
     * @param to   A to date (inclusive)
     * @return A predicate created from dates entered as parameters
     */
    public Predicate<Record> FilterByDate(LocalDate from, LocalDate to) {
        Predicate<Record> predicate = record -> record.getDate().isAfter(from) && record.getDate().isBefore(to);
        Stream<Record> recordStream = this.stream().filter(predicate);
        List<Record> filteredList = recordStream.toList();
        this.clear();
        this.addAll(filteredList);

        return predicate;
    }

    /**
     * Allows to filter the records in the collection by amount
     *
     * @param from A from amount (inclusive)
     * @param to   A to amount (inclusive)
     * @return A predicate created from amounts entered as parameters
     */
    public Predicate<Record> FilterByAmount(BigDecimal from, BigDecimal to) {
        Predicate<Record> predicate = record -> record.getAmount().compareTo(from) > 0 && record.getAmount().compareTo(
                to) < 0;
        Stream<Record> recordStream = this.stream().filter(predicate);
        List<Record> filteredList = recordStream.toList();
        this.clear();
        this.addAll(filteredList);

        return predicate;
    }


    private DataOutputStream OpenFileForWriting(String filename, boolean append) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(filename, append);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        return new DataOutputStream(bos);
    }

    private DataInputStream OpenFileForReading(String filename) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        BufferedInputStream bis = new BufferedInputStream(fis);

        return new DataInputStream(bis);
    }

    private void WriteRecord(DataOutputStream out, Record r) throws IOException {
        out.writeUTF(r.getDate().toString());
        out.writeUTF(r.getReason());
        out.writeUTF(r.getAmount().toString());
    }

    private Record LoadRecord(DataInputStream di) throws IOException {
        return new Record(LocalDate.parse(di.readUTF()), di.readUTF(), new BigDecimal(di.readUTF()));
    }

    /**
     * Writes the entire collection to a given file
     *
     * @param filename The name of the file
     * @throws IOException
     */
    public void WriteToFile(String filename) throws IOException {
        DataOutputStream out = OpenFileForWriting(filename, false);
        for (Record r : this) {
            WriteRecord(out, r);
        }
        out.close();
    }

    /**
     * Reads the entire collection from a given file
     *
     * @param filename The name of the file
     * @throws IOException
     */
    public void ReadFromFile(String filename) throws IOException {
        this.clear();

        try (DataInputStream in = OpenFileForReading(filename)) {
            while (true) {
                this.add(LoadRecord(in));
            }
        } catch (EOFException eofException) {
        }
    }
}
