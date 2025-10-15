package com.pluralsight;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * This method loads the transactions form the file,
     * transactions should be in this  format: date|time|description|vendor|amount
     * @param fileName - name of the file that should be read.
     */
    public static void loadTransactions(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] transaction = line.split("\\|");
                transactions.add(new Transaction(parseDate(transaction[0]), parseTime(transaction[1]), transaction[2], transaction[3], parseDouble(transaction[4])));
            }
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format "yyyy-MM-dd HH:mm:ss", description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     * @param scanner - scanner used to prompts user
     */
    private static void addDeposit(Scanner scanner) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true));
            System.out.println("Please enter the date and time of the transaction(yyyy-MM-dd HH:mm:ss): ");
            String[] dateAndTime =  scanner.nextLine().split(" ");
            LocalDate date = LocalDate.parse(dateAndTime[0], DATE_FMT);
            LocalTime time = LocalTime.parse((dateAndTime[1]),TIME_FMT);
            System.out.println("Please enter the description:");
            String description = scanner.nextLine();
            System.out.println("Please enter the vendor:");
            String vendor = scanner.nextLine();
            double amount = 0;
            //Validation that's making sure that here deposit is positive
            do {
                System.out.println("Please enter the positive amount:");
                 amount = parseDouble(scanner.nextLine());
            } while (amount < 0);

            Transaction transaction = new Transaction(date, time, description, vendor, amount);
            transactions.add(transaction);
            writer.write("\n"+transaction.getDate()+"|"+transaction.getTime()+"|"+transaction.getDescription()+"|"+transaction.getVendor()+"|"+transaction.getAmount());
            writer.close();
            System.out.println("New deposit has been added to the transactions");
        } catch (Exception e){
            System.out.println("Something went wrong! Please try again.");
        }

    }

    /**
     * Prompt for ONE date+time string in the format "yyyy-MM-dd HH:mm:ss", description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (negative) and append to the file.
     * @param scanner - scanner used to prompts user
     */
    private static void addPayment(Scanner scanner) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.csv", true));
            System.out.println("Please enter the date and time of the transaction(yyyy-MM-dd HH:mm:ss): ");
            String[] dateAndTime =  scanner.nextLine().split(" ");
            LocalDate date = LocalDate.parse(dateAndTime[0], DATE_FMT);
            LocalTime time = LocalTime.parse((dateAndTime[1]),TIME_FMT);
            System.out.println("Please enter the description:");
            String description = scanner.nextLine();
            System.out.println("Please enter the vendor:");
            String vendor = scanner.nextLine();
            double amount = 0;
            //Validation that's making sure that here deposit is positive
            do {
                System.out.println("Please enter the positive amount:");
                amount = parseDouble(scanner.nextLine());
            } while (amount < 0);

            Transaction transaction = new Transaction(date, time, description, vendor, -amount);
            transactions.add(transaction);
            writer.write("\n"+transaction.getDate()+"|"+transaction.getTime()+"|"+transaction.getDescription()+"|"+transaction.getVendor()+"|"+transaction.getAmount());
            writer.close();
            System.out.println("New payment has been added to the transactions");
        } catch (Exception e){
            System.out.println("Something went wrong! Please try again.");
        }
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */

    /**
     * Ledger menu
     * @param scanner - to input options
     */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */

    /**
     * Displays all transactions from newest to oldest
     */
    private static void displayLedger() {
        try {
            System.out.println("=========================================================================================");
            System.out.println("Date        | Time      | Description                 | Vendor              | Amount    |");
            for (int i = transactions.size()-1; i > 0 ; i--) {
                Transaction tr = transactions.get(i);
                PrintOut(tr);
            }
            System.out.println("=========================================================================================");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    /**
     * Displays only deposits
     */
    private static void displayDeposits() {
        try {
            System.out.println("=========================================================================================");
            System.out.println("Date        | Time      | Description                 | Vendor              | Amount    |");
            for (int i = transactions.size()-1; i > 0 ; i--) {
                Transaction tr = transactions.get(i);
                if(tr.getAmount()>0) {
                    PrintOut(tr);
                }
            }
            System.out.println("=========================================================================================");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    /**
     * Displays only payments
     */
    private static void displayPayments() {
        try {
        System.out.println("=========================================================================================");
        System.out.println("Date        | Time      | Description                 | Vendor              | Amount    |");
        for (int i = transactions.size()-1; i > 0 ; i--) {
            Transaction tr = transactions.get(i);
            if(tr.getAmount()<0) {
                PrintOut(tr);
            }
        }
            System.out.println("=========================================================================================");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */

    /**
     * Reports menu
     * @param scanner - to input option
     */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    LocalDate end = LocalDate.now();
                    LocalDate start = end.withDayOfMonth(1);
                    filterTransactionsByDate(start, end);
                }
                case "2" -> {
                    int year = Year.now().getValue();
                    boolean isLeapYear = Year.now().isLeap();
                    int month = LocalDate.now().getMonth().getValue()-1;
                    LocalDate start = LocalDate.of(year, month, 1 );
                    LocalDate end = LocalDate.of(year, month, numberOfDays(month, isLeapYear));
                    filterTransactionsByDate(start, end);
                }
                case "3" -> {
                    LocalDate end = LocalDate.now();
                    LocalDate start = end.withDayOfYear(1);
                    filterTransactionsByDate(start, end);
                }
                case "4" -> {
                    int year = Year.now().getValue()-1;
                    LocalDate start = LocalDate.of(year, 1, 1 );
                    LocalDate end = LocalDate.of(year, 12, 31);
                    filterTransactionsByDate(start, end);
                }
                case "5" -> {
                    System.out.println("Please enter name of the vendor:");
                    filterTransactionsByVendor(scanner.nextLine());
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */

    /**
     * Prints out the transactions within date range
     * @param start - LocalDate - from when method should start
     * @param end - LocalDate -  to when method should end.
     */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        System.out.println("=========================================================================================");
        System.out.println("Date        | Time      | Description                 | Vendor              | Amount    |");
        boolean printed = false;
        for(Transaction transaction : transactions){
            if (transaction.getDate().isAfter(start)&&transaction.getDate().isBefore(end)){
                PrintOut(transaction);
                printed = true;
            }
        }
        if (!printed){
            System.out.println("Sorry! There is nothing within this date range.");
        }
        System.out.println("=========================================================================================");
    }

    /**
     * Prints out the transactions from the selected vendor
     * @param vendor - vendor from which all transactions should be printed
     */
    private static void filterTransactionsByVendor(String vendor) {
        System.out.println("=========================================================================================");
        System.out.println("Date        | Time      | Description                 | Vendor              | Amount    |");
        boolean printed = false;
        for(Transaction transaction : transactions){
            if (transaction.getVendor().equalsIgnoreCase(vendor)){
                PrintOut(transaction);
                printed = true;
            }
        }
        if (!printed){
            System.out.println("Sorry! There is nothing from this vendor.");
        }
        System.out.println("=========================================================================================");
    }

    private static void customSearch(Scanner scanner) {

        boolean hasDateRange = false;
        boolean hasDescription = false;
        boolean hasVendor = false;
        boolean hasAmount = false;

        LocalDate start = null;
        LocalDate end = null;
        String vendor = "";
        double amount = 0.0;
        String description = "";

        System.out.println("Do you want to enter a date range?(yes/no)");
        if(scanner.nextLine().equalsIgnoreCase("yes")){
            System.out.println("Please enter the start date(yyyy-MM-dd):");
            start = parseDate(scanner.nextLine());
            System.out.println("Please enter the end date(yyyy-MM-dd):");
            end = parseDate(scanner.nextLine());
            hasDateRange = true;
        }


        System.out.println("Do you want to enter the description?(yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")){
            System.out.println("Please enter the description:");
            description = scanner.nextLine();
            hasDescription = true;
        }

        System.out.println("Do you want to enter the vendor?(yes/no)");
        if(scanner.nextLine().equalsIgnoreCase("yes")){
            System.out.println("Please enter the vendor:");
            vendor = scanner.nextLine();
            hasVendor = true;
        }

        System.out.println("Do you want to enter the amount?(yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")){
            System.out.println("Please enter the amount:");
            amount = Double.parseDouble(scanner.nextLine());
            hasAmount = true;
        }


        if (hasDateRange&!hasAmount&!hasDescription&!hasVendor){
            filterTransactionsByDate(start, end);
        }
        else if (hasVendor&!hasAmount&!hasDescription&!hasDateRange){
            filterTransactionsByVendor(vendor);
        }
        else{
            filterTransactionsByCustoms(start, end, vendor, description, amount);
        }
    }

    private static void filterTransactionsByCustoms(LocalDate start, LocalDate end, String vendor, String description, double amount) {
        System.out.println("=========================================================================================");
        System.out.println("Date        | Time      | Description                 | Vendor              | Amount    |");
        boolean printed = false;
        if (start == null&&vendor.equalsIgnoreCase("")&&amount==0.0&&!description.equalsIgnoreCase("")){
            for(Transaction transaction : transactions){
                if (transaction.getDescription().equalsIgnoreCase(description)){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        } else if (start == null&&vendor.equalsIgnoreCase("")&&amount!=0.0&&description.equalsIgnoreCase("")) {
            for(Transaction transaction : transactions){
                if (transaction.getAmount()==amount){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        } else if (start!=null&&!vendor.equalsIgnoreCase("")&&description.equalsIgnoreCase("")&&amount==0.0) {
            for(Transaction transaction : transactions){
                if (transaction.getVendor().equalsIgnoreCase(vendor)&&transaction.getDate().isAfter(start)&&transaction.getDate().isBefore(end)){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        } else if (start!=null&&!vendor.equalsIgnoreCase("")&&!description.equalsIgnoreCase("")&&amount==0.0) {
            for(Transaction transaction : transactions){
                if (transaction.getVendor().equalsIgnoreCase(vendor)&&transaction.getDate().isAfter(start)&&transaction.getDate().isBefore(end)&&transaction.getDescription().equalsIgnoreCase(description)){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        } else if (start!=null&&!vendor.equalsIgnoreCase("")&&!description.equalsIgnoreCase("")&&amount!=0.0) {
            for(Transaction transaction : transactions){
                if (transaction.getVendor().equalsIgnoreCase(vendor)&&transaction.getDate().isAfter(start)&&transaction.getDate().isBefore(end)&&transaction.getDescription().equalsIgnoreCase(description)&&transaction.getAmount()==amount){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        }else if (start==null&&!vendor.equalsIgnoreCase("")&&!description.equalsIgnoreCase("")&&amount==0.0) {
            for(Transaction transaction : transactions){
                if (transaction.getVendor().equalsIgnoreCase(vendor)&&transaction.getDescription().equalsIgnoreCase(description)){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        } else if (start==null&&!vendor.equalsIgnoreCase("")&&!description.equalsIgnoreCase("")&&amount!=0.0) {
            for(Transaction transaction : transactions){
                if (transaction.getVendor().equalsIgnoreCase(vendor)&&transaction.getDescription().equalsIgnoreCase(description)&&transaction.getAmount()==amount){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        }else if (start==null&&vendor.equalsIgnoreCase("")&&!description.equalsIgnoreCase("")&&amount!=0.0) {
            for(Transaction transaction : transactions){
                if (transaction.getDescription().equalsIgnoreCase(description)&&transaction.getAmount()==amount){
                    PrintOut(transaction);
                    printed = true;
                }
            }
        }

        if (!printed){
            System.out.println("Sorry! There is nothing for this parameters.");
        }
        System.out.println("=========================================================================================");
    }

    /**
     * This method takes string and converts it to LocalDate
     * @param s - String that should contain LocalDate
     * @return - LocalDate that was it inputted string
     */
    private static LocalDate parseDate(String s) {
        return LocalDate.parse(s, DATE_FMT);
    }

    /**
     * This method takes string and converts it to LocalTime
     * @param s - String that should contain LocalTime
     * @return - LocalTime that was it inputted string
     */
    private static LocalTime parseTime (String s) {
        return LocalTime.parse(s,TIME_FMT);
    }

    /**
     * This method takes string and converts it to double
     * @param s - String that should contain double
     * @return - double that was it inputted string
     */
    private static Double parseDouble(String s) {
        return Double.parseDouble(s);
    }

    /**
     * Prints the transaction
     * @param transaction - transaction that should be printed
     */
    private static void PrintOut(Transaction transaction){
        String formatString = "%-12s| %-10s| %-28s| %-20s| %-10s|";
        System.out.printf((formatString) + "%n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
    }

    /**
     * This method return number of days in month
     * @param month - int value of month (1 - January...)
     * @param isLeapYear - boolean variable that indicates if year is leap year.
     * @return - return number of days as int.
     */
    private static int numberOfDays(int month, boolean isLeapYear){
        switch (month){
            case 1, 3, 5, 7, 8,10, 12 -> {
                return 31;
            }
            case 2 ->{
                if(isLeapYear){
                  return 29;
                }
                else {
                    return 28;
                }
            }
            case 4, 6, 9, 11 -> {
                return 30;
            }

        }
        return 0;
    }
}
 