package com.pfe.BienImmobilier.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        // Récupérer le token depuis les paramètres
        String query = request.getURI().getQuery();
        if (query == null) return false;

        String[] params = query.split("&");
        String token = null;
        String userId = null;

        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                if ("token".equals(keyValue[0])) {
                    token = keyValue[1];
                } else if ("userId".equals(keyValue[0])) {
                    userId = keyValue[1];
                }
            }
        }

        if (token != null && userId != null) {
            try {
                String email = jwtUtil.extractEmail(token);
                if (jwtUtil.validateToken(token, email)) {
                    attributes.put("userId", userId);
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}