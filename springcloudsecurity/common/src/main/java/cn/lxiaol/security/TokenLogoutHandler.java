package cn.lxiaol.security;

import cn.lxiaol.utils.R;
import cn.lxiaol.utils.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 退出处理器 退出时移除token 并删除redis中的token信息
 *
 * @author lixiaolong
 * @date 2021/1/22 21:18
 */
public class TokenLogoutHandler implements LogoutHandler {
    private TokenManager tokenManager;

    private RedisTemplate redisTemplate;

    public TokenLogoutHandler(TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //1 从header里面获取token
        //2 token不为空，移除token，从redis删除token
        String token = request.getHeader("token");
        if (token != null) {
            //移除
            tokenManager.removeToken(token);
            //从token获取用户名
            String username = tokenManager.getUserInfoFromToken(token);
            redisTemplate.delete(username);
        }
        ResponseUtil.out(response, R.ok());
    }
}
