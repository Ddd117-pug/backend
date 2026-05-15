package com.toyshop.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.toyshop.user.entity.SysUser;
import com.toyshop.user.mapper.SysUserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;

    public JwtAuthFilter(JwtUtil jwtUtil, SysUserMapper sysUserMapper) {
        this.jwtUtil = jwtUtil;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtUtil.parseClaims(token);
                String username = claims.getSubject();
                Integer role = claims.get("role", Integer.class);
                Long uid = claims.get("uid", Long.class);

                SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                        .eq("id", uid)
                        .eq("is_deleted", 0)
                        .eq("status", 1)
                        .last("limit 1"));
                if (user == null) {
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(role != null && role == 1 ? "ROLE_ADMIN" : "ROLE_USER")
                );

                JwtUser principal = new JwtUser(uid, username, role);
                Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
