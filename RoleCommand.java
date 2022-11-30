import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;


/**
 * @author
 * @version 1.0
 * @date 2022/10/14/11:53 PM
 */
public class RoleCommand extends AbstractCommand {

    private Integer applicationsSubmittedCount = 0;

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    private static Set<Instant> hasSubmittedJob = new HashSet<>();

    private boolean hasCreatedApplication = false;

    private boolean hasInput = false;

    private Application application;

    public RoleCommand(List<String> args) {
        super(args);
    }

    @Override
    public void action(HRAssistant hrAssistant) {
        // 进行参数检查
        // 是否携带参数
        if (args.size() == 0) {
            System.out.println("ERROR: no role defined.");
            throw new IllegalArgumentException();
        }

        String roleStr = args.get(0);
        Role role = Role.parse(roleStr);
        if (Objects.isNull(role)) {
            System.out.println("ERROR: " + roleStr + " is not a valid role.");
            throw new IllegalArgumentException();
        }

        switch (role) {
            case hr:
                hrOperation(hrAssistant);
                break;
            case applicant:
                applicationOperation(hrAssistant);
                break;
            case audit:
                auditOperation(hrAssistant);
                break;
            default:
                break;
        }

    }


    private void applicationOperation(HRAssistant hrAssistant) {
        CommonUtils.displayWelcomeMessage("welcome_applicant.ascii");
        System.out.println();
        while (true) {
            printApplicationOperationMenu(hrAssistant);
            Scanner scanner = new Scanner(System.in);
            if (!scanner.hasNext()) {
                if(hasInput){
                    System.out.println();
                }
                break;
            }
            String userInput = scanner.nextLine().trim();
            hasInput = true;
            while (true) {
                if (isRightApplicationOperationCommand(userInput)) {
                    break;
                } else {
                    System.out.print("Invalid input! Please enter a valid command to continue:\n" + ">");
                    userInput = scanner.nextLine().trim();
                }
            }


            switch (userInput) {
                case "create":
                case "c":
                    createNewApplication(scanner, hrAssistant);
                    List<UserApplicationJobRelation> userApplicationJobRelations = hrAssistant.getUserApplicationJobRelations();
                    FileParse.saveAllUserApplicationJobRelation(userApplicationJobRelations);
                    break;
                case "jobs":
                case "j":
                    applicaitionListAvailableJobs(scanner, hrAssistant);
                    break;
                case "quit":
                case "q":
                    FileParse.saveApplications(hrAssistant.getApplications());
                    return;
            }
        }
    }

    private void applicaitionListAvailableJobs(Scanner scanner, HRAssistant hrAssistant) {
        List<Job> jobs = hrAssistant.getJobs();
        int number = 1;
        Map<Integer, Job> numberAndJob = new HashMap<>();
        sortJobByCreatedTime(jobs);

        if (jobs.size() == hasSubmittedJob.size()) {
            System.out.println("No jobs available.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (hasSubmittedJob.contains(job.getCreatedAt())) {
                continue;
            }
            numberAndJob.put(number, job);
            String line = job.convert2ListShowJob(number++);
            System.out.println(line);
        }

        // 如果已经创建了简历则可以进行工作申请
        List<UserApplicationJobRelation> relations = new ArrayList<>();
        if (hasCreatedApplication) {
            System.out.print("Please enter the jobs you would like to apply for (multiple options are possible): ");
            String line = scanner.nextLine().trim();
            if (line.length() != 0) {
                String[] splitNumber = line.split(",");
                for (String numberStr : splitNumber) {
                    numberStr = numberStr.trim();
                    if (numberStr.length() != 0) {
                        int num = Integer.parseInt(numberStr);
                        Job job = numberAndJob.get(num);

                        if (Objects.nonNull(job) && !hasSubmittedJob.contains(job.getCreatedAt())) {
                            UserApplicationJobRelation userApplicationJobRelation = new UserApplicationJobRelation();
                            userApplicationJobRelation.setCreatedAt(Instant.now().getEpochSecond());
                            userApplicationJobRelation.setApplicationCreatedAt(application.getCreatedAt().getEpochSecond());
                            userApplicationJobRelation.setJobCreatedAt(job.getCreatedAt().getEpochSecond());
                            relations.add(userApplicationJobRelation);
                            applicationsSubmittedCount++;
                            hasSubmittedJob.add(job.getCreatedAt());
                        }
                    }
                }
            }
        }
        hrAssistant.getUserApplicationJobRelations().addAll(relations);
    }

