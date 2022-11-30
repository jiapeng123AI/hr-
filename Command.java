
/**
 * 命令接口(解析控制传入参数 作出相应动作)
 *
 * @author
 * @version 1.0
 * @date 2022/10/14/11:14 PM
 */
public interface Command {

    /**
     * 执行命令动作
     */
    void action(HRAssistant hrAssistant);


}
