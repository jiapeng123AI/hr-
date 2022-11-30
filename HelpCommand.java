import java.util.List;

/**
 * @author
 * @version 1.0
 * @date 2022/10/14/11:17 PM
 */
public class HelpCommand extends AbstractCommand {


    public HelpCommand(List<String> args) {
        super(args);
    }

    @Override
    public void action(HRAssistant hrAssistant) {
        System.out.println("HRAssistant - COMP90041 - Final Project");
        System.out.println();
        System.out.println("Usage: java HRAssistant [arguments]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("    -r or --role            Mandatory: determines the user's role");
        System.out.println("    -a or --applications    Optional: path to applications file");
        System.out.println("    -j or --jobs            Optional: path to jobs file");
        System.out.println("    -h or --help            Optional: print Help (this message) and exit");
    }

}