    private void createNewApplication(Scanner scanner, HRAssistant hrAssistant) {
        Application application = new Application();
        System.out.println("# Create new Application");
        String lastname = "";
        System.out.print("Lastname: ");
        while (true) {
            lastname = scanner.nextLine().trim();
            if (lastname.length() == 0) {
                System.out.print("Ooops! Lastname must be provided:");
            } else {
                break;
            }
        }
        application.setLastname(lastname);
        String firstname = "";
        System.out.print("Firstname: ");
        while (true) {
            firstname = scanner.nextLine().trim();
            if (firstname.length() == 0) {
                System.out.print("Ooops! Firstname must be provided:");
            } else {
                break;
            }
        }
        application.setFirstname(firstname);
        System.out.print("Career Summary: ");
        String careerSummary = scanner.nextLine().trim();
        application.setCareerSummary(careerSummary);

        System.out.print("Age: ");
        int age = 0;
        boolean ageRight = false;
        while (!ageRight) {
            String ageStr = scanner.nextLine().trim();
            if (ageStr.length() == 0) {
                System.out.print("Ooops! Age must be provided: ");
            } else {
                try {
                    age = Integer.parseInt(ageStr);
                    if (age <= 18 || age >= 100) {
                        System.out.print("Ooops! A valid age between 18 and 100 must be provided:");
                    } else {
                        ageRight = true;
                    }
                } catch (Exception e) {
                    // TODO 文档中没有给出具体的 异常输入是什么 得看老师具体的测试了
                    System.out.println("");
                }
            }
        }
        application.setAge(age);

        System.out.print("Gender: ");
        String genderStr = scanner.nextLine().trim();
        // 允许为空 但是不允许有错误的输入
        if (genderStr.length() != 0) {
            Gender gender = Gender.parse(genderStr);
            while (Objects.isNull(gender)) {
                System.out.print("Invalid input! Please specify Gender:");
                genderStr = scanner.nextLine().trim();
                if (genderStr.length() == 0) {
                    // 允许输入为空
                    break;
                } else {
                    gender = Gender.parse(genderStr);
                }
            }
            application.setGender(gender);
        }

        System.out.print("Highest Degree: ");
        String highestDegree = scanner.nextLine().trim();
        // 允许为空 但是不允许有错误的输入
        if (highestDegree.length() != 0) {
            Degree degree = Degree.parse(highestDegree);
            while (Objects.isNull(degree)) {
                System.out.print("Invalid input! Please specify Highest Degree: ");
                highestDegree = scanner.nextLine().trim();
                if (highestDegree.length() == 0) {
                    // 允许输入为空
                    break;
                } else {
                    degree = Degree.parse(highestDegree);
                }
            }
            application.setHighestDegree(degree);
        }

        System.out.println("Coursework: ");
        System.out.print("- COMP90041: ");
        String COMP90041Str = scanner.nextLine().trim();
        if (COMP90041Str.length() != 0) {
            while (true) {
                int COMP90041 = Integer.parseInt(COMP90041Str);
                if (COMP90041Str.length() == 0) {
                    break;
                }
                if (COMP90041 < 49 || COMP90041 > 100) {
                    System.out.print("Invalid input! Please specify COMP90041:");
                    COMP90041Str = scanner.nextLine().trim();
                } else {
                    break;
                }
            }
            if (COMP90041Str.length() != 0) {
                application.setCOMP90041(Integer.parseInt(COMP90041Str));
            }
        }

        System.out.print("- COMP90038: ");
        String COMP90038Str = scanner.nextLine().trim();
        if (COMP90038Str.length() != 0) {
            while (true) {
                int COMP90038 = Integer.parseInt(COMP90038Str);
                if (COMP90038Str.length() == 0) {
                    break;
                }
                if (COMP90038 < 49 || COMP90038 > 100) {
                    System.out.print("Invalid input! Please specify COMP90038:");
                    COMP90038Str = scanner.nextLine().trim();
                } else {
                    break;
                }
            }
            if (COMP90038Str.length() != 0) {
                application.setCOMP90038(Integer.parseInt(COMP90038Str));
            }
        }

        System.out.print("- COMP90007: ");
        String COMP90007Str = scanner.nextLine().trim();
        if (COMP90007Str.length() != 0) {
            while (true) {
                int COMP90007 = Integer.parseInt(COMP90007Str);
                if (COMP90007Str.length() == 0) {
                    break;
                }
                if (COMP90007 < 49 || COMP90007 > 100) {
                    System.out.print("Invalid input! Please specify COMP90007:");
                    COMP90007Str = scanner.nextLine().trim();
                } else {
                    break;
                }
            }
            if (COMP90007Str.length() != 0) {
                application.setCOMP90007(Integer.parseInt(COMP90007Str));
            }
        }

        System.out.print("- INFO90002: ");
        String INFO90002Str = scanner.nextLine().trim();
        if (INFO90002Str.length() != 0) {
            while (true) {
                int INFO90002 = Integer.parseInt(INFO90002Str);
                if (INFO90002Str.length() == 0) {
                    break;
                }
                if (INFO90002 < 49 || INFO90002 > 100) {
                    System.out.print("Invalid input! Please specify INFO90002:");
                    INFO90002Str = scanner.nextLine().trim();
                } else {
                    break;
                }
            }
            if (INFO90002Str.length() != 0) {
                application.setINFO90002(Integer.parseInt(INFO90002Str));
            }
        }

        System.out.print("Salary Expectations ($ per annum): ");
        String salaryExpectationsStr = scanner.nextLine().trim();
        if (salaryExpectationsStr.length() != 0) {
            while (true) {
                if (salaryExpectationsStr.length() == 0) {
                    break;
                }
                int salaryExpectations = Integer.parseInt(salaryExpectationsStr);
                if (salaryExpectations <= 0) {
                    System.out.print("Invalid input! Please specify Salary Expectations ($ per annum):");
                    salaryExpectationsStr = scanner.nextLine().trim();
                } else {
                    break;
                }
            }
            if (salaryExpectationsStr.length() != 0) {
                application.setSalrayExpectations(Integer.parseInt(salaryExpectationsStr));
            }
        }

        System.out.print("Availability: ");
        String availabilityStr = scanner.nextLine().trim();
        if (availabilityStr.length() != 0) {
            while (true) {
                if (availabilityStr.length() == 0) {
                    break;
                }
                try {
                    TemporalAccessor parse = DATE_TIME_FORMATTER.parse(availabilityStr);
                    LocalDate availability = LocalDate.parse(availabilityStr, DATE_TIME_FORMATTER);
                    LocalDate now = LocalDate.now();
                    break;
                } catch (Exception e) {
                    System.out.print("Invalid input! Please specify Availability:");
                    availabilityStr = scanner.nextLine().trim();
                }

            }
            if (availabilityStr.length() != 0) {
                LocalDate availability = LocalDate.parse(availabilityStr, DATE_TIME_FORMATTER);
                application.setAvailability(availability);
            }
        }
        application.setCreatedAt(Instant.now());
        hrAssistant.addApplication(application);
        hasCreatedApplication = true;
        this.application = application;
    }

