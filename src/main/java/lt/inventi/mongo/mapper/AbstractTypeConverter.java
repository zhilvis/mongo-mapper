package lt.inventi.mongo.mapper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTypeConverter<T> implements TypeConverter<T> {

    @Override
    public List<?> convertToDBList(List<T> objectList) {
        List<Object> list = new ArrayList<Object>();
        for (T object : objectList) {
            list.add(convertToDBValue(object));
        }
        return list;
    }

    @Override
    public List<T> convertFromDBList(List<?> dbList) {
        List<T> list = new ArrayList<T>();
        for (Object dbValue : dbList) {
            list.add(convertFromDBValue(dbValue));
        }
        return list;
    }
}
