import java.util.List;

/**
 * @author
 * @version 1.0
 * @date 2022/10/14/11:49 PM
 */
public abstract class AbstractCommand implements Command {

    /**
     * 用户输入的命令参数
     */
    protected List<String> args;

    public AbstractCommand(List<String> args) {
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}
