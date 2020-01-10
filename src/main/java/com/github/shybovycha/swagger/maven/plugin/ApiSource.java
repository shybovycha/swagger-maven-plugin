package com.github.shybovycha.swagger.maven.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Parameter;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSource.class);

    /**
     * Java classes annotated with JAX-RS <code>@Path</code>, or Java packages containing those classes
     * can be configured here.
     */
    @Parameter(required = true)
    private List<String> locations;

    /**
     * <code>templatePath</code> is the path of a hbs template file,
     * see more details in next section.
     * If you don't want to generate extra api documents, just don't set it.
     */
    @Parameter
    private String templatePath;

    @Parameter
    private String outputPath;

    @Parameter(defaultValue = "json")
    private OutputFormat outputFormat;

    public Set<Class<?>> getValidClasses(Class<? extends Annotation> clazz) {
        Set<Class<?>> result = new LinkedHashSet<>();

        List<String> prefixes = new ArrayList<>();

        if (getLocations() == null) {
            prefixes.add("");
        } else {
            prefixes.addAll(getLocations());
        }

        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner());

        for (String location : prefixes) {
            configuration.addUrls(ClasspathHelper.forResource(location));
        }

        if (prefixes.isEmpty()) {
            configuration.addUrls(ClasspathHelper.forJavaClassPath())
                    .addUrls(ClasspathHelper.forClassLoader());
        }

        Reflections reflections = new Reflections(configuration);

        LOGGER.debug("Reflections: all {} classes: {}", reflections.getAllTypes().size(), reflections.getAllTypes());

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(clazz, true);
        result.addAll(classes);

        LOGGER.debug("Reflections: for {} annotation found classes: {}", clazz, result.stream().map(Class::getCanonicalName).collect(Collectors.joining(",")));

        return result;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = OutputFormat.valueOf(outputFormat);
    }

	private static String emptyToNull(final String str) {
        return StringUtils.isEmpty(str) ? null : str;
    }
}

