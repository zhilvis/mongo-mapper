package lt.inventi.mongo.mapper;

import static lt.inventi.mongo.mapper.MapperUtils.getListGenericType;
import static lt.inventi.mongo.mapper.MapperUtils.isBaseType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;

public class DBObjectMapper {

    private static final Logger log = LoggerFactory.getLogger(DBObjectMapper.class);

    private Map<Class<?>, AutoConverter<?>> converters = new HashMap<Class<?>, AutoConverter<?>>();

    protected void setConverters(Map<Class<?>, AutoConverter<?>> converters) {
        this.converters = converters;
    }

    public <T> T entity(DBObject doc, T entity) throws Exception {
        return doConvertDBObject(doc, entity);
    }

    private <T> T doConvertDBObject(DBObject doc, T entity) throws Exception {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        } else if (doc == null) {
            return null;
        }

        List<Field> fields = MapperUtils.getDeclaredFields(entity);
        for (Field field : fields) {
            String fieldName = field.getName();
            if ("id".equals(fieldName)) {
                Object id = doc.get("_id");
                if (id == null) {
                    id = doc.get("id");
                }
                if (id != null) {
                    field.set(entity, id.toString());
                }
                continue;
            }

            Object value = doc.get(fieldName);
            if (value == null) {
                continue;
            }

            Object convertedValue = null;
            try {
                convertedValue = convertValue(field, entity, value);
            } catch (NumberFormatException e) {
                log.error("Field \"" + fieldName + "\" seems corrupted. Can't covert value \"" + value
                        + "\" to BigDecimal.");
            }

            try {
                field.set(entity, convertedValue);
            } catch (IllegalArgumentException e) {
                Type argType = field.getGenericType();
                String message = "Can't map \"" + entity.getClass().getSimpleName() + "." + fieldName + "\" field."
                        + " Parameter type: \"" + argType + "\"" + ", value type: \""
                        + convertedValue.getClass().getName() + "\"";
                throw new IllegalArgumentException(message, e);
            }
        }
        return entity;
    }

    private <T> T convertNestedDBObject(DBObject doc, Class<T> entityClass) throws Exception {
        return doConvertDBObject(doc, entityClass.newInstance());
    }

    private <T> Object convertValue(Field field, T entity, Object value) throws Exception {

        Object convertedValue = null;
        Class<?> valueType = field.getType();
        AutoConverter<?> converter = converters.get(valueType);

        if (converter != null) {
            convertedValue = converter.entityValue(value);
        } else if (value instanceof List<?>) {
            List<Object> targetList = null;
            convertedValue = convertListValue(field, (List<?>) value, targetList);

        } else if (value instanceof DBObject) {

            if (Map.class == valueType) {
                convertedValue = convertMapValue((DBObject) value);
            } else {
                convertedValue = convertNestedDBObject((DBObject) value, valueType);
            }

        } else {

            convertedValue = convertSimpleValue(value, valueType);

        }

        return convertedValue;
    }

    private <T> Object convertMapValue(DBObject value) {
        if (value == null)
            return new HashMap<Object, Object>();
        else
            return value.toMap();
    }

    private <T> Object convertListValue(Field field, List<?> source, Collection<Object> target) throws Exception {

        Class<?> fieldType = getListGenericType(field);
        AutoConverter<?> converter = converters.get(fieldType);
        if (converter != null) {
            return converter.entityValues(source);
        }

        if (target == null) {
            target = new ArrayList<Object>();
        }

        for (Object item : source) {
            if (item instanceof DBObject) {
                target.add(convertNestedDBObject((DBObject) item, fieldType));
            } else {
                target.add(convertSimpleValue(item, fieldType));
            }
        }
        return target;
    }

    private Object convertSimpleValue(Object value, Class<?> type) throws Exception {

        if (BigDecimal.class.equals(type)) {
            return new BigDecimal(value.toString());
        } else if (isBaseType(type)) {
            // sometimes when type is long mongo returns integer
            // not sure whether this is bug.
            if (value instanceof Integer && type == Long.class) {
                value = new Long(((Integer) value).longValue());
            }
            return value;
        } else if (type.isArray()) {
            return value;
        } else {
            // this might be enum
            Object found = null;
            Object[] enumConstants = type.getEnumConstants();
            if (enumConstants != null) {
                for (Object enumValue : enumConstants) {
                    if (value.equals(enumValue.toString())) {
                        found = enumValue;
                        break;
                    }
                }
                return found;
            }
        }
        // we are unable to map, so lets try to leave value as it is.
        return value;
    }

}
