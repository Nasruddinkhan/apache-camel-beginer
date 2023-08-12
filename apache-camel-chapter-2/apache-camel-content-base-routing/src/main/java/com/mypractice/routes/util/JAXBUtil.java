package com.mypractice.routes.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringWriter;
import java.util.List;

public class JAXBUtil {
    public static <T> String marshal(T request) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(request.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        String marshalledXml = writer.toString();

        return marshalledXml;
    }

    public static  <T> T unmarshal(String xmlResponse, Class<T> response) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(response);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return response.cast(unmarshaller.unmarshal(new File(xmlResponse)));
    }
    public static <T> List<T> unmarshalList(String xmlResponse, Class<T> response) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(response);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (List<T>) response.cast(unmarshaller.unmarshal(new File(xmlResponse)));
    }

//    public static final <S, T> List<T> mapAll(final List<S> source, Class<T> destination) {
//        Type type = new ParameterizedType() {
//            @Override
//            public Type[] getActualTypeArguments() {
//                return new Type[]{destination};
//            }
//
//            @Override
//            public Type getRawType() {
//                return List.class;
//            }
//
//            @Override
//            public Type getOwnerType() {
//                return null;
//            }
//        };
//        return getModelMapper().map(source, type);
//    }

    public static void main(String[] args) throws JAXBException {

    }
}
