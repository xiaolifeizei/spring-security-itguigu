package cn.lxiaol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author lixiaolong
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;
    /**
     * 注入数据源
     */
    @Resource
    private DataSource dataSource;

    /**
     * 实例化操作数据库的对象Bean
     *
     * @author lixiaolong
     * @date 2021/1/22 14:54
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        // 密码加密
        return new BCryptPasswordEncoder();
    }

    /**
     * 验证账号密码
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //指定service
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * 自定义系统登陆页面
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        formLogin() 自定义登陆页面
        loginPage("/login.html") 自定义登陆页面的路径
        loginProcessingUrl("/user/login") 点击登陆请求的地址
        defaultSuccessUrl("/test/index") 登陆成功之后，默认跳转地址
        permitAll() 无条件允许访问但是不校验权限
        authorizeRequests() 哪些不需要认证
        anyRequest().authenticated() 任何请求都必须经过身份验证
        csrf().disable() 关闭csrf防护,也就是关闭跨域保护
        .antMatchers("/test/index").hasAuthority("admin,manager") 用户拥有指定的权限时才可以访问/test/index 路径 全匹配
        .antMatchers("/test/index").hasAnyAuthority("admin,manager") 满足一个就行 包含
        .antMatchers("/test/index").hasRole("sale") 角色全匹配才可以访问该路径 注意 ROLE_
         */
        http.formLogin().loginPage("/login.html")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/success.html").permitAll()
                .and()
                // 哪些路径不需要登陆
                .authorizeRequests()
                .antMatchers("/", "/test/hello", "/user/login").permitAll()
                //权限全匹配
//                .antMatchers("/test/index").hasAuthority("admin")
                //权限满足一个就行
//                .antMatchers("/test/index").hasAnyAuthority("admin,manager")
                //角色全匹配
//                .antMatchers("/test/index").hasRole("sale")
                //角色满足一个就行
                .antMatchers("/test/index").hasAnyRole("sale,manager")
                .anyRequest().authenticated()
                .and()
                // 设置 过期时间 用于记住我 自动登陆
                .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60)
                .userDetailsService(userDetailsService)
                .and()
                .csrf().disable();

        /*
         自定义无访问权限跳转的页面
         */
        http.exceptionHandling().accessDeniedPage("/unauth.html");

        /*
        logoutUrl("/logout") 退出路径
        logoutSuccessUrl("/test/hello") 退出成功后跳转到哪个路径
         */
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/test/hello").permitAll();
    }


}
