/**
 * 统计数据
 *
 * @author
 * @version 1.0
 * @date 2022/10/15/10:00 PM
 */
public class StatisticData {

    /**
     * 统计数据的名称
     */
    private String name;

    /**
     * 统计数据的计算得分
     */
    private double score;

    public StatisticData(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
