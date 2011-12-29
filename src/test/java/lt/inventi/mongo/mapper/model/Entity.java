package lt.inventi.mongo.mapper.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Entity extends ParentEntity {

    private String id;
    private List<NestedEntity> nestedList;
    private NestedEntity nestedSingle;

    private String string;
    private Integer integer;
    private Long myLong;
    private Boolean bool;
    private List<String> listOfStrings = new ArrayList<String>();

    private List<BigDecimal> listOfBigDecimal = new ArrayList<BigDecimal>();

    private boolean boolPrimitive;
    private int intPrimitive;

    private transient String transientField;
    private BigDecimal bigDecimal;

    MyEnum enumField;

    private Map<String, Object> mapField;

    private ConvertableClass convertableClass;
    private List<ConvertableClass> convertableList;

    private byte[] bytes;

    public void setListOfStrings(List<String> listOfStrings) {
        this.listOfStrings = listOfStrings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public List<String> getListOfStrings() {
        return listOfStrings;
    }

    public boolean isBoolPrimitive() {
        return boolPrimitive;
    }

    public void setBoolPrimitive(boolean boolPrimitive) {
        this.boolPrimitive = boolPrimitive;
    }

    public int getIntPrimitive() {
        return intPrimitive;
    }

    public void setIntPrimitive(int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public List<NestedEntity> getNestedList() {
        return nestedList;
    }

    public void setNestedList(List<NestedEntity> nestedList) {
        this.nestedList = nestedList;
    }

    public String getTransientField() {
        return transientField;
    }

    public void setTransientField(String transientField) {
        this.transientField = transientField;
    }

    public MyEnum getEnumField() {
        return enumField;
    }

    public void setEnumField(MyEnum enumField) {
        this.enumField = enumField;
    }

    public List<BigDecimal> getListOfBigDecimal() {
        return listOfBigDecimal;
    }

    public void setListOfBigDecimal(List<BigDecimal> listOfBigDecimal) {
        this.listOfBigDecimal = listOfBigDecimal;
    }

    public NestedEntity getNestedSingle() {
        return nestedSingle;
    }

    public void setNestedSingle(NestedEntity nestedSingle) {
        this.nestedSingle = nestedSingle;
    }

    public Long getMyLong() {
        return myLong;
    }

    public void setMyLong(Long myLong) {
        this.myLong = myLong;
    }

    public Map<String, Object> getMapField() {
        return mapField;
    }

    public void setMapField(Map<String, Object> mapField) {
        this.mapField = mapField;
    }

    public ConvertableClass getConvertableClass() {
        return convertableClass;
    }

    public void setConvertableClass(ConvertableClass convertableClass) {
        this.convertableClass = convertableClass;
    }

    public List<ConvertableClass> getConvertableList() {
        return convertableList;
    }

    public void setConvertableList(List<ConvertableClass> convertableList) {
        this.convertableList = convertableList;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}