package cz.upol.jj.finance_keeper;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A class for storing information about income/spending
 */
public class Record {
    private LocalDate date;
    private String reason;
    private BigDecimal amount;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Initializes a Record object
     * @param date Date of the record
     * @param reason Reason for the income/spending
     * @param amount The monetary amount
     * @throws NumberFormatException
     */
    public Record(LocalDate date, String reason, BigDecimal amount) throws NumberFormatException {
        this.date = date;
        this.reason = reason;
        this.amount = amount;
    }

    /**
     * Prints the record either green or red depending on the amount
     */
    public void print(){
        String format = "";

        if(amount.compareTo(new BigDecimal(0)) > 0)
            format = PrintFormats.recordFormatPositive;
        else
            format = PrintFormats.recordFormatNegative;

        System.out.printf(format, date.format(PrintFormats.dateFormat), reason, amount.toString());
    }
}
