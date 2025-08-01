package dev.shaaf.keycloak.mcp.server.ingest.loader;

import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class JarDocumentSource implements DocumentSource {
    public static final String SOURCE = "source";

    private final InputStream inputStream;

    private final String pathString;

    JarDocumentSource(Path filePath) throws IOException {
        pathString = filePath.toString();
        this.inputStream = Files.newInputStream(filePath);
    }

    @Override
    public InputStream inputStream() throws IOException {
        return inputStream;
    }

    @Override
    public Metadata metadata() {
        return Metadata.from(SOURCE, pathString);
    }
}
