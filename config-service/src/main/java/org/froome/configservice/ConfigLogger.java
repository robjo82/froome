package org.froome.configservice;


import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class ConfigLogger implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        System.out.println("========== Property Sources ==========");
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            System.out.println("Property Source: " + propertySource.getName());
            System.out.println(propertySource.getSource());
        }
        System.out.println("=======================================");
    }
}
