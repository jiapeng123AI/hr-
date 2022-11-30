import java.util.*;


/**
 * COMP90041, Sem2, 2022: Final Project
 *
 * @author:
 */
public class HRAssistant {

    private List<Job> jobs = new ArrayList<>();

    private List<Application> applications = new ArrayList<>();

    private List<UserApplicationJobRelation> userApplicationJobRelations;

    /**
     * 获取可用的工作数量
     *
     * @return 工作数量
     */
    public int getAvailableJobNumber() {
        return jobs.size();
    }


    public static void main(String[] args) {

        HRAssistant hrAssistant = new HRAssistant();

        // 命令操作
        List<Command> commands = CommandFactory.buildCommandList(args);
        if (commands.size() == 0) {
            // 没有参数输入给出系统提示
            System.out.println("No parameter input");
            System.exit(1);
        }
        // 依次执行输入的命令(优先执行其他命令 最后执行用户登陆的命令 因为用户的行为建立在系统将其他资源加载好之后)
        Command roleCommand = null;
        boolean containLoadJobCommand = false;
        boolean containLoadApplicationCommand = false;
        try {
            for (Command command : commands) {
                if (command instanceof LoadJobsCommand) {
                    containLoadJobCommand = true;
                }
                if (command instanceof RoleCommand) {
                    roleCommand = command;
                } else {
                    command.action(hrAssistant);
                }
            }

            // 如果不存在倒入Job的命令则从默认的目录中加载Job
            if (!containLoadJobCommand) {
                new LoadJobsCommand(Collections.singletonList("jobs.csv")).action(hrAssistant);
            }

            if (!containLoadApplicationCommand) {
                new LoadApplicationsCommand(Collections.singletonList("applications.csv")).action(hrAssistant);
            }

            // 加载所有的申请与工作的关联关系
            hrAssistant.setUserApplicationJobRelations(FileParse.loadAllUserApplicationJobRelation());

            // 用户登陆的命令最后执行
            if (Objects.nonNull(roleCommand)) {
                roleCommand.action(hrAssistant);
            }
        }catch (Exception e){
            // 当有命令执行错误的时候就进行提示
            new HelpCommand(new ArrayList<>()).action(hrAssistant);
         }


    }


    public void addApplication(Application application) {
        this.applications.add(application);
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public void addJob(Job job) {
        this.jobs.add(job);
    }

    public List<UserApplicationJobRelation> getUserApplicationJobRelations() {
        return userApplicationJobRelations;
    }

    public void setUserApplicationJobRelations(List<UserApplicationJobRelation> userApplicationJobRelations) {
        this.userApplicationJobRelations = userApplicationJobRelations;
    }

    public Job getJobByCreated(Long created) {
        for (Job job : jobs) {
            if (job.getCreatedAt().getEpochSecond() == created.longValue()) {
                return job;
            }
        }
        return null;
    }

    public Application getApplicationByCreated(Long created) {
        for (Application application : applications) {
            if (application.getCreatedAt().getEpochSecond() == created.longValue()) {
                return application;
            }
        }
        return null;
    }

    public List<UserApplicationJobRelation> getRelationsByJobCreated(long epochSecond) {
        List<UserApplicationJobRelation> result = new ArrayList<>();
        for (UserApplicationJobRelation userApplicationJobRelation : getUserApplicationJobRelations()) {
            if (userApplicationJobRelation.getJobCreatedAt().equals(epochSecond)) {
                result.add(userApplicationJobRelation);
            }
        }
        return result;
    }
}
