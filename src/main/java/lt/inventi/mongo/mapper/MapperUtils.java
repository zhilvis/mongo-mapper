package lt.inventi.mongo.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static List<Field> getDeclaredFields(Object pojo) {
    	List<Field> fields = getFields(pojo.getClass());

        Class<?> parentClass = pojo.getClass().getSuperclass();

        while (!parentClass.equals(Object.class)) {
            fields.addAll(getFields(parentClass));
            parentClass = parentClass.getSuperclass();
        }
        return fields;
    }

	private static List<Field> getFields(Class<?> aClass) {
		List<Field> fields = new ArrayList<Field>();
        for(Field field : aClass.getDeclaredFields()){
        	field.setAccessible(true);
        	int modifiers = field.getModifiers();
        	if(Modifier.isStatic(modifiers)
        			|| "java.lang.Class".equals(field.getType().getName())
        			|| "serialVersionUID".equals(field.getName())){
        		continue;
        	}
        	fields.add(field);
        }
		return fields;
	}
}
