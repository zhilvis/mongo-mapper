package lt.inventi.mongo.mapper;

import static lt.inventi.mongo.mapper.MapperUtils.getDeclaredFields;
import static lt.inventi.mongo.mapper.MapperUtils.isBase;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class EntityMapper {

    private Map<Class<?>, AutoConverter<?>> converters = new HashMap<Class<?>, AutoConverter<?>>();

    public void setConverters(Map<Class<?>, AutoConverter<?>> converters) {
        this.converters = converters;
    }

    /**
     * Converts given entity to the DBObject The only difference between this
     * method has over convertPojo, is that it expects entity to have an id and
     * sets it as mongo ObjectId.
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public DBObject dbObject(Object entity) throws Exception {
        DBObject dbObject = (DBObject) convertPojo(entity);
        return dbObject;
    }

    /**
     * Converts given pojo to the DBObject
     * 
     * @param pojo
     * @return
     * @throws Exception
     */
    private Object convertPojo(Object pojo) throws Exception {
        AutoConverter<Object> converter = (AutoConverter<Object>) converters.get(pojo.getClass());
        if (converter != null) {
            return converter.dbValue(pojo);
        }

        BasicDBObject dbObject = new BasicDBObject();

        List<Field> fields = getDeclaredFields(pojo);

        for (Field field : fields) {
            String fieldName = field.getName();
            Object value = field.get(pojo);
            if (value == null) {
                continue;
            } else if ("id".equals(fieldName)) {
                dbObject.put("_id", new ObjectId((String) value));
                continue;
            } else if (fieldName.indexOf("Id") > -1) {
                // do something with id
            }

            value = convertValue(value);
            if (value != null) {
                dbObject.put(fieldName, value);
            }
        }

        return dbObject;
    }

    private Object convertNestedPojo(Object pojo) throws Exception {
        return convertPojo(pojo);
    }

    private Object convertValue(Object value) throws Exception {

        Object resultValue = null;
        if (isBase(value)) {
            resultValue = convertSimpleValue(value);
        } else if (value instanceof Enum) {
            resultValue = ((Enum<?>) value).name();
        } else if (value instanceof List) {
            List<?> col = (List<?>) value;
            if (!col.isEmpty()) {
                resultValue = convertListValue(col);
            }
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;

            if (!map.isEmpty())
                resultValue = new BasicDBObject((Map<?, ?>) value);
        } else {
            resultValue = convertNestedPojo(value);
        }
        return resultValue;
    }

    private List<?> convertListValue(List<?> col) throws Exception {
        Object firstVal = col.iterator().next();
        AutoConverter<Object> converter = (AutoConverter<Object>) converters.get(firstVal.getClass());
        if (converter != null) {
            return converter.dbValues((List<Object>) col);
        }

        List<Object> dbObjList = new BasicDBList();
        if (isBase(firstVal)) {
            for (Object val : col) {
                dbObjList.add(convertSimpleValue(val));
            }
        } else if (firstVal instanceof Enum<?>) {
            for (Object val : col) {
                dbObjList.add(((Enum<?>) val).name());
            }
        } else {
            for (Object val : col) {
                dbObjList.add(convertNestedPojo(val));
            }
        }
        return dbObjList;
    }

    private Object convertSimpleValue(Object value) {
        Object result = value;
        if (value instanceof BigDecimal) {
            result = value.toString();
        }
        return result;
    }
}
