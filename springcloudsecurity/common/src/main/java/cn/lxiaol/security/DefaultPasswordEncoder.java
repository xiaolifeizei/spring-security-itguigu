package cn.lxiaol.security;

import cn.lxiaol.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码处理工具类
 *
 * @author lixiaolong
 * @date 2021/1/22 20:48
 */
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    public DefaultPasswordEncoder() {
        this(-1);
    }

    public DefaultPasswordEncoder(int strength) {
    }


    /**
     * 进行MD5加密
     *
     * @author lixiaolong
     * @date 2021/1/22 20:48
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return MD5.encrypt(rawPassword.toString());
    }

    /**
     * 进行密码比对
     *
     * @author lixiaolong
     * @date 2021/1/22 20:48
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }

}