    private static ArrayList<String> APPLICATION_OPERATION_COMMAND = new ArrayList<>();

    static {
        APPLICATION_OPERATION_COMMAND.add("create");
        APPLICATION_OPERATION_COMMAND.add("c");
        APPLICATION_OPERATION_COMMAND.add("jobs");
        APPLICATION_OPERATION_COMMAND.add("j");
        APPLICATION_OPERATION_COMMAND.add("quit");
        APPLICATION_OPERATION_COMMAND.add("q");
    }

    private boolean isRightApplicationOperationCommand(String userInput) {
        return APPLICATION_OPERATION_COMMAND.contains(userInput);
    }


    private static ArrayList<String> HR_OPERATION_COMMAND = new ArrayList<>();

    static {
        HR_OPERATION_COMMAND.add("create");
        HR_OPERATION_COMMAND.add("c");
        HR_OPERATION_COMMAND.add("jobs");
        HR_OPERATION_COMMAND.add("j");
        HR_OPERATION_COMMAND.add("applicants");
        HR_OPERATION_COMMAND.add("a");
        HR_OPERATION_COMMAND.add("filter");
        HR_OPERATION_COMMAND.add("f");
        HR_OPERATION_COMMAND.add("match");
        HR_OPERATION_COMMAND.add("m");
        HR_OPERATION_COMMAND.add("quit");
        HR_OPERATION_COMMAND.add("q");
    }

