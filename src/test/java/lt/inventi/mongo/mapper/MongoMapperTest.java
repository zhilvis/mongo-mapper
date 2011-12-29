package lt.inventi.mongo.mapper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;
import lt.inventi.mongo.mapper.MongoMapper;
import lt.inventi.mongo.mapper.TypeConverter;
import lt.inventi.mongo.mapper.model.AnotherEntity;
import lt.inventi.mongo.mapper.model.ConvertableClass;
import lt.inventi.mongo.mapper.model.Entity;
import lt.inventi.mongo.mapper.model.MyEnum;
import lt.inventi.mongo.mapper.model.NestedEntity;
import lt.inventi.mongo.mapper.model.ValueObject;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoMapperTest {

    private MongoMapper objectMapper = new MongoMapper();
    private HashMap<Class<?>, TypeConverter<?>> converters;

    @Before
    public void setUp() {
        converters = new HashMap<Class<?>, TypeConverter<?>>();
        objectMapper.setConverters(converters);
    }

    @Test
    public void testPerformance() {
        Entity inv = createEntity();
        for (int i = 0; i < 10; i++) {
            DBObject dbObj = objectMapper.dbObject(inv);
            objectMapper.entity(dbObj, new Entity());
        }

        long time = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            DBObject dbObj = objectMapper.dbObject(inv);
            objectMapper.entity(dbObj, new Entity());
        }
        time = System.currentTimeMillis() - time;
        double result = time / 100D;
        // Assert.assertTrue("Conversion is slow. Expected - " + acceptable +
        // " but was - "+result , acceptable > result);
        System.out.println("result: " + result);
    }

    @Test
    public void testParentEntityMapping() {
        Entity entity = new Entity();
        entity.setParentField("parent");

        DBObject dbObject = objectMapper.dbObject(entity);
        assertEquals("parent", dbObject.get("parentField"));

        Entity newEntity = objectMapper.entity(dbObject, new Entity());
        assertEquals("parent", newEntity.getParentField());
    }

    @Test
    public void testValueObjectMapping() {
        ValueObject obj = new ValueObject("test", 123);
        DBObject dbObj = objectMapper.dbObject(obj);
        assertEquals("test", dbObj.get("stringValue"));
        assertEquals(123, dbObj.get("intValue"));

        ValueObject newObj = objectMapper.entity(dbObj, new ValueObject());
        assertEquals(obj, newObj);
    }

    @Test
    public void testId() {
        Entity entity = new Entity();
        entity = objectMapper.entity(new BasicDBObject("_id", new ObjectId()), entity);
        assertNotNull("id is not mapped", entity.getId());

        DBObject dbObject = objectMapper.dbObject(entity);

        assertNotNull("id is not mapped", dbObject.get("_id"));
        assertTrue(dbObject.get("_id") instanceof ObjectId);

    }

    @Test
    public void testToDBObject() throws Exception {
        Entity testEntity = createEntity();

        DBObject expected = new BasicDBObject();
        expected.put("string", "test");
        expected.put("integer", new Integer(123));
        expected.put("myLong", new Long(1234));
        expected.put("bool", Boolean.TRUE);
        expected.put("boolPrimitive", true);
        expected.put("intPrimitive", 123);
        expected.put("bigDecimal", "123.1234");
        expected.put("enumField", MyEnum.ENUM_VAL1.name());
        expected.put("listOfStrings", Arrays.asList("test1", "test2", "test3"));
        expected.put("listOfBigDecimal", Arrays.asList("1", "2", "3"));
        expected.put("nestedSingle", new BasicDBObject("string", "nested-single"));
        expected.put("transientField", "transientField");

        List<DBObject> nestedDBList = new ArrayList<DBObject>();
        DBObject obj = new BasicDBObject();
        obj.put("_id", new ObjectId(testEntity.getNestedList().get(0).getId()));
        obj.put("string", "nested-test1");
        nestedDBList.add(obj);
        obj = new BasicDBObject();
        obj.put("_id", new ObjectId(testEntity.getNestedList().get(1).getId()));
        obj.put("string", "nested-test2");
        nestedDBList.add(obj);
        expected.put("nestedList", nestedDBList);

        DBObject map = new BasicDBObject();
        map.put("key1", "strValue");
        map.put("key2", Long.valueOf(0));
        map.put("key3", testEntity.getMapField().get("key3"));
        map.put("key4", BigDecimal.valueOf(0));
        expected.put("mapField", map);

        expected.put("bytes", "bytesTest".getBytes());

        DBObject actualDBObj = objectMapper.dbObject(testEntity);

        assertEquals(new String((byte[]) expected.removeField("bytes")),
                new String((byte[]) actualDBObj.removeField("bytes")));

        assertEquals(expected, actualDBObj);
    }

    @Test
    public void testMappedTypeSetting() {
        Entity testEntity = createEntity();
        DBObject dbObj = objectMapper.dbObject(testEntity);
        NestedEntity nested = new NestedEntity();
        nested.setString("myEntity");
        dbObj.put("nestedSingle", nested);
        Entity newEntity = objectMapper.entity(dbObj, new Entity());

        Assert.assertEquals("myEntity", newEntity.getNestedSingle().getString());
    }

    /**
     * Keep in mind that this test is not accurate, as there is no round trip to
     * db made. (see commented code block) Mongo replaces some fields with its
     * values when inserting/fetching, like for example List to BasicDBList and
     * etc. At the moment this test is OK with and without DB, but consider to
     * check continually against db.
     */
    @Test
    public void testFromDBObject() throws Exception {

        Entity testEntity = createEntity();

        DBObject dbObj = objectMapper.dbObject(testEntity);
        dbObj.put("_id", new ObjectId("4bba12b8867a00000000bc13"));

        // Mongo mongo = new Mongo(new DBAddress("localhost", "zz"));
        // DB db = mongo.getDB("test");
        // DBCollection col = db.getCollection("tests");
        // col.insert(dbObj);
        // dbObj = col.findOne(new BasicDBObject("_id", dbObj.get("_id")));

        Entity newTestEntity = (Entity) objectMapper.entity(dbObj, new Entity());

        assertEquals("test", newTestEntity.getString());
        assertEquals(new Integer(123), newTestEntity.getInteger());
        assertEquals(new Long(1234), newTestEntity.getMyLong());
        assertEquals(Boolean.TRUE, newTestEntity.getBool());
        assertEquals(true, newTestEntity.isBoolPrimitive());
        assertEquals(123, newTestEntity.getIntPrimitive());

        assertNotNull(newTestEntity.getEnumField());
        assertEquals(MyEnum.ENUM_VAL1, newTestEntity.getEnumField());

        List<String> list = newTestEntity.getListOfStrings();
        assertNotNull(list);
        int i = 1;
        for (String item : list) {
            assertEquals("test" + i, item);
            i++;
        }

        List<BigDecimal> decimalList = newTestEntity.getListOfBigDecimal();
        assertNotNull(decimalList);
        i = 1;
        for (BigDecimal item : decimalList) {
            assertEquals(new BigDecimal("" + i), item);
            i++;
        }

        List<NestedEntity> nestedDBList = (List<NestedEntity>) newTestEntity.getNestedList();

        i = 1;
        for (NestedEntity nestedItem : nestedDBList) {
            assertEquals("nested-test" + i, nestedItem.getString());
            assertNotNull(nestedItem.getId());
            i++;
        }

        assertEquals("strValue", testEntity.getMapField().get("key1"));
        assertEquals(Long.valueOf(0), testEntity.getMapField().get("key2"));
        assertNotNull(testEntity.getMapField().get("key3"));
        assertEquals(BigDecimal.valueOf(0), testEntity.getMapField().get("key4"));

        assertEquals("bytesTest", new String(newTestEntity.getBytes()));
    }

    @Test
    public void testBigDecimalConversion() throws Exception {
        Locale.setDefault(new Locale("lt", "LT"));
        Entity testEntity = createEntity();
        testEntity.setBigDecimal(new BigDecimal("123456789.255123456789"));
        DBObject object = objectMapper.dbObject(testEntity);
        String bigDecimal = (String) object.get("bigDecimal");
        assertEquals("123456789.255123456789", bigDecimal);

        BasicDBObject obj = new BasicDBObject("bigDecimal", "123456789.255123456789");
        testEntity = new Entity();
        objectMapper.entity(obj, testEntity);
        assertEquals(new BigDecimal("123456789.255123456789"), testEntity.getBigDecimal());

        try {
            obj.put("bigDecimal", "BAD FORMAT");
            // TODO what exactly should happen here? should util handle the
            // error?
            objectMapper.entity(obj, testEntity);
        } catch (Exception ex) {

        }
        assertNull(testEntity.getBigDecimal());
    }

    @Test
    public void testFromDBObjectItem() throws Exception {

        Entity testEntity = createEntity();
        DBObject dbObj = objectMapper.dbObject(testEntity);

        AnotherEntity anotherEntity = objectMapper.entity(dbObj, new AnotherEntity());

        assertEquals("test", anotherEntity.getString());
        assertEquals(new Integer(123), anotherEntity.getInteger());
        assertEquals(Boolean.TRUE, anotherEntity.getBool());

        List<String> list = (List<String>) anotherEntity.getListOfStrings();
        assertNotNull(list);
        int i = 1;
        for (Object item : list) {
            assertEquals("test" + i, (String) item);
            i++;
        }
    }

    @Test
    public void testConvertableObject() {

        final ConvertableClass listType = new ConvertableClass("");
        final ConvertableClass convertableType = new ConvertableClass("");
        converters.put(ConvertableClass.class, new TypeConverter<ConvertableClass>() {
            @Override
            public Object convertToDBValue(ConvertableClass object) {
                return "TEST_CODE";
            }

            @Override
            public ConvertableClass convertFromDBValue(Object dbValue) {
                if ("TEST_CODE".equals(dbValue)) {
                    return convertableType;
                }
                return null;
            }

            @Override
            public List<?> convertToDBList(List<ConvertableClass> objectList) {
                List<Object> dbList = new ArrayList<Object>();
                for (ConvertableClass aClass : objectList) {
                    dbList.add("TEST_LIST");
                }
                return dbList;
            }

            @Override
            public List<ConvertableClass> convertFromDBList(List<?> dbList) {
                List<ConvertableClass> list = new ArrayList<ConvertableClass>();
                for (Object obj : dbList) {
                    if ("TEST_LIST".equals(obj)) {
                        list.add(listType);
                    }
                }
                return list;
            }
        });
        objectMapper.setConverters(converters);

        Entity entity = new Entity();
        entity.setConvertableClass(convertableType);
        List<ConvertableClass> convertableList = new ArrayList<ConvertableClass>();
        convertableList.add(convertableType);
        convertableList.add(convertableType);
        entity.setConvertableList(convertableList);

        DBObject dbObject = objectMapper.dbObject(entity);
        assertEquals("TEST_CODE", dbObject.get("convertableClass"));
        assertEquals("TEST_LIST", ((List) dbObject.get("convertableList")).get(0));
        assertEquals("TEST_LIST", ((List) dbObject.get("convertableList")).get(1));

        entity = objectMapper.entity(dbObject, new Entity());

        assertEquals(convertableType, entity.getConvertableClass());
        assertEquals(listType, entity.getConvertableList().get(0));
        assertEquals(listType, entity.getConvertableList().get(1));
    }

    private Entity createEntity() {
        Entity testEntity = new Entity();
        testEntity.setString("test");
        testEntity.setInteger(new Integer(123));
        testEntity.setMyLong(new Long(1234));
        testEntity.setBool(Boolean.TRUE);
        testEntity.getListOfStrings().addAll(Arrays.asList("test1", "test2", "test3"));
        testEntity.getListOfBigDecimal().addAll(
                Arrays.asList(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")));
        testEntity.setBoolPrimitive(true);
        testEntity.setIntPrimitive(123);
        testEntity.setBigDecimal(new BigDecimal("123.1234"));
        testEntity.setTransientField("transientField");
        testEntity.setEnumField(MyEnum.ENUM_VAL1);

        List<NestedEntity> nestedList = new ArrayList<NestedEntity>();
        NestedEntity nested = new NestedEntity();
        nested.setString("nested-test1");
        nested.setId(ObjectId.get().toString());
        nestedList.add(nested);

        nested = new NestedEntity();
        nested.setString("nested-test2");
        nested.setId(ObjectId.get().toString());
        nestedList.add(nested);
        testEntity.setNestedList(nestedList);

        nested = new NestedEntity();
        nested.setString("nested-single");
        testEntity.setNestedSingle(nested);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "strValue");
        map.put("key2", Long.valueOf(0));
        map.put("key3", new Date());
        map.put("key4", BigDecimal.valueOf(0));

        testEntity.setMapField(map);

        testEntity.setBytes("bytesTest".getBytes());

        return testEntity;
    }
}
