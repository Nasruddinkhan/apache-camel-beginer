package com.mypractice.routes.util;

import com.mypractice.routes.dto.Products;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CSVUtilParser {
    public static <T> List<T> parseCSV(String csvFilePath, Class<T> targetType) {
        System.out.println("csvFilePath :::" +csvFilePath);
        List<T> objects = new ArrayList<>();
        try (CSVParser csvParser = CSVParser.parse(new FileReader(csvFilePath), CSVFormat.DEFAULT.withHeader())) {
            objects = csvParser.getRecords().stream()
                    .map(csvRecord -> createObjectFromRecord(csvRecord, targetType))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }
    private static <T> T createObjectFromRecord(CSVRecord csvRecord, Class<T> targetType)  {
        T object;
        try {
            object = targetType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        for (Field field : targetType.getDeclaredFields()) {
            String fieldName = field.getName();
            if (csvRecord.isMapped(fieldName)) {
                String fieldValue = csvRecord.get(fieldName);
                try {
                    setFieldValue(object, field, fieldValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return object;
    }
    private static <T> void setFieldValue(T object, Field field, String value) throws IllegalAccessException {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        switch (fieldType.getSimpleName()) {
            case "String" -> field.set(object, value);
            case "Integer", "int" -> field.set(object, Integer.parseInt(value));
            case "Double", "double" -> field.set(object, Double.parseDouble(value));
            // Add more type mappings as needed
        }
        field.setAccessible(false);
    }
    public static void main(String[] args){
      //
     var products =   parseCSV("D:\\Projects\\sftp-inbound\\products.csv", Products.class);
     products.forEach(System.out::println);
    }
}
