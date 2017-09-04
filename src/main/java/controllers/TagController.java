package controllers;


import api.ReceiptResponse;
import dao.ReceiptDao;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/tag/{tag}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagController {

    final TagDao tags_record;

    public TagController(TagDao tags_record) {
        this.tags_record = tags_record;
    }

    @PUT
    public void toggleTag(@PathParam("tag") String tagName, int id) {
        if(!tags_record.existsTag(tagName, id)){
            tags_record.addTag(tagName, id);
        }
        else{
            tags_record.deleteTag(tagName, id);
        }

    }

    @GET
    public List<ReceiptResponse> getTag(@PathParam("tag") String tag){
        List<ReceiptsRecord> receipts = tags_record.getReceipts(tag);
        return receipts.stream().map(ReceiptResponse::new).collect(toList());
    }

}
