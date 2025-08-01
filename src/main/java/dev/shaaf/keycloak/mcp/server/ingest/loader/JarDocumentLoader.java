package dev.shaaf.keycloak.mcp.server.ingest.loader;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentParser;
import io.quarkus.logging.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class JarDocumentLoader {

    public Document loadDocument(JarDocumentSource source, DocumentParser parser) {
        return DocumentLoader.load(source, parser);
    }

    public List<Document> loadDocuments(String directory, DocumentParser parser) throws IOException, URISyntaxException {
        List<Document> documents = new ArrayList<>();
        // Use getResource on the ClassLoader WITHOUT a leading slash
        URL resourceUrl = getClass().getClassLoader().getResource(directory);
        if (resourceUrl == null) {
            throw new RuntimeException("Cannot find resource directory: " + directory);
        }
        URI uri = resourceUrl.toURI();
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {

            Path docPath = fileSystem.getPath(directory);
            try (Stream<Path> paths = Files.walk(docPath)) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> {
                            try {
                                // Create a Document directly from the InputStream
                                documents.add(loadDocument(new JarDocumentSource(filePath), parser));
                                Log.infof("Loaded document: %s", filePath);
                            } catch (Exception e) {
                                Log.errorf(e, "Failed to load document: %s", filePath);
                            }
                        });
            }
        }
        return documents;
    }

}
