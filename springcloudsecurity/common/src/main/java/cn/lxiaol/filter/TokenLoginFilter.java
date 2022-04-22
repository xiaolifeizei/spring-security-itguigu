package cn.lxiaol.filter;

import cn.lxiaol.entity.CurrentUserInfo;
import cn.lxiaol.entity.SecurityUser;
import cn.lxiaol.security.TokenManager;
import cn.lxiaol.utils.R;
import cn.lxiaol.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 认证过滤器 生成token 放到redis中
 *
 * @author lixiaolong
 * @date 2021/1/22 21:30
 */
@SuppressWarnings("unchecked")
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * 权限
     */
    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;

    public TokenLoginFilter(AuthenticationManager authenticationManager, TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
        this.setPostOnly(false);
        // 设置登陆路径，并且post请求
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/acl/login", "POST"));
    }

    /**
     * 获取表单提交的相关信息
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @author lixiaolong
     * @date 2021/1/23 9:50
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        // 获取表单提交数据
        try {
            CurrentUserInfo user = new ObjectMapper().readValue(request.getInputStream(), CurrentUserInfo.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(), user.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    /**
     * Default behaviour for successful authentication.
     * 认证成功调用的方法 生成token 存入到redis
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult the object returned from the <tt>attemptAuthentication</tt>
     *                   method.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // 认证成功之后，得到认证成功后的用户信息
        SecurityUser user = (SecurityUser) authResult.getPrincipal();

        // 根据用户名生成token
        String token = tokenManager.createToken(user.getUsername());

        // 把用户名和用户权限放入redis中
        redisTemplate.opsForValue().set(user.getCurrentUserInfo().getUsername(), user.getPermissionValueList());

        ResponseUtil.out(response, R.ok().data("token", token));
    }

    /**
     * Default behaviour for unsuccessful authentication.
     * 认证失败调用的方法
     *
     * @param request
     * @param response
     * @param failed
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        ResponseUtil.out(response, R.error());
    }
}