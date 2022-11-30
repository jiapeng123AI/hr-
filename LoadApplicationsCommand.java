import java.util.List;

/**
 * @author
 * @version 1.0
 * @date 2022/10/14/11:53 PM
 */
public class LoadApplicationsCommand extends AbstractCommand {

    public LoadApplicationsCommand(List<String> args) {
        super(args);
    }

    @Override
    public void action(HRAssistant hrAssistant) {
        // 从文件中将工作加载进来
        String jobFilePath = args.get(0);
        List<Application> applications = FileParse.parsingTheApplicationsFile(jobFilePath);
        hrAssistant.setApplications(applications);
    }


}
