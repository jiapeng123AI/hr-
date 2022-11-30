/**
 * 性别的枚举
 *
 * @author
 * @version 1.0
 * @date 2022/10/14/7:08 PM
 */
public enum Gender {

    female, male, other;

    public static Gender parse(String genderStr) {
        for (Gender value : Gender.values()) {
            if (value.name().equals(genderStr)) {
                return value;
            }
        }

        return null;
    }
}
