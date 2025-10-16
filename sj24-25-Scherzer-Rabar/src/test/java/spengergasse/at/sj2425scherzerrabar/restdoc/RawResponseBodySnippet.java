package spengergasse.at.sj2425scherzerrabar.restdoc;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractBodySnippet;

import java.io.IOException;

public class RawResponseBodySnippet extends AbstractBodySnippet {

    public RawResponseBodySnippet() {super("raw-response",null,null);}

    @Override
    protected byte[] getContent(Operation operation) throws IOException {
        return operation.getResponse().getContent();
    }

    @Override
    protected MediaType getContentType(Operation operation) {
        return operation.getResponse().getHeaders().getContentType();
    }
}
