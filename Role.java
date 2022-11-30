/**
 * 系统中存在的三种角色
 * @author
 * @version 1.0
 * @date 2022/10/14/11:22 PM
 */
public enum Role {

    hr,

    applicant,

    audit,

    ;

    public static Role parse(String roleStr){
        for (Role value : Role.values()) {
            if(value.name().equals(roleStr)){
                return value;
            }
        }

        return null;
    }

}
