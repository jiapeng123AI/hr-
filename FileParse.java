
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 文件解析器
 *
 * @author
 * @version 1.0
 * @date 2022/10/14/7:24 PM
 */
public class FileParse {

    private static String applicationFilePath = "applications.csv";

    private static String jobFilePath = "jobs.csv";

    private static final String SEPARATOR = System.getProperty("line.separator");

    /**
     * 解析工作文件
     *
     * @param filepath 文件路径
     * @return 工作列表
     */
    public static List<Job> parsingTheJobsFile(String filepath) {
        jobFilePath = filepath;
        // 没有该目录则新建
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try {
                    file.createNewFile();
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter(filepath);
                        fileWriter.write("createdAt,title,description,degree,salary,startDate"+ SEPARATOR);
                    } catch (IOException e) {
                        System.out.println();
                    } finally {
                        if (fileWriter != null) {
                            try {
                                fileWriter.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        List<Job> result = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int lineNumber = 2;
            Scanner scanner = new Scanner(fileInputStream);
            if(!scanner.hasNext()){
                return result;
            }
            // 删除首行
            scanner.nextLine();
            while (scanner.hasNext()) {
                String line = scanner.nextLine().trim();
                try {
                    List<String> fieldList = csvLineClean(line, 6);
                    Job job = Job.parseJobFromFileData(fieldList, lineNumber);
                    result.add(job);
                } catch (InvalidDataFormatException e) {
                    System.out.println("WARNING: invalid data format in jobs file in line {" + lineNumber + "}");
                } catch (NumberFormatException e) {
                    System.out.println("WARNING: invalid number format in jobs file in line {" + lineNumber + "}");
                } catch (InvalidCharacteristicException e) {
                    System.out.println("WARNING: invalid characteristic in jobs file in line {" + lineNumber + "}");
                } finally {
                    lineNumber++;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 解析工作申请文件
     *
     * @param filepath 文件路径
     */
    static public List<Application> parsingTheApplicationsFile(String filepath) {
        applicationFilePath = filepath;
        // 没有该目录则新建
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(filepath);
                    fileWriter.write("createdAt,lastname,firstname,careerSummary,age,gender,highestDegree,COMP90041,COMP90038,COMP90007,INFO90002,salayExpectations,availability" + SEPARATOR);
                } catch (IOException e) {
                    System.out.println();
                } finally {
                    if (fileWriter != null) {
                        try {
                            fileWriter.close();
                        } catch (IOException e) {
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        List<Application> result = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int lineNumber = 1;
            Scanner scanner = new Scanner(fileInputStream);
            if(!scanner.hasNext()){
                return result;
            }
            // 删除首行
            scanner.nextLine();
            while (scanner.hasNext()) {
                String line = scanner.nextLine().trim();
                try {
                    List<String> fieldList = csvLineClean(line, 13);
                    Application application = Application.parseApplicationFromFileData(fieldList, lineNumber);
                    result.add(application);
                } catch (InvalidDataFormatException e) {
                    System.out.println("WARNING: invalid data format in applications file in line {" + lineNumber + "}");
                } catch (NumberFormatException e) {
                    System.out.println("WARNING: invalid number format in applications file in line {" + lineNumber + "}");
                } catch (InvalidCharacteristicException e) {
                    System.out.println("WARNING: invalid characteristic in applications file in line {" + lineNumber + "}");
                } finally {
                    lineNumber++;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    /**
     * 存储所有的申请
     *
     * @param applications 申请列表
     */
    public static void saveApplications(List<Application> applications) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(applicationFilePath);
            fileWriter.write("createdAt,lastname,firstname,careerSummary,age,gender,highestDegree,COMP90041,COMP90038,COMP90007,INFO90002,salayExpectations,availability" + SEPARATOR);
            for (Application application : applications) {
                String line = application.convert2CsvFileStyle() + SEPARATOR;
                fileWriter.write(line);
            }
        } catch (IOException e) {
            System.out.println();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 加载所有的申请以及关联关系
     *
     * @return 申请人与工作的关联关系
     */
    public static List<UserApplicationJobRelation> loadAllUserApplicationJobRelation() {
        File file = new File("relations.csv");
        if (!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        List<UserApplicationJobRelation> result = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Scanner scanner = new Scanner(fileInputStream);
            while (scanner.hasNext()) {
                String line = scanner.nextLine().trim();
                UserApplicationJobRelation item = UserApplicationJobRelation.parseUserApplicationJobRelation(line);
                result.add(item);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static void saveAllUserApplicationJobRelation(List<UserApplicationJobRelation> relations) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("relations.csv");
            for (UserApplicationJobRelation relation : relations) {
                String line = relation.convert2CsvFileStyle() + SEPARATOR;
                fileWriter.write(line);
            }
        } catch (IOException e) {
            System.out.println();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 存储所有的job
     *
     * @param jobs job列表
     */
    public static void saveJobs(List<Job> jobs) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(jobFilePath);
            fileWriter.write("createdAt,title,description,degree,salary,startDate"+ SEPARATOR);
            for (Job job : jobs) {
                String line = job.convert2CsvFileStyle() + SEPARATOR;
                fileWriter.write(line);
            }
        } catch (IOException e) {
            System.out.println();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }


    /**
     * 解析CSV文件以及其可能携带,的字段
     *
     * @param line        csv文件的一行记录
     * @param filedNumber 需要的字段数量
     * @return 解析后的CSV文件 当某个需要字段为空的的时候对应的null值
     * @throws InvalidDataFormatException
     */
    public static List<String> csvLineClean(String line, int filedNumber) throws InvalidDataFormatException {
        // 数量对不上的 小于要求的字段个数
        if (containsCharNumberInString(line, ',') < filedNumber - 1) {
            throw new InvalidDataFormatException();
        }
        List<String> cleanData = new ArrayList<>();
        List<String> splitArray = split(line, ',');
        boolean flag = false;
        StringBuilder oneField = new StringBuilder();
        for (String item : splitArray) {
            if (item.length() == 0) {
                // 该字段为空
                cleanData.add(null);
                continue;
            }
            oneField.append(item);
            if (!flag && item.startsWith("\"")) {
                oneField.append(',');
                flag = true;
            } else if (flag && item.contains("\"")) {
                // 去掉"
                oneField.delete(0, 1);
                oneField.delete(oneField.length() - 1, oneField.length());
                cleanData.add(oneField.toString());
                oneField = new StringBuilder();
                flag = false;
            } else if (flag) {
                oneField.append(',');
            } else {
                cleanData.add(oneField.toString());
                oneField = new StringBuilder();
            }
        }

        return cleanData;
    }


    /**
     * 将字符串按某个字符切分
     *
     * @param line 字符串
     * @param c    字符
     * @return 切分后的字符串数组
     */
    private static List<String> split(String line, char c) {
        List<String> result = new ArrayList<>();
        int begIndex = 0;
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char now = chars[i];
            if (c == now && begIndex == i) {
                result.add("");
                begIndex = i + 1;
            } else if (c == now) {
                result.add(line.substring(begIndex, i));
                begIndex = i + 1;
            } else if (i == chars.length - 1) {
                result.add(line.substring(begIndex, i + 1));
            }
        }

        if (line.endsWith(c + "")) {
            result.add("");
        }
        return result;
    }

    public static int containsCharNumberInString(String line, char c) {
        int count = 0;
        for (char item : line.toCharArray()) {
            if (item == c) {
                count++;
            }
        }
        return count;
    }

}
