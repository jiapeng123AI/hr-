import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 * This class contains the logic for your matchmaking algorithm
 */
public class Matchmaker {

    public Matchmaker() {

    }

    public static Application matchJob(Job job, List<Application> applications) {
        if (applications.size() == 0) {
            return null;
        }
        List<Application> applicationsCouldApply = new ArrayList<>();
        for (Application application : applications) {
            // 只有评分大于0的才可能被选择
            if (getUserJobScore(job, application) > 0) {
                applicationsCouldApply.add(application);
            }
        }

        if (applications.size() == 0) {
            return null;
        }
        applicationsCouldApply.sort(new Comparator<Application>() {
            @Override
            public int compare(Application o1, Application o2) {
                if (getUserJobScore(job, o2) == getUserJobScore(job, o1)) {
                    return o1.getLastname().compareTo(o2.getLastname());
                }
                return getUserJobScore(job, o2) - getUserJobScore(job, o1);
            }
        });

        return applications.get(0);
    }


    /**
     * 使用打分机制进行处理得到用户对于该工作的匹配程度
     * 通过4个纬度来进行打分
     *
     * @param job         工作
     * @param application 简历申请
     * @return 多少分
     */
    private static int getUserJobScore(Job job, Application application) {
        int score = 0;

        // 用户的职业生涯中参加过这类的工作加2分
        if (Objects.nonNull(application.getCareerSummary())) {
            if (application.getCareerSummary().contains(job.getTitle())) {
                score += 20;
            }
        }

        // 用户的学历评分
        if (Objects.nonNull(application.getHighestDegree()) && Objects.nonNull(job.getDegree())) {
            int degreeValue = CommonUtils.getDegreeValue(application.getHighestDegree());
            int jobDegreeValue = CommonUtils.getDegreeValue(job.getDegree());
            if (degreeValue < jobDegreeValue) {
                score -= 5;
            } else {
                score += (degreeValue - jobDegreeValue + 1) * 10;
            }
        }

        // 用户的期望薪资 在 job的工资范围内(相差不太大)
        if (Objects.nonNull(application.getSalrayExpectations()) && Objects.nonNull(job.getSalary())) {
            if (application.getSalrayExpectations() <= job.getSalary()) {
                score += 20;
            } else if (application.getSalrayExpectations() - job.getSalary() < 2000) {
                score += 5;
            } else {
                score -= 5;
            }
        }

        //  用户的可用时间 比 job要求开始时间早
        if (Objects.nonNull(application.getAvailability()) && Objects.nonNull(job.getStartDate())) {
            if (application.getAvailability().isBefore(job.getStartDate()) || application.getAvailability().equals(job.getStartDate())) {
                score += 10;
            } else {
                score -= 5;
            }
        }


        return score;
    }
}