package com.mypractice.routes.util;

import com.mypractice.routes.anotations.CsvField;
import com.mypractice.routes.dto.Products;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Deprecated
public class CSVUtilParserMap {
    private static Map<Class<?>, Map<String, Field>> classFieldMap = new HashMap<>();

    private static <T> void initializeFieldMap(Class<T> targetType) {
        if (!classFieldMap.containsKey(targetType)) {
            Field[] fields = targetType.getDeclaredFields();
            Map<String, Field> fieldMap = new HashMap<>();
            for (Field field : fields) {
                if (field.isAnnotationPresent(CsvField.class)) {
                    CsvField annotation = field.getAnnotation(CsvField.class);
                    fieldMap.put(annotation.name().toLowerCase(), field);
                    System.out.println(annotation.name()+" "+field.getName());

                }
            }
            classFieldMap.put(targetType, fieldMap);
        }
    }

    public static <T> List<T> parseCSV(String csvFilePath, Class<T> targetType) {
        initializeFieldMap(targetType);
        List<T> objects = new ArrayList<>();
        try (CSVParser csvParser = new CSVParser(new FileReader(csvFilePath), CSVFormat.DEFAULT.withHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                T object = targetType.getDeclaredConstructor().newInstance();
                Map<String, Field> fieldMap = classFieldMap.get(targetType);
                for (Field field : fieldMap.values()) {
                    String fieldName = field.getName();
                    System.out.println(fieldName+" fieldName ::: " + csvRecord.isMapped(fieldName));
                    if (csvRecord.isMapped(fieldName)) {
                        String fieldValue = csvRecord.get(fieldName);
                        setFieldValue(object, field, fieldValue);
                    }
                }
                objects.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }
    private static <T> void setFieldValue(T object, Field field, String value) throws IllegalAccessException {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        System.out.println(fieldType+" :: "+value);
        if (fieldType == String.class) {
            field.set(object, value);
        } else if (fieldType == Integer.class || fieldType == int.class) {
            field.set(object, Integer.parseInt(value));
        } else if (fieldType == Double.class || fieldType == double.class) {
            field.set(object, Double.parseDouble(value));
        }else {
            field.set(object, value);
        }
        // Add more type mappings as needed

        field.setAccessible(false);
    }
    public static void main(String[] args){
      //
     var products =   parseCSV("D:\\Projects\\sftp-inbound\\products.csv", Products.class);
     products.forEach(System.out::println);
    }
}
