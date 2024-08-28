package com.ericsson.gic.tms.tvs.infrastructure.provider;

import com.ericsson.gic.tms.presentation.dto.jsonapi.DocumentList;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.*;

@Provider
@Produces("text/csv")
public class CsvMessageBodyWriter implements MessageBodyWriter<DocumentList<?>> {

    private static final MediaType TEXT_CSV_TYPE = new MediaType("text", "csv");

    private CsvMapper mapper = new CsvMapper();

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return TEXT_CSV_TYPE.equals(mediaType) && DocumentList.class.isAssignableFrom((Class) type);
    }

    /**
     * Deprecated since JAX-RS 2.0
     */
    @Override
    public long getSize(DocumentList<?> testCaseResultBeans,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return -1L;
    }

    @Override
    public void writeTo(DocumentList<?> response,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        setHeaders(httpHeaders, response);

        List<?> results = response.unwrap();
        CsvSchema schema = mapper.schemaFor(getDocumentClass(genericType)).withHeader();
        String csvData = mapper.writer(schema).writeValueAsString(results);
        Writer out = new OutputStreamWriter(entityStream, StandardCharsets.UTF_8);
        entityStream.write(csvData.getBytes());
        out.flush();
    }

    private void setHeaders(MultivaluedMap<String, Object> httpHeaders, DocumentList res) {

        httpHeaders.putSingle(CONTENT_DISPOSITION, res.getMeta().get(CONTENT_DISPOSITION));
        httpHeaders.putSingle(CACHE_CONTROL, "public, no-cache, no-store, no-transform");
        httpHeaders.putSingle(CONTENT_TYPE, TEXT_CSV_TYPE);
    }

    private Class getDocumentClass(Type genericType) {
        Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        return (Class) type;
    }
}
