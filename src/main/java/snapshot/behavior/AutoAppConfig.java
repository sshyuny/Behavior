package snapshot.behavior;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import snapshot.behavior.interceptor.LoginInterceptor;

@Configuration
@ComponentScan
public class AutoAppConfig implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
            .addPathPatterns("/behavior", "/category")
            .excludePathPatterns("/", "/login", "/error");
    }

}
