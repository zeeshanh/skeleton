package api;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.tables.records.TagsRecord;

public class TagResponse {
    @JsonProperty
    Integer receipt;

    @JsonProperty
    String tag;

    public TagResponse(TagsRecord dbRecord) {
        this.receipt = dbRecord.getReceipt();
        this.tag = dbRecord.getTag();
    }
}