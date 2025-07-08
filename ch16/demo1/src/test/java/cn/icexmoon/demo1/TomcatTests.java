package cn.icexmoon.demo1;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @ClassName TomcatTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 上午10:50
 * @Version 1.0
 */
public class TomcatTests {
    /**
     * 创建 Tomcat 服务器
     * @throws IOException
     * @throws LifecycleException
     */
    @Test
    public void test() throws IOException, LifecycleException {
        Tomcat tomcat = new Tomcat();
        // 创建临时目录作为 Tomcat 应用的目录
        File dir = Files.createTempDirectory("tomcat.").toFile();
        dir.deleteOnExit(); // 程序退出后自动删除
        // 添加 context，虚拟路径为根路径/，本地文件路径为临时目录
        tomcat.addContext("", dir.getAbsolutePath());
        // 启动 Tomcat
        tomcat.start();
        // 设置连接，监听 8080 端口
        Connector connector = new Connector(new Http11Nio2Protocol());
        connector.setPort(8080);
        tomcat.setConnector(connector);
        tomcat.getServer().await();
    }
}
