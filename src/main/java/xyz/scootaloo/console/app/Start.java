package xyz.scootaloo.console.app;

import org.testng.annotations.Test;
import xyz.scootaloo.console.app.support.application.ApplicationRunner;
import xyz.scootaloo.console.app.support.common.Commons;
import xyz.scootaloo.console.app.support.component.Boot;
import xyz.scootaloo.console.app.support.parser.Interpreter;
import xyz.scootaloo.console.app.support.parser.InvokeInfo;
import xyz.scootaloo.console.app.support.parser.SimpleParameterParser;
import xyz.scootaloo.console.app.workspace.*;
import xyz.scootaloo.console.app.workspace.QuickStart.Student;

/**
 * 控制台开发框架
 * 简化开发过程，自动装配命令，解析命令参数
 * 支持可选参数和必选参数，自动解析表单类
 *
 * 快速学习如何使用请移步到:
 * {@link QuickStart}
 *
 * @author flutterdash@qq.com
 * @since 2020/12/27 14:58
 */
@Boot
public class Start {

    /**
     * 启动一个控制台应用
     * 1. 使用Commons.config()进行配置
     * 2. 在workspace目录下进行开发。
     * 3. 回到此类运行此 main 方法，系统启动。
     */
    public static void main(String[] args) {
        ApplicationRunner.consoleApplication(
                Commons.config()
                        // 应用信息
                        .appName("测试应用示例")   // 应用的名称
                        .printWelcome(false)    // 是否打印欢迎信息
                        .prompt("example> ")    // 控制台输入的提示符
                        .printStackTrace(false) // 遇到异常时是否打印调用栈
                        .exitCmd(new String[] {"exit", "e.", "q"}) // 使用这些命令可以退出应用
                        .maxHistory(128) // 最多保存的历史记录数量
                        .enableVariableFunction(true) // 开启变量功能，get set命令可用，占位符功能可用
                        // 编辑作者信息，当printWelcome设置为false时，这些信息不会被输出
                        .editAuthorInfo()
                            .authorName("fd")
                            .email("~~")
                            .comment("备注: ~~")
                            .createDate("2020/12/27")
                            .updateDate("2021/1/18")
                            .ok()
                        // 设置系统启动时执行的命令
                        .addInitCommands()
                            .getFromFile("init.txt") // 从文件中读取
                            .add("find --tag usr")   // 查询所有的用户命令
                            .add("help --name help") // 获取 help 命令的使用帮助
                            .ok()
                        // 增加命令工厂，enable参数决定是否启用该命令工厂，将false修改为true可以开启对应命令工厂的测试，
                        // 但是为了方便功能演示，建议测试以下几个类的时候，每次只有一个工厂类enable为true
                        .addCommandFactories()
                            .add(QuickStart.class, true) // 使用Class对象，可以实例化private的无参构造器，但是可能会导致系统中存在多个实例
                            .add(AdvancedDemo::new, true) // 构造器引用，同样存在导致系统中多例的问题
                            .add(ListenerDemo.INSTANCE, false) // 使用已存在的对象做为命令工厂，单例
                            .add(LoginDemo.class, false)
                            .ok()
                        // 添加自定义的解析器实现
                        .addParameterParser()
                            .addParser("raw", SimpleParameterParser.INSTANCE) // 现在可以用"raw"这个解析器了
                            .ok()
                        .addHelpFactory(HelpForDemo.INSTANCE) // 加入命令帮助
                        // 设置完成，应用启动
                        .build()).run();
    }

    /**
     * 仅获取一个解释器，而不是从控制台上获取键盘输入
     * 动态地执行某类的方法，可以直接得到结果(返回的是包装类，包装类含有方法执行的信息)
     */
    @Test
    public void testInterpreter() {
        // 使用 Commons.simpleConf() 获取更精简的配置类
        Interpreter interpreter = ApplicationRunner.getInterpreter(
                Commons.simpleConf()
                    .printStackTrace(false)
                    .addFactory()
                        .add(QuickStart::new, true)
                        .ok()
                    .build());

        // 直接运行命令，得到结果的包装类
        InvokeInfo result1 = interpreter.interpret("add 11 12");
        System.out.println("执行 'add 11 12' 的结果: " + result1.get());

        // 使用参数运行，这里的args等于方法参数，也就是说这里可以看成是调用 add(11, 12)
        InvokeInfo result2 = interpreter.invoke("add", 11, 12);
        System.out.println("使用参数执行，结果: " + result2.get());

        // 解释器调用参数含有对象的方法时，字符串命令中的占位符会触发等待键盘输入，如
//        InvokeInfo result3 = interpreter.interpret("stuAdd #"); // 在 main 方法中调用可以观察到

        // result3的方式调用参数中含有对象的方法，某些场景下可能会引起线程阻塞，可以使用 invoke 方法传入对象调用
        // 或者实现自定义的类型转换器，参考 AdvancedDemo.resolveByte(Str) 方法
        InvokeInfo result4 = interpreter.invoke("addStu", new Student());
        System.out.println("result4: " + result4.get());

        // 在解释器中使用变量占位符
        InvokeInfo result5 = interpreter.interpret("echo -v ${rand.int(10,15)}");
        System.out.println("\"echo -v ${rand.int(10,15)}\"的结果是: " + result5.get());

        System.out.println("\n----------------------------------------------------\n");

        // 变量功能在解释器中的使用
        // 将这个随机整型做为 "randNumber" 这个键的值
        boolean flag = interpreter.set("randNumber", "echo 使用echo时两边的内容${rand.int(10,15)}都被忽略了");
        // 检查设置情况
        if (flag)
            System.out.println("设置成功");
        // 现在可以获取到这个值了，使用get (不需要占位符) 或者 echo (需要占位符)
        InvokeInfo result6 = interpreter.interpret("get randNumber"); // else: echo ${randNumber}
        System.out.println("randNumber is " + result6.get());
    }

}
