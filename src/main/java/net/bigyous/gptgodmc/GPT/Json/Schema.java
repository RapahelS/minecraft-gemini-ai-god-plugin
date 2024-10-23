package net.bigyous.gptgodmc.GPT.Json;

import com.google.gson.annotations.SerializedName;

import net.bigyous.gptgodmc.utils.GPTUtils;

import java.util.List;
import java.util.Map;

// https://ai.google.dev/api/caching#Schema
public class Schema {

    // Enum for Type field as per OpenAPI 3.0 spec
    public enum Type {
        @SerializedName("TYPE_UNSPECIFIED") TYPE_UNSPECIFIED,
        @SerializedName("STRING") STRING,
        @SerializedName("NUMBER") NUMBER,
        @SerializedName("INTEGER") INTEGER,
        @SerializedName("BOOLEAN") BOOLEAN,
        @SerializedName("ARRAY") ARRAY,
        @SerializedName("OBJECT") OBJECT
    }

    // creates the "root" schema defining a function
    // takes in a list of parameter names to parameter type definitions of Schema(Type type)
    public Schema(Map<String, Schema> properties) {
        this.type = Type.OBJECT;
        this.properties = properties;
    }

    // creates the "root" schema defining a function
    // takes in a list of parameter names to parameter type definitions of Schema(Type type) and which are required
    public Schema(Map<String, Schema> properties, List<String> requiredFields) {
        this.type = Type.OBJECT;
        this.properties = properties;
        this.requiredFields = requiredFields;
    }

    // for parameters and such
    public Schema(Type type) {
        this.type = type;
    }

    // parameter with description
    public Schema(Type type, String description) {
        this.type = type;
        this.description = description;
    }

     // parameter with description and subtype (for arrays)
    public Schema(Type type, String description, Type subType) {
        this.type = type;
        this.description = description;
        this.items = new Schema(subType);
    }

    // Required field: type
    @SerializedName("type")
    private Type type;

    // Optional fields
    @SerializedName("format")
    private String format;

    @SerializedName("description")
    private String description;

    @SerializedName("nullable")
    private Boolean nullable;

    @SerializedName("enum")
    private List<String> enumValues;

    @SerializedName("maxItems")
    private String maxItems;

    @SerializedName("minItems")
    private String minItems;

    // For Type.OBJECT: map of properties.
    // Optional. Properties of Type.OBJECT.
    @SerializedName("properties")
    private Map<String, Schema> properties;

    // List of required fields for Type.OBJECT
    // Optional. Required properties of Type.OBJECT.
    @SerializedName("required")
    private List<String> requiredFields;

    // For Type.ARRAY: schema of items in the array
    @SerializedName("items")
    private Schema items;

    // Getters and Setters

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    public String getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(String maxItems) {
        this.maxItems = maxItems;
    }

    public String getMinItems() {
        return minItems;
    }

    public void setMinItems(String minItems) {
        this.minItems = minItems;
    }

    public Map<String, Schema> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Schema> properties) {
        this.properties = properties;
    }

    public List<String> getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(List<String> requiredFields) {
        this.requiredFields = requiredFields;
    }

    public Schema getItems() {
        return items;
    }

    public void setItems(Schema items) {
        this.items = items;
    }

    public int calculateParameterTokens(){
        int sum = 0;
        for(Schema param: properties.values()){
            sum+= GPTUtils.countTokens(param.getType().toString()) + GPTUtils.countTokens(param.getDescription());
        }
        return sum;
    }
}
