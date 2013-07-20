/**
 * Copyright 2013 David van Enckevort
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.allthingsdigital.example.webconfig;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * Initialize the Application Context with an external properties file if it exists.
 * @author David van Enckevort
 */
public class PropertiesInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext>  {
    /**
     * A logger instance used for logging messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(PropertiesInitializer.class);
    private static final String CONFIG_PATH = "WEBCONFIG_LOCATION";
    
    @Override
    public void initialize(ConfigurableWebApplicationContext ctx) {
        final String userLocation = System.getProperty(CONFIG_PATH);
        if (userLocation != null) {
            Path propertiesFile = Paths.get(userLocation);
            
            if (propertiesFile.toFile().canRead()) {

                final Properties userProperties = new Properties();
                try (Reader propertiesReader = Files.newBufferedReader(propertiesFile, StandardCharsets.UTF_8)) {
                    userProperties.load(propertiesReader);
                    final PropertiesPropertySource ps = new PropertiesPropertySource("USER_PROPERTIES", userProperties);
                    ctx.getEnvironment().getPropertySources().addFirst(ps);
                } catch (final IOException ex) {
                    logger.warn("Exception while reading the properties", ex);
                }
            } else {
                logger.warn("The file {} does not exist.", userLocation);
            }
        } else {
            logger.info("The Java system property {} was not set."
                    + " You can set it to the location of a properties file to override the default configuration.",
                    CONFIG_PATH);
        }
    }
}
