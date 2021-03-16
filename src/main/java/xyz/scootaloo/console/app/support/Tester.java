package xyz.scootaloo.console.app.support;

import xyz.scootaloo.console.app.common.Colorful;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 测试工具
 * @author flutterdash@qq.com
 * @since 2021/3/15 22:36
 */
public class Tester<T, R> {
    private static final Colorful color = Colorful.INSTANCE;
    private final Function<T, R> function;
    private final boolean exitOnException;
    private final List<Sample<T, R>> samples = new ArrayList<>();

    private Matcher<R> DEFAULT_MATCHER = Object::equals;

    /**
     * 创建一个方法测试对象
     * @param function 被测试的方法
     * @param <T> 方法的输入 方法参数
     * @param <R> 方法的输出 方法返回值
     * @return 测试对象
     */
    public static <T, R> Tester<T, R> createTest(Function<T, R> function) {
        return createTest(function, false);
    }

    public static <T, R> Tester<T, R> createTest(Function<T, R> function, boolean exitOnException) {
        return new Tester<>(function, exitOnException);
    }

    private Tester(Function<T, R> function, boolean exitOnException) {
        this.function = function;
        this.exitOnException = exitOnException;
    }

    /**
     * 添加一个测试用例
     * @param input 输入
     * @param output 预期输出
     * @return 构建者
     */
    public Tester<T, R> addCase(T input, R output) {
        samples.add(new Sample<>(input, output));
        return this;
    }

    /**
     * 设置一个匹配器
     * @param usrMatcher 用户自定义的匹配器
     * @return 构建者
     */
    public Tester<T, R> setMatcher(Matcher<R> usrMatcher) {
        DEFAULT_MATCHER = usrMatcher;
        return this;
    }

    /**
     * 执行测试
     * 测试结果，如果通过则绿色显示
     *         如果失败则红色显示
     */
    public void test() {
        samples.forEach(sample ->
                sample.matcher = DEFAULT_MATCHER);
        samples.forEach(sample ->
                sample.matchAndShow(function, exitOnException));
    }

    private static class Sample<In, Out> {

        In input;
        Out output;
        Matcher<Out> matcher;

        private Sample(In input, Out output) {
            this.input = input;
            this.output = output;
        }

        private void matchAndShow(Function<In, Out> function, boolean exitOnException) {
            try {
              Out actualOut = function.apply(input);
              if (matcher.match(output, actualOut)) {
                  System.out.println(color.green("PASS Input: " + input + "\n     Output: " + output));
              } else {
                  System.out.println(color.red("FAIL Input: " + input + "\n     Output: " + output
                          + "\n     Actual: " + actualOut));
              }
            } catch (Exception e) {
                e.printStackTrace();
                if (exitOnException)
                    System.exit(0);
            }
        }

    }

    @FunctionalInterface
    public interface Matcher<T> {

        /**
         * 自定义的匹配器
         * @param sample 预期值
         * @param actual 实际值
         * @return 判断实际值是否符合预期，假如符合条件返回true，不符合返回false
         */
        boolean match(T sample, T actual);

    }

}
