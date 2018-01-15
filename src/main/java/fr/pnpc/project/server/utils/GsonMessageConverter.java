package fr.pnpc.project.server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.pnpc.project.server.backingbean.LoginBean;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonMessageConverter
        implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

    private final static Logger LOGGER = Logger.getLogger(GsonMessageConverter.class.getSimpleName());

    private static final String UTF_8 = "UTF-8";

    private Gson gson;

    /**
     * Gson initializer
     * @return gson object with custom config.
     */
    private Gson getGson() {
        if (gson == null) {
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.disableHtmlEscaping()
                    //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();
        }
        return gson;
    }

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        InputStreamReader streamReader = null;
        try {
            streamReader = new InputStreamReader(inputStream, UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOGGER.info(e.getLocalizedMessage());
        }
        try {
            return getGson().fromJson(streamReader, type);
        } finally {
            try {
                if(streamReader != null)
                    streamReader.close();
            } catch (IOException e) {
                LOGGER.info(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, UTF_8);
        try {
            getGson().toJson(o, type, writer);
        } finally {
            writer.close();
        }
    }
}
