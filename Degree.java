/**
 * 学历
 *
 * @author
 * @version 1.0
 * @date 2022/10/14/7:10 PM
 */
public enum Degree {


    Bachelor, Master, PHD;

    public static Degree parse(String degreeStr){
        for (Degree value : Degree.values()) {
            if(value.name().equals(degreeStr)){
                return value;
            }
        }

        return null;
    }
}
