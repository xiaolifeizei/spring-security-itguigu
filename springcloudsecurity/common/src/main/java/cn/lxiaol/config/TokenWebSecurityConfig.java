package cn.lxiaol.config;

import cn.lxiaol.filter.TokenAuthFilter;
import cn.lxiaol.filter.TokenLoginFilter;
import cn.lxiaol.security.DefaultPasswordEncoder;
import cn.lxiaol.security.TokenLogoutHandler;
import cn.lxiaol.security.TokenManager;
import cn.lxiaol.security.UnauthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;
    private DefaultPasswordEncoder defaultPasswordEncoder;
    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;

    @Autowired
    public TokenWebSecurityConfig(UserDetailsService userDetailsService,
                                  DefaultPasswordEncoder defaultPasswordEncoder,
                                  TokenManager tokenManager,
                                  RedisTemplate redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 配置设置
     *
     * @param http
     * @throws Exception
     */
    //设置退出的地址和token，redis操作地址
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                //没有权限访问时调用自定义的处理类
                .authenticationEntryPoint(new UnauthEntryPoint())
                .and().csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                //退出路径
                .and()
                .logout().logoutUrl("/admin/acl/index/logout")
                // 调用退出时的处理器
                .addLogoutHandler(new TokenLogoutHandler(tokenManager, redisTemplate))
                .and()
                // 认证过滤器
                .addFilter(new TokenLoginFilter(authenticationManager(), tokenManager, redisTemplate))
                // 授权过滤器
                .addFilter(new TokenAuthFilter(authenticationManager(), tokenManager, redisTemplate))
                .httpBasic();
    }

    /**
     * 调用userDetailsService和密码处理
     *
     * @param auth
     * @throws Exception
     * @author lixiaolong
     * @date 2021/1/23 13:36
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    /**
     * 不进行认证的路径，可以直接访问
     *
     * @param web
     * @throws Exception
     * @author lixiaolong
     * @date 2021/1/23 13:36
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/**");
    }
}
