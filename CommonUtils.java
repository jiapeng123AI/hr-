

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Scanner;

/**
 * @author
 * @version 1.0
 * @date 2022/10/14/8:09 PM
 */
public class CommonUtils {
    public static void checkInputCreatedAt(String createdAt) throws NumberFormatException {
        try {
            Long.parseLong(createdAt);
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }
    public static int getDegreeValue(Degree degree) {
        if (degree.name().equals("PHD")) {
            return 3;
        } else if (degree.name().equals("Master")) {
            return 2;
        } else {
            return 1;
        }
    }


    public static void checkInputDate(String dateStr) throws InvalidCharacteristicException {
        try {
            String[] split = dateStr.split("/");
            if (split.length != 3) {
                throw new InvalidCharacteristicException();
            }
            int day = Integer.parseInt(split[0]);
            if (day < 0 || day > 31) {
                throw new InvalidCharacteristicException();
            }
            int month = Integer.parseInt(split[1]);
            if (month < 0 || month > 12) {
                throw new InvalidCharacteristicException();
            }
            int year = Integer.parseInt(split[2]);
        } catch (Exception e) {
            throw new InvalidCharacteristicException();
        }
    }
    public static LocalDate getLocalDateFromFile(String startDateStr) {
        String[] split = startDateStr.split("/");

        int day = Integer.parseInt(split[0]);

        int month = Integer.parseInt(split[1]);

        int year = Integer.parseInt(split[2]);
        if (year < 100) {
            year += 2000;
        }
        LocalDate localDate = LocalDate.of(year, Month.of(month), day);
        return localDate;
    }

    // feel free to modify this
    public static void displayWelcomeMessage(String filename) {

        Scanner inputStream = null;

        try {
            inputStream = new Scanner(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            System.out.println("Welcome File not found.");
        }

        while (inputStream.hasNextLine()) {
            System.out.println(inputStream.nextLine());
        }

    }

    public static String convert2CsvFileStyleFiled(String filed){
        if(null == filed){
            return "";
        }
        if(filed.contains(",")){
            return "\""+filed+"\"";
        }
        return filed;
    }
}
