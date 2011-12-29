package lt.inventi.mongo.mapper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAutoConverter<T> implements AutoConverter<T> {

    @Override
    public List<?> dbValues(List<T> objectList) {
        List<Object> list = new ArrayList<Object>();
        for (T object : objectList) {
            list.add(dbValue(object));
        }
        return list;
    }

    @Override
    public List<T> entityValues(List<?> dbList) {
        List<T> list = new ArrayList<T>();
        for (Object dbValue : dbList) {
            list.add(entityValue(dbValue));
        }
        return list;
    }
}
