package org.feature.management.config;

import org.feature.management.filters.EnvironmentETagValidationFilter;
import org.feature.management.filters.FeatureETagValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean()
    public FilterRegistrationBean<EnvironmentETagValidationFilter> environmentETagValidationFilterBean(EnvironmentETagValidationFilter filter) {
        FilterRegistrationBean<EnvironmentETagValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(1);
        registrationBean.addUrlPatterns("/environments/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<FeatureETagValidationFilter> featureETagValidationFilterBean(FeatureETagValidationFilter filter) {
        FilterRegistrationBean<FeatureETagValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(2);
        registrationBean.addUrlPatterns("/features/*");
        return registrationBean;
    }
}
