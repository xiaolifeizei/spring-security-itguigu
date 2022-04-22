package cn.lxiaol.serviceacl.service.impl;

import cn.lxiaol.entity.CurrentUserInfo;
import cn.lxiaol.entity.SecurityUser;
import cn.lxiaol.serviceacl.entity.User;
import cn.lxiaol.serviceacl.service.PermissionService;
import cn.lxiaol.serviceacl.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lixiaolong
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    /**
     * 根据用户名 获取用户权限
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     * @author lixiaolong
     * @date 2021/1/23 14:32
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询数据
        User user = userService.selectByUsername(username);
        //判断
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        CurrentUserInfo curUser = new CurrentUserInfo();
        BeanUtils.copyProperties(user, curUser);

        //根据用户查询用户权限列表
        List<String> permissionValueList = permissionService.selectPermissionValueByUserId(user.getId());

        SecurityUser securityUser = new SecurityUser();
        securityUser.setCurrentUserInfo(curUser);
        securityUser.setPermissionValueList(permissionValueList);
        return securityUser;
    }
}
