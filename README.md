# JConsoleApplication
> 基于Java8的控制台应用开发框架，简化控制台应用的开发过程。
>
> 框架的核心功能是根据字符串格式的命令执行对应的java方法，命令名称即是方法名称，命令参数即是方法参数。
>
> 以此为基础扩展出其他实用的功能，例如应用事件监听器，命令执行条件、自定义类型解析，可选参数和必选参数等。
>
> 另外框架提供了一个快速开始应用的静态工厂方法，进行少量的配置就能启动应用。

- 注解式开发，使用通过标记注解实现对应的功能



## 相关技术

`Java8`、`maven`、注解、反射

几乎不使用第三方库，所以框架体量非常小，所有功能都由代码实现，仅有的两个依赖其中一个是单元测试，只在演示应用启动时使用，另外一个是`lombok`插件，后续可能会移除这个依赖。



## 迭代方向

这个项目是做为控制台应用开发框架而设计的，目的是为了简化控制台应用的开发过程，而控制台应用在测试已有的代码时用的比较多，按照某种规则调用已有的方法，观察控制台输出的内容，最好还能根据键盘输入进行交互，既然如此框架需要做的不应该是让使用变得复杂，而且