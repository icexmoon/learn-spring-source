package org.springframework.boot;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * @ClassName BannerTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/7 下午4:58
 * @Version 1.0
 */
public class BannerTests {
    @Test
    public void test() {
        SpringApplicationBannerPrinter printer = new SpringApplicationBannerPrinter(
                new DefaultResourceLoader(), new SpringBootBanner());
        ApplicationEnvironment environment = new ApplicationEnvironment();
        printer.print(environment, BannerTests.class, System.out);
    }

    @Test
    public void test2() {
        SpringApplicationBannerPrinter printer = new SpringApplicationBannerPrinter(
                new DefaultResourceLoader(), new SpringBootBanner());
        ApplicationEnvironment environment = new ApplicationEnvironment();
        environment.getPropertySources().addLast(new MapPropertySource("custom", Map.of("spring.banner.location", "banner1.txt")));
        printer.print(environment, BannerTests.class, System.out);
        String version = SpringBootVersion.getVersion();
        System.out.println(version);
    }
}
