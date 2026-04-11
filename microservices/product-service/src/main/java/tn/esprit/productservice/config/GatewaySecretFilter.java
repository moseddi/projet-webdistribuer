package tn.esprit.productservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(1) // runs BEFORE Spring Security
public class GatewaySecretFilter implements Filter {

    @Value("${internal.secret}")
    private String secret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Allow health check to pass without secret
        if (req.getRequestURI().contains("/actuator/health")) {
            chain.doFilter(request, response);
            return;
        }

        String header = req.getHeader("X-Internal-Secret");
        if (!secret.equals(header)) {
            res.sendError(403, "Access denied - bypass gateway not allowed");
            return;
        }

        chain.doFilter(request, response);
    }
}