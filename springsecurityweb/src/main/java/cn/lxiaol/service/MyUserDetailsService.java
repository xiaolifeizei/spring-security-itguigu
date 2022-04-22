package cn.lxiaol.service;

import cn.lxiaol.entity.Users;
import cn.lxiaol.mapper.UsersMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lixiaolong
 * @Service("userDetailsService") 必须是这个名
 */
@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UsersMapper usersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //调用usersMapper方法，根据用户名查询数据库
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Users users = usersMapper.selectOne(wrapper);
        //数据库没有用户名，认证失败
        if (users == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        System.out.println(users);
        List<GrantedAuthority> auths =
                AuthorityUtils.createAuthorityList("admin", "system", "ROLE_manager");
        return new User(users.getUsername(), passwordEncoder.encode(users.getPassword()), auths);
    }
}