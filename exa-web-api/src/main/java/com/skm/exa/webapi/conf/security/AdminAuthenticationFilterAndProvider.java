package com.skm.exa.webapi.conf.security;

import com.skm.exa.common.object.UnifyAdmin;
import com.skm.exa.common.object.UnifyAuthority;
import com.skm.exa.common.object.UnifyRole;
import com.skm.exa.common.service.UnifyAdminService;
import com.skm.exa.common.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dhc
 * 2019-03-05 16:19
 */
public class AdminAuthenticationFilterAndProvider extends AbstractAuthenticationProcessingFilter implements AuthenticationProvider {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private UnifyAdminService unifyAdminService;

    private UnifyAdmin unifyAdmin;

    AdminAuthenticationFilterAndProvider(UnifyAdminService unifyAdminService, String loginUrl) {
        super(new AntPathRequestMatcher(loginUrl, HttpMethod.POST.name()));
        this.unifyAdminService = unifyAdminService;
    }

    // 获取用户输入的登录信息
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        LoginInfo info = ServletUtils.getRequestBody(request, LoginInfo.class);
        if (info == null) {
            throw new AuthenticationServiceException("Username and password are required!");
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(info.getUsername(), info.getPassword());
        token.setDetails(super.authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(token);
    }

    // 是否支持的类型
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // 验证登录信息
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                messages.getMessage("WebUserAuthenticationProvider.onlySupports", "Only WebUserAuthenticationToken is supported"));

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getPrincipal().toString();
        String password = token.getCredentials().toString();

        unifyAdmin = unifyAdminService.loadAdminByUsername(username);
        if (unifyAdmin == null) {
            throw new UsernameNotFoundException(username);
        }

        // 检查密码是否匹配
        checkPassword(unifyAdmin, password);

        // 添加用户角色权限
        List<GrantedAuthority> authorities = setAuthorities(unifyAdmin);

        return new UsernamePasswordAuthenticationToken(unifyAdmin, password, authorities);
    }

    private void checkPassword(UnifyAdmin admin, String password) {
        if (StringUtils.isBlank(password) || !PASSWORD_ENCODER.matches(password, admin.getPassword())) {
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    public static class LoginInfo implements Serializable {
        private String username;
        private String password;
        private String verificationCode;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }
    }



    public List<GrantedAuthority> setAuthorities(UnifyAdmin unifyAdmin){
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(UnifyRole unifyRole:unifyAdmin.getRole()){
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+unifyRole.getCode());
            authorities.add(grantedAuthority);
        }
        for(UnifyAuthority unifyAuthority:unifyAdmin.getAuthority()){
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(unifyAuthority.getCode());
            authorities.add(grantedAuthority);
        }
        return authorities;
    }

}
