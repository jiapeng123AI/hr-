import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;



/**
 * @author
 * @version 1.0
 * @date 2022/10/14/7:01 PM
 */
public class Job {

    /**
     * 创建时间
     */
    private Instant createdAt;

    /**
     * 标题
     */
    private String title;

    /**
     * 工作描述
     */
    private String description;

    /**
     * 级别
     */
    private Degree degree;

    /**
     * 薪资
     */
    private Integer salary;

    /**
     * 工作开始时间
     */
    private LocalDate startDate;

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    /**
     * 通过文件数据生成Job对象
     *
     * @param filed 文件数据
     * @return Job对象
     */
    public static Job parseJobFromFileData(List<String> filed, int lineNumber) throws InvalidDataFormatException, NumberFormatException, InvalidCharacteristicException {
        // Position Title and Start Date are mandatory data fields.
        Job job = new Job();
        try {
            CommonUtils.checkInputCreatedAt(filed.get(0));
            job.setCreatedAt(Instant.ofEpochSecond(Long.parseLong(filed.get(0))));
        } catch (NumberFormatException e) {
            // 创建时间允许错误 不需要抛弃这一行
            System.out.println("WARNING: invalid data format in jobs file in line {" + lineNumber + "}");
        }

        // 没有标题则不用这一行了 需要报错
        checkInputTitle(filed.get(1));
        job.setTitle(filed.get(1));
        job.setDescription(filed.get(2));
        if (filed.get(3) != null) {
            try {
                checkInputDegree(filed.get(3));
                job.setDegree(Degree.parse(filed.get(3)));
            } catch (InvalidCharacteristicException exception) {
                System.out.println("WARNING: invalid characteristic in jobs file in line {" + lineNumber + "}");
            }
        }


        if (null != filed.get(4)) {
            try {
                checkInputSalary(filed.get(4));
            job.setSalary(Integer.parseInt(filed.get(4)));
            } catch (NumberFormatException exception) {
                System.out.println("WARNING: invalid number format in jobs file in line {" + lineNumber + "}");
            }
        }

        CommonUtils.checkInputDate(filed.get(5));
        try {
            job.setStartDate(CommonUtils.getLocalDateFromFile(filed.get(5)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return job;
    }


    public static void checkInputTitle(String title) throws InvalidDataFormatException {
        if (null == title) {
            throw new InvalidDataFormatException();
        }
    }

    public static void checkInputDescription(String description) throws InvalidCharacteristicException {
        if (null == description) {
            throw new InvalidCharacteristicException();
        }
    }

    public static void checkInputDegree(String degreeStr) throws InvalidCharacteristicException {
        Degree degree = Degree.parse(degreeStr);
        if (Objects.isNull(degree)) {
            throw new InvalidCharacteristicException();
        }
    }

    public static void checkInputSalary(String salaryStr) throws NumberFormatException {
        try {
            int salary = Integer.parseInt(salaryStr);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public String convert2ListShowJob(Integer number) {
        return "[" + number + "]" + " " + title + "(" + (description == null ? "n/a" : description) + ")" + "." + " " + (degree == null ? "n/a" : degree.name()) + ". "
                + "Salary: " + (salary == null ? "n/a" : salary) + ". " + "Start Date: " + (startDate == null ? "n/a" : startDate.format(DATE_TIME_FORMATTER)) + ".";
    }

    public String convert2CsvFileStyle() {
//        createdAt,title,description,degree,salary,startDate
        return createdAt.getEpochSecond() + "," +
                CommonUtils.convert2CsvFileStyleFiled(title) + "," +
                CommonUtils.convert2CsvFileStyleFiled(description) + "," +
                (degree == null ? "" : degree.name()) + "," +
                (salary == null ? "" : salary) + "," +
                (startDate == null ? "" : startDate.format(DATE_TIME_FORMATTER));
    }
}
