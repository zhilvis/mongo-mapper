package lt.inventi.mongo.mapper.model;

public class ValueObject {

    private String stringValue;
    private Integer intValue;

    public ValueObject() {
    }

    public ValueObject(String stringValue, Integer intValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public String toString() {
        return stringValue + intValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((intValue == null) ? 0 : intValue.hashCode());
        result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ValueObject other = (ValueObject) obj;
        if (intValue == null) {
            if (other.intValue != null)
                return false;
        } else if (!intValue.equals(other.intValue))
            return false;
        if (stringValue == null) {
            if (other.stringValue != null)
                return false;
        } else if (!stringValue.equals(other.stringValue))
            return false;
        return true;
    }

}
