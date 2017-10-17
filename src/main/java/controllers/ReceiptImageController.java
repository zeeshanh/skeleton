package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import javax.validation.constraints.Null;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.h2.util.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import static java.lang.System.out;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {

    static class AnnotationComparator implements Comparator<EntityAnnotation>
    {
        public AnnotationComparator(){

        }

        public int compare(EntityAnnotation e1, EntityAnnotation e2)
        {
            BoundingPoly p1 = e1.getBoundingPoly();
            BoundingPoly p2 = e2.getBoundingPoly();

            double y1 = p1.getVertices(0).getY();
            double y2 = p2.getVertices(0).getY();

            double x1 = p1.getVertices(0).getX();
            double x2 = p2.getVertices(0).getX();

            if (Double.compare(y1, y2) != 0){
                return Double.compare(y1, y2);
            }

            //out.printf()
            return Double.compare(x2, x1);
        }
    }

    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);
            boolean gotMerchant = false;
            String merchantName = "here";
            BigDecimal amount = new BigDecimal(10);

            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount
            List<EntityAnnotation> extracts = new ArrayList<EntityAnnotation>();
            for(EntityAnnotation annotation: res.getTextAnnotationsList()){
                extracts.add(annotation);
            }
            extracts.remove(0);
            //Collections.sort(extracts, new AnnotationComparator());

            for (EntityAnnotation annotation : extracts) {

                out.printf("Position : %s\n", annotation.getBoundingPoly());
                out.printf("Text: %s\n", annotation.getDescription());

                if(!annotation.getDescription().contains(".") && !gotMerchant){
                    merchantName = annotation.getDescription();
                    gotMerchant = true;
                }

                if(annotation.getDescription().contains(".")){
                    String temp = annotation.getDescription();
                    if (temp.charAt(0)=='$'){
                        temp = temp.substring(1);
                    }
                    out.printf(temp);
                    amount = new BigDecimal(temp);
                }
            }

            //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
            return new ReceiptSuggestionResponse(merchantName, amount);
        }

        catch (Exception e){
            return new ReceiptSuggestionResponse("HTTP 500: Server error", new BigDecimal(0));
        }
    }
}
