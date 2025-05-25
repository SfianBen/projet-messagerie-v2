package org.isihop.fr.shellClient;

/**
 *
 * @author tondeur-h
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandCatalogCustomizer;

@Configuration
public class ShellConfig {

    @Bean
    public CommandCatalogCustomizer disableBuiltInCommands() {
        
        
        return catalog -> {
                catalog.unregister("stacktrace");
                catalog.unregister("clear");
                catalog.unregister("quit");
                catalog.unregister("exit");
                catalog.unregister("history");
                catalog.unregister("version");
                catalog.unregister("script");
            };
    }
}
