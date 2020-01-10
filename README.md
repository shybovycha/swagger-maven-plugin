# Swagger Maven Plugin

[![Build Status](https://travis-ci.org/shybovycha/swagger-maven-plugin.png)](https://travis-ci.org/shybovycha/swagger-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.shybovycha/swagger-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.shybovycha/swagger-maven-plugin)

This plugin enables your Swagger-annotated project to generate **Swagger specs** and **customizable, templated static documents** during the maven build phase. Unlike swagger-core, swagger-maven-plugin does not actively serve the spec with the rest of the application; it generates the spec as a build artifact to be used in downstream Swagger tooling.

## Features

* Supports [OpenAPI Spec 3.x](https://swagger.io/specification/)
* Supports [JAX-RS](https://jax-rs-spec.java.net/)

## Versions
- [1.0.0]() adds support for OpenAPI 3.x spec, removes support for Spring applications
- [kongchen's 3.1.0](https://github.com/kongchen/swagger-maven-plugin/) supports Swagger Spec [2.0](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md), support JAX-RS & SpingMVC.

## Usage

Import the plugin in your project by adding following configuration in your `plugins` block:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.shybovycha</groupId>
            <artifactId>swagger-maven-plugin</artifactId>
            <version>${swagger.maven.plugin.version}</version>
            <configuration>
                <apiSources>
                    <apiSource>
                        ...
                    </apiSource>
                </apiSources>
            </configuration>

            <executions>
                <execution>
                    <phase>compile</phase>

                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Plugin configuration

#### `configuration`

|Property|Description|Default value|
|--------|-----------|-------------|
| `apiSources` | List of `apiSource` elements. One `apiSource` can be considered as a version of APIs of your service. You can specify several `apiSource` elements, though generally one is enough. |

#### `apiSource`

| Name | Description |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `locations` | **[Required]** Classes containing Swagger's annotation ```@Api```, or packages containing those classes can be configured here. Each item must be located inside a <location> tag. Example: `<locations><location>com.github.kongchen.swagger.sample.wordnik.resource</location><location>com.github.kongchen.swagger.sample.wordnik.resource2</location></locations>` |
| `info` **required**| The basic information of the api, using same definition as Swagger Spec 2.0's [info Object](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#infoObject) |
| `outputPath` | The path of the generated static document, not existed parent directories will be created. If you don't want to generate a static document, just don't set it. |
| `outputFormat` | The format types of the generated swagger spec. Valid values are `json`, `yaml` or both `json,yaml`. The `json` format is default.|
