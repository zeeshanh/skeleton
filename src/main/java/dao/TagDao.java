package dao;

import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.impl.DSL;

import javax.lang.model.type.NullType;
import javax.print.DocFlavor;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;

public class TagDao {

    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int addTag(String tag, int receipt_id){
        TagsRecord tr = dsl
                .insertInto(TAGS, TAGS.TAG, TAGS.RECEIPT)
                .values(tag, receipt_id)
                .returning(TAGS.ID)
                .fetchOne();

        checkState(tr != null && tr.getId() != null, "Insert failed");

        return tr.getId();
    }

    public boolean existsTag(String tag, int receipt_id){
        List<TagsRecord> tr = dsl
                .selectFrom(TAGS)
                .where(TAGS.TAG.eq(tag).and(TAGS.ID.eq(receipt_id)))
                .fetch();

        return tr.size() > 0;

    }

    public void deleteTag(String tag, int receipt_id){
        dsl
                .delete(TAGS)
                .where(TAGS.TAG.eq(tag))
                .and(TAGS.RECEIPT.eq(receipt_id))
                .execute();

    }

    public List<ReceiptsRecord> getReceipts(String tag) {

        List<ReceiptsRecord> records= dsl
                .selectFrom(RECEIPTS
                        .join(TAGS)
                        .on(RECEIPTS.ID.eq(TAGS.RECEIPT)))
                        .where(TAGS.TAG.eq(tag))
                .fetchInto(RECEIPTS);


        //System.out.println(records.toString());
        return records;
    }

}
