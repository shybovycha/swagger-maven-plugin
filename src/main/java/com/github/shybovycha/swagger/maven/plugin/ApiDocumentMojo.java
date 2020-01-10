package com.github.shybovycha.swagger.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import java.util.List;
import java.util.Set;
import java.util.Stack;

@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.COMPILE,
        configurator = "include-project-dependencies",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        threadSafe = true
)
public class ApiDocumentMojo extends AbstractMojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocumentMojo.class);

    /**
     * A set of apiSources.
     * One apiSource can be considered as a set of APIs for one apiVersion in a basePath
     */
    @Parameter
    private List<ApiSource> apiSources;

    @Parameter
    private String outputPath;

    @Parameter(defaultValue = "json")
    private OutputFormat outputFormat;

    public List<ApiSource> getApiSources() {
        return apiSources;
    }

    public void setApiSources(List<ApiSource> apiSources) {
        this.apiSources = apiSources;
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

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            LOGGER.info("Generating OpenAPI definitions");

            int idx = 0;

            for (ApiSource apiSource : apiSources) {
                LOGGER.info("Processing source {}", idx++);

                validateConfiguration(apiSource);

                Set<Class<?>> classes = apiSource.getValidClasses(Path.class);

                LOGGER.info("Found {} resources", classes.size());

                for (Class<?> cls : classes) {
                    Class<?> currentClass = cls;

                    LOGGER.info("Processing resource {}", cls.getCanonicalName());

                    Stack<String> pathPieces = new Stack<>();

                    while (currentClass != null && currentClass != Object.class) {
                        LOGGER.info("Traversing class: {}, current: {}", cls.getCanonicalName(), currentClass.getCanonicalName());

                        Path[] annotations = currentClass.getAnnotationsByType(Path.class);

                        if (annotations.length > 0) {
                            Path apiPath = annotations[0];

                            if (apiPath != null) {
                                pathPieces.push(apiPath.value());
                            }
                        }

                        currentClass = currentClass.getSuperclass();
                    }

                    StringBuilder path = new StringBuilder();

                    while (!pathPieces.isEmpty()) {
                        path.append("/").append(pathPieces.pop());
                    }

                    LOGGER.info("Found resource '{}' with path {}", cls.getSimpleName(), path);
                }
            }
        } catch (GenerateException e) {
            throw new MojoFailureException(e.getMessage(), e);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * validate configuration according to swagger spec and plugin requirement
     *
     * @param apiSource
     * @throws GenerateException
     */
    private void validateConfiguration(ApiSource apiSource) throws GenerateException {
        if (apiSource == null) {
            throw new GenerateException("You do not configure any apiSource!");
        }

        if (apiSource.getLocations() == null) {
            throw new GenerateException("<locations> is required by this plugin.");
        }
    }
}
