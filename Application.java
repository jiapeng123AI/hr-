import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @author
 * @version 1.0
 * @date 2022/10/14/7:01 PM
 */
public class Application {

    private Instant createdAt;

    private String lastname;

    private String firstname;

    private String careerSummary;

    private Integer age;

    private Gender gender;

    private Degree highestDegree;

    private Integer COMP90041;

    private Integer COMP90038;

    private Integer COMP90007;

    private Integer INFO90002;

    private Integer salrayExpectations;

    private LocalDate availability;
    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    public static Application parseApplicationFromFileData(List<String> fieldList, int lineNumber) throws InvalidDataFormatException, NumberFormatException, InvalidCharacteristicException {
        // Firstname, Lastname, and Age are mandatory data 必填项目
        Application application = new Application();

        try {
            CommonUtils.checkInputCreatedAt(fieldList.get(0));
            application.setCreatedAt(Instant.ofEpochSecond(Long.parseLong(fieldList.get(0))));
        } catch (NumberFormatException e) {
            // 创建时间允许错误 不需要抛弃这一行
            System.out.println("WARNING: invalid data format in applications file in line {" + lineNumber + "}");
        }
        if (fieldList.get(1) == null) {
            throw new InvalidDataFormatException();
        } else {
            application.setLastname(fieldList.get(1));
        }

        if (fieldList.get(2) == null) {
            throw new InvalidDataFormatException();
        } else {
            application.setFirstname(fieldList.get(2));
        }
        application.setCareerSummary(fieldList.get(3));

        if(fieldList.get(4) == null){
            throw new InvalidDataFormatException();
        }else {
            checkInputAge(fieldList.get(4));
            application.setAge(Integer.parseInt(fieldList.get(4)));
        }





        if (null != fieldList.get(5)) {
            try {
                checkInputGender(fieldList.get(5));
                application.setGender(Gender.parse(fieldList.get(9)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid characteristic in applications file in line {" + lineNumber + "}");
            }

        }


        if (null != fieldList.get(6)) {
            try {
                checkInputDegree(fieldList.get(6));
                application.setHighestDegree(Degree.parse(fieldList.get(6)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid characteristic in applications file in line {" + lineNumber + "}");
            }
        }


        if (null != fieldList.get(7)) {
            try {
                checkInputCourseWork(fieldList.get(7));
                application.setCOMP90041(Integer.parseInt(fieldList.get(7)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid number format in applications file in line {" + lineNumber + "}");
            }
        }


        if (null != fieldList.get(8)) {

            try {
                checkInputCourseWork(fieldList.get(8));
                application.setCOMP90038(Integer.parseInt(fieldList.get(8)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid number format in applications file in line {" + lineNumber + "}");
            }
        }


        if (null != fieldList.get(9)) {
            try {
                checkInputCourseWork(fieldList.get(9));
                application.setCOMP90007(Integer.parseInt(fieldList.get(9)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid number format in applications file in line {" + lineNumber + "}");
            }
        }


        if (null != fieldList.get(10)) {
            try {
                checkInputCourseWork(fieldList.get(10));
                application.setINFO90002(Integer.parseInt(fieldList.get(10)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid number format in applications file in line {" + lineNumber + "}");
            }
        }


        if (null != fieldList.get(11)) {
            try {
                checkInputSalrayExpectations(fieldList.get(11));
                application.setSalrayExpectations(Integer.parseInt(fieldList.get(11)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid number format in applications file in line {" + lineNumber + "}");
            }
        }
        if (null != fieldList.get(12)) {
            try {
                CommonUtils.checkInputDate(fieldList.get(12));
                application.setAvailability(CommonUtils.getLocalDateFromFile(fieldList.get(12)));
            } catch (InvalidCharacteristicException e) {
                System.out.println("WARNING: invalid characteristic in applications file in line {" + lineNumber + "}");
            }
        }

        return application;
    }

    public String convert2ListShowJob(String sequence) {
        return "[" + sequence + "]" + " " + lastname + " " + firstname + "(" + (highestDegree == null ? "n/a" : highestDegree.name()) + "):" + (careerSummary == null ? "n/a" : careerSummary) + ". " + "Salary Expectations: " + (salrayExpectations == null ? "n/a" : salrayExpectations) + ". " + "Availability: " + (availability == null ? "n/a" : availability.format(DATE_TIME_FORMATTER)) + ".";
    }

    public String convert2CsvFileStyle() {
        return createdAt.getEpochSecond() + "," + CommonUtils.convert2CsvFileStyleFiled(lastname) + "," + CommonUtils.convert2CsvFileStyleFiled(firstname) +
                "," + CommonUtils.convert2CsvFileStyleFiled(careerSummary) + "," + age + "," + (gender == null ? "" : gender.name()) + "," +
                (highestDegree == null ? "" : highestDegree.name()) + "," +
                (COMP90041 == null ? "" : COMP90041) + "," + (COMP90038 == null ? "" : COMP90038) + "," + (COMP90007 == null ? "" : COMP90007) + "," +
                (INFO90002 == null ? "" : INFO90002) + "," + (salrayExpectations == null ? "" : salrayExpectations) + "," +
                (availability == null ? "" : availability.format(DATE_TIME_FORMATTER));
    }



    public static void checkInputGender(String genderStr) throws NumberFormatException, InvalidCharacteristicException {
        Gender parse = Gender.parse(genderStr);
        if (parse == null) {
            throw new InvalidCharacteristicException();
        }
    }

    public static void checkInputDegree(String degreeStr) throws NumberFormatException, InvalidCharacteristicException {
        Degree parse = Degree.parse(degreeStr);
        if (parse == null) {
            throw new InvalidCharacteristicException();
        }
    }

    //Course Work grades range from 49 to 100.
    public static void checkInputCourseWork(String courseWork) throws NumberFormatException, InvalidCharacteristicException {
        int courseWorkNumber = Integer.parseInt(courseWork);
        if (courseWorkNumber < 49 || courseWorkNumber > 100) {
            throw new InvalidCharacteristicException();
        }
    }

    public static void checkInputSalrayExpectations(String salrayExpectationsStr) throws InvalidCharacteristicException {

        int age = Integer.parseInt(salrayExpectationsStr);
        if (age <= 0) {
            throw new InvalidCharacteristicException();
        }

    }

    public static void checkInputAge(String ageStr) throws NumberFormatException {
        if (ageStr == null) {
            return;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 0) {
                throw new InvalidCharacteristicException();
            }
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCareerSummary() {
        return careerSummary;
    }

    public void setCareerSummary(String careerSummary) {
        this.careerSummary = careerSummary;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Degree getHighestDegree() {
        return highestDegree;
    }

    public void setHighestDegree(Degree highestDegree) {
        this.highestDegree = highestDegree;
    }

    public Integer getCOMP90041() {
        return COMP90041;
    }

    public void setCOMP90041(Integer COMP90041) {
        this.COMP90041 = COMP90041;
    }

    public Integer getCOMP90038() {
        return COMP90038;
    }

    public void setCOMP90038(Integer COMP90038) {
        this.COMP90038 = COMP90038;
    }

    public Integer getCOMP90007() {
        return COMP90007;
    }

    public void setCOMP90007(Integer COMP90007) {
        this.COMP90007 = COMP90007;
    }

    public Integer getINFO90002() {
        return INFO90002;
    }

    public void setINFO90002(Integer INFO90002) {
        this.INFO90002 = INFO90002;
    }

    public Integer getSalrayExpectations() {
        return salrayExpectations;
    }

    public void setSalrayExpectations(Integer salrayExpectations) {
        this.salrayExpectations = salrayExpectations;
    }

    public LocalDate getAvailability() {
        return availability;
    }

    public void setAvailability(LocalDate availability) {
        this.availability = availability;
    }
}
