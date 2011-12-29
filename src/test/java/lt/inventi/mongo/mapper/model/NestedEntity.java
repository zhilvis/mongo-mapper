package lt.inventi.mongo.mapper.model;

public class NestedEntity {
    private String id;

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}