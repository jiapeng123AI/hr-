/**
 * @author
 * @version 1.0
 * @date 2022/10/15/3:05 PM
 */
public class UserApplicationJobRelation {

    /**
     * 创建时间
     */
    private Long createdAt;
    /**
     * 申请创建的时间(用于寻找简历)
     */
    private Long applicationCreatedAt;

    /**
     * job创建时间(用于寻找Job)
     */
    private Long jobCreatedAt;


    public static UserApplicationJobRelation parseUserApplicationJobRelation(String line) {
        String[] split = line.split(",");
        UserApplicationJobRelation result = new UserApplicationJobRelation();
        result.setCreatedAt(Long.parseLong(split[0]));
        result.setApplicationCreatedAt(Long.parseLong(split[1]));
        result.setJobCreatedAt(Long.parseLong(split[2]));
        return result;
    }

    public String convert2CsvFileStyle() {
        return createdAt+","+applicationCreatedAt+","+jobCreatedAt;
    }


    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getApplicationCreatedAt() {
        return applicationCreatedAt;
    }

    public void setApplicationCreatedAt(Long applicationCreatedAt) {
        this.applicationCreatedAt = applicationCreatedAt;
    }

    public Long getJobCreatedAt() {
        return jobCreatedAt;
    }

    public void setJobCreatedAt(Long jobCreatedAt) {
        this.jobCreatedAt = jobCreatedAt;
    }
}
