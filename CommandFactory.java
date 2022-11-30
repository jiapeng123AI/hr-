import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 命令工厂:将用户输入的命令参数转化为实际的命令 通过命令去做相关操作
 *
 * @author
 * @version 1.0
 * @date 2022/10/14/11:11 PM
 */
public class CommandFactory {

    private static final String[] COMMAND_ARRAY = {"-r", "--role", "-a", "--applications", "-j", "--jobs", "-h", "--help"};

    public static List<Command> buildCommandList(String[] args) {
        List<Command> result = new ArrayList<>();
        // 清洗命令参数
        List<String> cleanArgs = new ArrayList<>();
        for (String arg : args) {
            if (Objects.nonNull(arg)) {
                arg = arg.trim();
                if (arg.length() != 0) {
                    cleanArgs.add(arg);
                }
            }
        }

        for (int i = 0; i < cleanArgs.size(); i++) {
            String commandStr = cleanArgs.get(i);
            if (!isCommand(commandStr)) {
                throw new IllegalArgumentException("input is not a valid command");
            }
            List<String> argList = new ArrayList<>();
            if (!isNoArgsCommand(commandStr)) {
                // 应该携带参数的命令后面却没有参数则不添加参数
                if (i != cleanArgs.size() - 1 && !isCommand(cleanArgs.get(i + 1))) {
                    argList.add(cleanArgs.get(i + 1));
                    i++;
                }
            }
            Command command = buildCommand(commandStr, argList);
            result.add(command);
        }
        return result;
    }

    /**
     * 根据命令和参数创建对应的命令对象
     *
     * @param commandStr 命令字符串
     * @param argList    命令参数
     * @return 命令对象
     */
    private static Command buildCommand(String commandStr, List<String> argList) {
        switch (commandStr) {
            case "-h":
            case "--help":
                return new HelpCommand(argList);
            case "-r":
            case "--role":
                return new RoleCommand(argList);
            case "-a":
            case "--applications":
                return new LoadApplicationsCommand(argList);
            case "-j":
            case "--jobs":
                return new LoadJobsCommand(argList);
            default:
                // 抛异常 用户输入的启动命令异常
                throw new IllegalArgumentException("Startup command entered by an illegal user");
        }
    }


    /**
     * 判断一个字符串是否为命令
     *
     * @param command 命令字符串
     * @return 是否为命令
     */
    private static boolean isCommand(String command) {
        return Arrays.asList(COMMAND_ARRAY).contains(command);
    }


    /**
     * 判断是否为无参数命令
     *
     * @return 判断是否为无参数命令
     */
    private static boolean isNoArgsCommand(String command) {
        return command.equals("-h") || command.equals("--help");
    }
}
