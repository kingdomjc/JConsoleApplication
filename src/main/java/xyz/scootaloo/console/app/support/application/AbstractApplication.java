package xyz.scootaloo.console.app.support.application;

import java.util.List;
import java.util.Locale;

/**
 * @author flutterdash@qq.com
 * @since 2020/12/27 21:54
 */
public abstract class AbstractApplication {

    protected abstract String getInput();

    protected abstract void printPrompt();

    protected abstract boolean isExitCmd(String cmdName);

    public void run() {
        welcome();
        while (true) {
            try {
                printPrompt();
                if (simpleRunCommand(getInput()))
                    break;
            } catch (Exception e) {
                exceptionHandle(e);
            }
        }
        whenExit();
    }

    protected void welcome() {}

    protected void whenExit() {}

    protected void exceptionHandle(Exception e) {
        e.printStackTrace();
    }

    abstract boolean simpleRunCommand(String command) throws Exception;

    public String getCmdName(List<String> items) {
        if (items.isEmpty())
            return "";
        String cmdName = items.remove(0).trim();
        return cmdName.toLowerCase(Locale.ROOT);
    }

}
