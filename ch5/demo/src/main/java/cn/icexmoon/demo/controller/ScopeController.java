package cn.icexmoon.demo.controller;

import cn.icexmoon.demo.component.ApplicationBean;
import cn.icexmoon.demo.component.RequestBean;
import cn.icexmoon.demo.component.SessionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName ScopeController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午2:56
 * @Version 1.0
 */
@Controller
@RequestMapping("/scope")
public class ScopeController {
    @Lazy
    @Autowired
    private RequestBean requestBean;
    @Lazy
    @Autowired
    private SessionBean sessionBean;
    @Lazy
    @Autowired
    private ApplicationBean applicationBean;

    @GetMapping
    @ResponseBody
    public String index() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<ul>");
        stringBuilder.append("<li>%s</li>".formatted(requestBean));
        stringBuilder.append("<li>%s</li>".formatted(sessionBean));
        stringBuilder.append("<li>%s</li>".formatted(applicationBean));
        stringBuilder.append("</ul>");
        return stringBuilder.toString();
    }
}
