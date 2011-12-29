package lt.inventi.mongo.mapper;

import java.util.List;

/**
 * 
 * Converts type to/from DB specific format. This is usefull to convert static
 * types, or classes which cannot be constructed with default constructor.
 * 
 * @see ConvertableType
 */
public interface TypeConverter<T> {
    public List<?> convertToDBList(List<T> objectList);

    public List<T> convertFromDBList(List<?> dbList);

    public Object convertToDBValue(T object);

    public T convertFromDBValue(Object dbValue);
}
