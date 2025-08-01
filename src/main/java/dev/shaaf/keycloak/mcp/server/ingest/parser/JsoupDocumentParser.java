package dev.shaaf.keycloak.mcp.server.ingest.parser;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;

@RegisterForReflection
public class JsoupDocumentParser implements DocumentParser {
    @Override
    public Document parse(InputStream inputStream) {
        try {
            org.jsoup.nodes.Document doc = Jsoup.parse(inputStream,null,"");
            return Document.from(doc.text());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
