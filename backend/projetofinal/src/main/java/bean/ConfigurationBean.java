package bean;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateless;

@Stateless
public class ConfigurationBean {
    /**
     * Allows to write logs in a file
     */
    String filename = "project-critical.log";
    String pattern = "%d{yyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %m%n";
    ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
    RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.DEBUG);

    public ConfigurationBean() {

        builder.setStatusLevel(Level.DEBUG);
        builder.setConfigurationName("DefaultFileLogger");
        // set the pattern layout and pattern
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout").addAttribute("pattern", pattern);

        // create a console appender
        AppenderComponentBuilder concoleAppenderBuilder = builder.newAppender("Console", "CONSOLE")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);

        // add a layout like pattern
        concoleAppenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", pattern));

        // create a file appender
        AppenderComponentBuilder fileAppenderBuilder = builder.newAppender("LogToFile", "File")
                .addAttribute("fileName", filename).add(layoutBuilder);
        builder.add(concoleAppenderBuilder);
        builder.add(fileAppenderBuilder);
        rootLogger.add(builder.newAppenderRef("Console"));
        rootLogger.add(builder.newAppenderRef("LogToFile"));
        builder.add(rootLogger);
        Configurator.reconfigure(builder.build());
    }
}
