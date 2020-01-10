package com.github.shybovycha.swagger.maven.plugin;

public enum OutputFormat {
    json("json"), yaml("yaml");

    private String value;

    OutputFormat(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