    private boolean isRightHROperationCommand(String userInput) {
        return HR_OPERATION_COMMAND.contains(userInput);
    }

    private void auditOperation(HRAssistant hrAssistant) {
        System.out.println("======================================\n" + "# Matchmaking Audit\n" + "======================================\n");
        if (hrAssistant.getJobs().size() == 0) {
            System.out.println("No jobs available for interrogation.");
            return;
        }

        if (hrAssistant.getApplications().size() == 0) {
            System.out.println("No applicants available for interrogation.");
            return;
        }
        System.out.println("Available jobs: {" + hrAssistant.getJobs().size() + "}\n" + "Total number of applicants: {" + hrAssistant.getUserApplicationJobRelations().size() + "}");

        List<Application> applications = hrAssistant.getApplications();
        List<Application> computeApplications = new ArrayList<>();
        computeApplications.addAll(applications);
        List<Job> jobs = hrAssistant.getJobs();
        Map<Job, Application> jobAndSelectedApplication = new HashMap<>();
        List<Application> selectedApplicationList = new ArrayList<>();
        for (Job job : jobs) {
            Application selectedApplication = Matchmaker.matchJob(job, computeApplications);
            if (Objects.nonNull(selectedApplication)) {
                selectedApplicationList.add(selectedApplication);
                jobAndSelectedApplication.put(job, selectedApplication);
                // 下次进行判断不再选择该申请人
                computeApplications.remove(selectedApplication);
            }
        }

        // 进行统计数据的计算
        System.out.println("Number of successful matches: " + selectedApplicationList.size());
        double selectedAverageAge = 0.0;
        for (Application item : selectedApplicationList) {
            selectedAverageAge += item.getAge();
        }
        double allAverageAge = 0.0;
        for (Application item : applications) {
            allAverageAge += item.getAge();
        }
        System.out.println("Average age: " + String.format("%.2f", selectedAverageAge / selectedApplicationList.size()) + " (average age of all applicants: " + String.format("%.2f", allAverageAge / applications.size()) + ")");
        System.out.println("Average WAM: " + (getTotalHasScoreClassCount(selectedApplicationList) == 0 ? "n/a" : String.format("%.2f", getTotalClassScore(selectedApplicationList) * 1.0 / getTotalHasScoreClassCount(selectedApplicationList))) + " (average WAM of all applicants: " + (getTotalHasScoreClassCount(applications) == 0 ? "n/a" : String.format("%.2f", getTotalClassScore(applications) * 1.0 / getTotalHasScoreClassCount(applications)) + ")"));

        // 学历 和 性别的统计计算
        // 如果不匹配的人里面存在有相关的数据 则要进行相关的统计计算
        Set<Degree> containsDegree = new HashSet<>();
        Set<Gender> containsGender = new HashSet<>();
        for (Application item : applications) {
            if (!selectedApplicationList.contains(item)) {
                if (Objects.nonNull(item.getGender())) {
                    containsGender.add(item.getGender());
                }

                if (Objects.nonNull(item.getHighestDegree())) {
                    containsDegree.add(item.getHighestDegree());
                }
            }
        }

        // 进行统计处理
        List<StatisticData> statisticDataList = new ArrayList<>();
        if (containsDegree.size() > 0) {
            for (Degree degree : containsDegree) {
                statisticDataList.add(getStatisticDataByDegree(degree, applications, selectedApplicationList));
            }
        }

        if (containsGender.size() > 0) {
            for (Gender gender : containsGender) {
                statisticDataList.add(getStatisticDataByGender(gender, applications, selectedApplicationList));
            }
        }

        // 将统计结果进行排序
        if (statisticDataList.size() > 0) {
            statisticDataList.sort(new Comparator<StatisticData>() {
                @Override
                public int compare(StatisticData o1, StatisticData o2) {
                    double diff = o1.getScore() - o2.getScore();
                    if (diff < 0.0001 && diff > -0.0001) {
                        return o1.getName().compareTo(o2.getName());
                    } else if (diff < 0) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
        }
        // 打印结果
        for (StatisticData statisticData : statisticDataList) {
            System.out.println(statisticData.getName() + " " + String.format("%.2f", statisticData.getScore()));
        }

    }

    /**
     * 根据相应的性别计算统计数据
     *
     * @param gender 性别
     * @return 统计数据
     */
    private StatisticData getStatisticDataByGender(Gender gender, List<Application> allApplications, List<Application> selectedApplications) {
        int countSelected = 0;
        int allCount = 0;
        for (Application selectedApplication : selectedApplications) {
            if (Objects.nonNull(selectedApplication.getGender()) && selectedApplication.getGender().equals(gender)) {
                countSelected++;
            }
        }

        for (Application item : allApplications) {
            if (Objects.nonNull(item.getGender()) && item.getGender().equals(gender)) {
                allCount++;
            }
        }

        return new StatisticData(gender.name(), countSelected * 1.0 / allCount);
    }

    private StatisticData getStatisticDataByDegree(Degree degree, List<Application> allApplications, List<Application> selectedApplications) {
        int countSelected = 0;
        int allCount = 0;
        for (Application selectedApplication : selectedApplications) {
            if (Objects.nonNull(selectedApplication.getHighestDegree()) && selectedApplication.getHighestDegree().equals(degree)) {
                countSelected++;
            }
        }

        for (Application item : allApplications) {
            if (Objects.nonNull(item.getHighestDegree()) && item.getHighestDegree().equals(degree)) {
                allCount++;
            }
        }

        return new StatisticData(degree.name(), countSelected * 1.0 / allCount);
    }

    private int getTotalClassScore(List<Application> applications) {
        int score = 0;
        for (Application item : applications) {
            score += getTotalScore(item);
        }
        return score;
    }

    private int getTotalHasScoreClassCount(List<Application> applications) {
        int countNumber = 0;
        for (Application item : applications) {
            countNumber += classTotalNumber(item);
        }
        return countNumber;
    }


    /**
     * HR角色的处理流程
     *
     * @param hrAssistant hr主控
     */
    private void hrOperation(HRAssistant hrAssistant) {
         
        CommonUtils.displayWelcomeMessage("welcome_hr.ascii");
        while (true) {
            printHrOperationMenu(hrAssistant);
            Scanner scanner = new Scanner(System.in);
           if(!scanner.hasNext()){
System.out
.println("Invalid input! Please enter a valid command to continue: \n" + "> ");
                return;
            }
            String userInput = scanner.nextLine().trim();
            hasInput = true;

            while (!isRightHROperationCommand(userInput)) {
                System.out.println("Invalid input! Please enter a valid command to continue: ");
                System.out.print("> ");
                
                userInput = scanner.nextLine();
              
            }
            
            switch (userInput) {
                case "create":
                case "c":
                    createNewJob(scanner, hrAssistant);
                    break;
                case "jobs":
                case "j":
                    hrListAvailableJobs(scanner, hrAssistant);
                    break;
                case "applicants":
                case "a":
                    hrListApplicants(scanner, hrAssistant);
                    break;
                case "filter":
                case "f":
                    hrFilterApplicants(scanner, hrAssistant);
                    break;
                case "match":
                case "m":
                    matchmaking(scanner, hrAssistant);
                    break;
                case "quit":
                case "q":
                System.out.println();
                    return;
            }
        }
    }

    private void matchmaking(Scanner scanner, HRAssistant hrAssistant) {
        List<Job> jobs = hrAssistant.getJobs();
        // 没有jobs
        if (jobs.size() == 0) {
            System.out.println("No jobs available.");
        }

        List<Application> applications = hrAssistant.getApplications();
        if (applications.size() == 0) {
            System.out.println("No applications available.");
        }

        // 按创建时间排序
        sortJobByCreatedTime(jobs);
        int showNumber = 1;
        for (Job job : jobs) {
            // 没有匹配项目不列出该job
            List<UserApplicationJobRelation> relations = hrAssistant.getRelationsByJobCreated(job.getCreatedAt().getEpochSecond());
            // 按申请时间排序
            sortRelationByCreatedTime(relations);
            List<Application> applicationList = new ArrayList<>();
            for (UserApplicationJobRelation relation : relations) {
                Application application = hrAssistant.getApplicationByCreated(relation.getApplicationCreatedAt());
                applicationList.add(application);
            }
            Application applicationMatch = Matchmaker.matchJob(job, applicationList);
            if (Objects.nonNull(applicationMatch)) {
                System.out.println(job.convert2ListShowJob(showNumber++));
                String line = applicationMatch.convert2ListShowJob("Applicant match:");
                System.out.println("    " + line);
            }

        }

    }

    private void sortRelationByCreatedTime(List<UserApplicationJobRelation> relations) {
        relations.sort(new Comparator<UserApplicationJobRelation>() {
            @Override
            public int compare(UserApplicationJobRelation o1, UserApplicationJobRelation o2) {
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }
        });
    }

    private void sortJobByCreatedTime(List<Job> jobs) {
        jobs.sort(new Comparator<Job>() {
            @Override
            public int compare(Job o1, Job o2) {
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }
        });
    }

    private void hrFilterApplicants(Scanner scanner, HRAssistant hrAssistant) {
//        lastname：根据姓氏按字母顺序（从 A 到 Z）列出申请人。
//        degree：按照最高学位降序排列申请者。
//        wam：按照加权平均分从大到小排列申请人。 WAM 被计算为他们提供的所有课程分数的简单平均值。 如果没有提供分数，申请人会发现自己按照姓氏、名字的字母顺序排在列表的底部。
        List<Application> applications = hrAssistant.getApplications();
        if (applications.size() == 0) {
            System.out.println("No applications available.");
            return;
        }
        String input = "";
        while (true) {
            System.out.print("Filter by: [lastname], [degree] or [wam]:");
            input = scanner.nextLine().trim();
            if (isRightFilterCommand(input)) {
                break;
            } else {
//                文档上还没有相关提示
                System.out.println("Invalid input! Please enter a valid command to continue:");
                input = scanner.nextLine().trim();
            }
        }
        switch (input) {
            case "lastname":
                applications.sort(new Comparator<Application>() {
                    @Override
                    public int compare(Application o1, Application o2) {
                        return o1.getFirstname().compareTo(o2.getFirstname());
                    }
                });
                break;
            case "degree":
                // 筛选出 存在开始时间的人
                List<Application> existHighestDegree = new ArrayList<>();
                List<Application> noHighestDegree = new ArrayList<>();
                for (Application item : applications) {
                    if (Objects.isNull(item.getAvailability())) {
                        noHighestDegree.add(item);
                    } else {
                        existHighestDegree.add(item);
                    }
                }

                List<Application> showApplication = new ArrayList<>();
                // 排序
                if (existHighestDegree.size() > 0) {
                    existHighestDegree.sort(new Comparator<Application>() {
                        @Override
                        public int compare(Application o1, Application o2) {
                            return CommonUtils.getDegreeValue(o2.getHighestDegree()) - CommonUtils.getDegreeValue(o1.getHighestDegree());
                        }
                    });
                }
                showApplication.addAll(existHighestDegree);
                sortByLastNameAndFirstName(noHighestDegree);
                showApplication.addAll(noHighestDegree);
                applications = showApplication;
                break;
            case "wam":
                List<Application> existScore = new ArrayList<>();
                List<Application> noScore = new ArrayList<>();
                for (Application item : applications) {
                    if (Objects.isNull(item.getCOMP90007()) && Objects.isNull(item.getCOMP90041()) && Objects.isNull(item.getCOMP90038()) && Objects.isNull(item.getINFO90002())) {
                        noScore.add(item);
                    } else {
                        existScore.add(item);
                    }
                }

                List<Application> scoreShowApplication = new ArrayList<>();
                // 排序
                if (existScore.size() > 0) {
                    existScore.sort(new Comparator<Application>() {
                        @Override
                        public int compare(Application o1, Application o2) {
                            double diff = (getTotalScore(o2) * 1.0 / classTotalNumber(o2)) - (getTotalScore(o1) * 1.0 / classTotalNumber(o1));
                            if (diff < 0.0001 && diff > -0.0001) {
                                return 0;
                            } else if (diff < 0) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                }
                scoreShowApplication.addAll(existScore);
                sortByLastNameAndFirstName(noScore);
                scoreShowApplication.addAll(noScore);
                applications = scoreShowApplication;

                break;
        }
        showApplicationList(applications);
    }


    private int classTotalNumber(Application application) {
        int result = 0;
        if (application.getINFO90002() != null) {
            result++;
        }
        if (application.getCOMP90038() != null) {
            result++;
        }
        if (application.getCOMP90041() != null) {
            result++;
        }
        if (application.getCOMP90007() != null) {
            result++;
        }
        return result;
    }

    private int getTotalScore(Application application) {
        return (application.getINFO90002() == null ? 0 : application.getINFO90002()) + (application.getCOMP90038() == null ? 0 : application.getCOMP90038()) + (application.getCOMP90041() == null ? 0 : application.getCOMP90041()) + (application.getCOMP90007() == null ? 0 : application.getCOMP90007());
    }


    void sortByLastNameAndFirstName(List<Application> applications) {
        applications.sort(new Comparator<Application>() {
            @Override
            public int compare(Application o1, Application o2) {
                if (o1.getLastname().compareTo(o2.getLastname()) == 0) {
                    return o1.getFirstname().compareTo(o2.getFirstname());
                }
                return o1.getLastname().compareTo(o2.getLastname());
            }
        });
    }


    private static List<String> FILTER_COMMAND = new ArrayList<>();

    static {
        FILTER_COMMAND.add("lastname");
        FILTER_COMMAND.add("degree");
        FILTER_COMMAND.add("wam");
    }

    private boolean isRightFilterCommand(String input) {
        return FILTER_COMMAND.contains(input);
    }

    private void hrListApplicants(Scanner scanner, HRAssistant hrAssistant) {
        // 一层排序按照 最早可以开始时间的字典序进行排序
        List<Application> applications = hrAssistant.getApplications();
        if (applications.size() == 0) {
            System.out.println("No applications available.");
            return;
        }
        // 筛选出 存在开始时间的人
        List<Application> existAvailability = new ArrayList<>();
        List<Application> noAvailability = new ArrayList<>();
        for (Application item : applications) {
            if (Objects.isNull(item.getAvailability())) {
                noAvailability.add(item);
            } else {
                existAvailability.add(item);
            }
        }

        List<Application> showApplication = new ArrayList<>();
        // 排序
        if (existAvailability.size() > 0) {
            existAvailability.sort(new Comparator<Application>() {
                @Override
                public int compare(Application o1, Application o2) {
                    if (o1.getAvailability().compareTo(o2.getAvailability()) == 0) {
                        if (o1.getLastname().compareTo(o2.getLastname()) == 0) {
                            return o1.getFirstname().compareTo(o2.getFirstname());
                        }
                        return o1.getLastname().compareTo(o2.getLastname());
                    }
                    return o1.getAvailability().compareTo(o2.getAvailability());
                }
            });
        }
        showApplication.addAll(existAvailability);
        noAvailability.sort(new Comparator<Application>() {
            @Override
            public int compare(Application o1, Application o2) {
                if (o1.getLastname().compareTo(o2.getLastname()) == 0) {
                    return o1.getFirstname().compareTo(o2.getFirstname());
                }
                return o1.getLastname().compareTo(o2.getLastname());
            }
        });
        showApplication.addAll(noAvailability);
        showApplicationList(showApplication);
    }

    private void showApplicationList(List<Application> showApplication) {
        for (int number = 1; number <= showApplication.size(); number++) {
            Application applicationShow = showApplication.get(number - 1);
            System.out.println(applicationShow.convert2ListShowJob(number + ""));
        }
    }

    private void hrListAvailableJobs(Scanner scanner, HRAssistant hrAssistant) {
        List<Job> jobs = hrAssistant.getJobs();
        // 没有jobs
        if (jobs.size() == 0) {
            System.out.println("No jobs available.");
            return;
        }

        // 按创建时间排序
        sortJobByCreatedTime(jobs);
        for (int number = 1; number <= jobs.size(); number++) {
            Job job = jobs.get(number - 1);
            System.out.println(job.convert2ListShowJob(number));
            List<UserApplicationJobRelation> relations = hrAssistant.getRelationsByJobCreated(job.getCreatedAt().getEpochSecond());
            // 按申请时间排序
            sortRelationByCreatedTime(relations);
            int count = 0;
            char sequence = 'a';
            for (UserApplicationJobRelation relation : relations) {
                Application application = hrAssistant.getApplicationByCreated(relation.getApplicationCreatedAt());
                if (count % 26 == 0) {
                    sequence = 'a';
                }
                String line = application.convert2ListShowJob(sequence + (count / 26 == 0 ? "" : ((count / 26) + 1 + "")));
                System.out.println("    " + line);
                count++;
                sequence += 1;
            }
        }

    }


    private void createNewJob(Scanner scanner, HRAssistant hrAssistant) {
        Job job = new Job();
        System.out.println("# Create new Job");


        String title = "";
        System.out.print("Position Title: ");
        while (true) {
            title = scanner.nextLine().trim();
            if (title.length() == 0) {
                System.out.print("Ooops! Position Title must be provided: ");
            } else {
                break;
            }
        }
        job.setTitle(title);


        System.out.print("Position Description: ");
        String description = scanner.nextLine().trim();
        job.setDescription(description);

        System.out.print("Minimum Degree Requirement: ");
        String degreeStr = scanner.nextLine().trim();
        // 允许为空 但是不允许有错误的输入
        if (degreeStr.length() != 0) {
            Degree degree = Degree.parse(degreeStr);
            while (Objects.isNull(degree)) {
                System.out.print("Invalid input! Please specify Minimum Degree Requirement:");
                degreeStr = scanner.nextLine().trim();
                if (degreeStr.length() == 0) {
                    // 允许输入为空
                    break;
                } else {
                    degree = Degree.parse(degreeStr);
                }
            }
            job.setDegree(degree);
        }

        System.out.print("Salary ($ per annum): ");
        String salaryStr = scanner.nextLine().trim();
        if (salaryStr.length() != 0) {
            while (true) {
                if (salaryStr.length() == 0) {
                    break;
                }
                int salaryExpectations = Integer.parseInt(salaryStr);
                if (salaryExpectations <= 0) {
                    System.out.print("Invalid input! Please specify Salary ($ per annum):");
                    salaryStr = scanner.nextLine().trim();
                } else {
                    break;
                }
            }
            if (salaryStr.length() != 0) {
                job.setSalary(Integer.parseInt(salaryStr));
            }
        }


        System.out.print("Start Date: ");
        String startDateStr = scanner.nextLine().trim();
        while (true) {
            if (startDateStr.length() == 0) {
                System.out.print("Ooops! Start Date must be provided: ");
                startDateStr = scanner.nextLine().trim();
            } else {
                try {
                    DATE_TIME_FORMATTER.parse(startDateStr);
                    break;
                } catch (Exception e) {
                    System.out.print("Invalid input! Please specify Start Date: ");
                    startDateStr = scanner.nextLine().trim();
                }
            }
        }
        job.setStartDate(LocalDate.parse(startDateStr, DATE_TIME_FORMATTER));

        job.setCreatedAt(Instant.now());
        hrAssistant.addJob(job);
        FileParse.saveJobs(hrAssistant.getJobs());
    }


    private void printApplicationOperationMenu(HRAssistant hrAssistant) {
        System.out.println((hrAssistant.getAvailableJobNumber() - applicationsSubmittedCount) + " jobs available. " + applicationsSubmittedCount + " applications submitted.");
        System.out.println("Please enter one of the following commands to continue:");
        if (!hasCreatedApplication) {
            System.out.println("- create new application: [create] or [c]");
        }
        System.out.println("- list available jobs: [jobs] or [j]");
        System.out.println("- quit the program: [quit] or [q]");
        System.out.print("> ");

    }


    private void printHrOperationMenu(HRAssistant hrAssistant) {
        System.out.println(hrAssistant.getUserApplicationJobRelations().size() + " applications received.");
        System.out.println("Please enter one of the following commands to continue:");
        System.out.println("- create new job: [create] or [c]");
        System.out.println("- list available jobs: [jobs] or [j]");
        System.out.println("- list applicants: [applicants] or [a]");
        System.out.println("- filter applications: [filter] or [f]");
        System.out.println("- matchmaking: [match] or [m]");
        System.out.println("- quit the program: [quit] or [q]");
        System.out.print("> ");

    }


}
