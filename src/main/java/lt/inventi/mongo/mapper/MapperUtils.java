package lt.inventi.mongo.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;

public class MapperUtils {

    public static boolean isBase(Object value) {
        return value instanceof String || value instanceof Boolean || value instanceof Number || value instanceof Date
                || value instanceof byte[];
    }

    public static boolean isBaseType(Class<?> type) {
        return type == String.class || type == Boolean.class || type == Integer.class || type == Long.class
                || type == Float.class || type == Double.class || type == BigDecimal.class || type == Date.class
                || type == boolean.class || type == long.class || type == int.class || type == float.class
                || type == double.class;
    }

    public static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superType = type.getSuperclass();
            if (superType != null) {
                return findField(superType, fieldName);
            }
            return null;
        }
    }

    public static Class<?> getListGenericType(Field field) {
        Type type = field.getGenericType();
        Type paramType = null;
        if (type instanceof ParameterizedType) {
            paramType = ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        return (Class<?>) paramType;
    }

    public static <T> T newInstance(Class<T> entityClass) {
        T entity;
        try {
            entity = entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    public static Field[] getDeclaredFields(Object pojo) {
        Field[] fields = pojo.getClass().getDeclaredFields();

        Class<?> parentClass = pojo.getClass().getSuperclass();

        while (!parentClass.equals(Object.class)) {
            Field[] parentFields = parentClass.getDeclaredFields();
            Field[] allFields = new Field[fields.length + parentFields.length];
            System.arraycopy(parentFields, 0, allFields, 0, parentFields.length);
            System.arraycopy(fields, 0, allFields, parentFields.length, fields.length);
            fields = allFields;
            parentClass = parentClass.getSuperclass();
        }
        return fields;
    }
}
