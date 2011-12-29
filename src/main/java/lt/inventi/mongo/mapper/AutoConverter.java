package lt.inventi.mongo.mapper;

import java.util.List;

/**
 * Converts type to/from DB specific format. This is usefull to convert static
 * types, or classes which cannot be constructed with default constructor.
 */
public interface AutoConverter<T> {
    public List<?> dbValues(List<T> objectList);

    public List<T> entityValues(List<?> dbList);

    public Object dbValue(T object);

    public T entityValue(Object dbValue);
}
