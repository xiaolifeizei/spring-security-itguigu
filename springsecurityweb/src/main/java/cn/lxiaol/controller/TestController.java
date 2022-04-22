package cn.lxiaol.controller;

import cn.lxiaol.entity.Users;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lixiaolong
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("hello")
    public String hello() {
        return "hello hello";
    }

    @GetMapping("index")
    public String index() {
        return "hello index";
    }

    /**
     * 验证角色 满足一个则放行
     *
     * @author lixiaolong
     * @date 2021/1/21 21:58
     */
    @GetMapping("Secured")
    @Secured({"ROLE_sale", "ROLE_manager"})
    public String Secured() {
        System.out.println("secured......");
        return "hello Secured";
    }


    @GetMapping("PreAuthorize")
    @PreAuthorize("hasAnyAuthority('admin,system')")
    public String PreAuthorize() {
        System.out.println("secured......");
        return "hello PreAuthorize";
    }

    @GetMapping("PostAuthorize")
    @PostAuthorize("hasAnyAuthority('admin')")
    public String PostAuthorize() {
        System.out.println("secured......");
        return "hello PostAuthorize";
    }


    @GetMapping("PostFilter")
    @PostAuthorize("hasAnyAuthority('admin')")
    @PostFilter("filterObject.username == 'admin1'")
    public List<Users> PostFilter() {
        ArrayList<Users> list = new ArrayList<>();
        list.add(new Users(11, "admin1", "6666"));
        list.add(new Users(21, "admin2", "888"));
        System.out.println(list);
        return list;
    }

    @RequestMapping("PreFilter")
    @PreAuthorize("hasRole('ROLE_manager')")
    @PreFilter(value = "filterObject.id%2==0")
    public Users PreFilter(@RequestBody Users t) {
        System.out.println(t.getId() + "\t" + t.getUsername());
        return t;
    }
}