package lt.inventi.mongo.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;

public class MongoMapper {

    private EntityMapper entityMapper;
    private DBObjectMapper dbObjectMapper;

    public MongoMapper() {
        this.entityMapper = new EntityMapper();
        this.dbObjectMapper = new DBObjectMapper();
    }

    public void setConverters(Map<Class<?>, AutoConverter<?>> converters) {
        dbObjectMapper.setConverters(converters);
        entityMapper.setConverters(converters);
    }

    /**
     * Converts any pojo object to DBObject. Pojo may contain other nested
     * pojos.
     * 
     * @param entity
     * @return
     */
    public DBObject dbObject(Object entity) {
        try {
            return entityMapper.dbObject(entity);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts DBObject to specified pojo if there are any nested pojos those
     * will be instaniated automatically.
     * 
     * Root pojo may optionally contain string id attribute, which will be set
     * to mongo db id.
     * 
     * @param dbObj
     * @param entity
     */
    public <T> T entity(DBObject dbObj, T entity) {
        try {
            return dbObjectMapper.entity(dbObj, entity);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public final CursorMapper from(Iterable<DBObject> dbObj) {
        return new CursorMapper(dbObj);
    }

    public final ObjectMapper from(DBObject dbObj) {
        return new ObjectMapper(dbObj);
    }

    public class ObjectMapper {
        private DBObject dbObject;

        public ObjectMapper(DBObject dbObject) {
            this.dbObject = dbObject;
        }

        public <T> T to(Class<T> entity) {
            T instance;
            try {
                instance = entity.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            entity(dbObject, instance);
            return instance;
        }
    }

    public class CursorMapper {
        private Iterator<DBObject> dbCursor;

        public CursorMapper(Iterable<DBObject> dbObject) {
            if (dbObject instanceof Iterator) {
                this.dbCursor = (Iterator<DBObject>) dbObject;
            } else {
                this.dbCursor = dbObject.iterator();
            }
        }

        public <T> List<T> toList(Class<T> entityClass) {
            List<T> list = new ArrayList<T>();
            while (dbCursor.hasNext()) {
                DBObject dbObj = dbCursor.next();
                T entity = MapperUtils.newInstance(entityClass);
                entity(dbObj, entity);
                list.add(entity);
            }
            return list;
        }
    }
}
