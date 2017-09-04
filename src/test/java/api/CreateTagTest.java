package api;

import controllers.TagController;
import dao.TagDao;
import io.dropwizard.jersey.validation.Validators;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.validation.Validator;

public class CreateTagTest {
    private final Validator validator = Validators.newValidator();

    @Rule public MockitoRule rule = MockitoJUnit.rule();
    @Mock public TagDao tDao;

    @Test
    public void testgetTag(){
        MockitoAnnotations.initMocks(this);
        TagController tg = new TagController(tDao);

        tg.getTag("abcfd");
        Mockito.verify(tDao).getReceipts("abcfd");


    }

    @Test
    public void testToggleTag(){
        MockitoAnnotations.initMocks(this);
        TagController tg = new TagController(tDao);

        tg.toggleTag("abcd", 2);
        Mockito.verify(tDao).existsTag("abcd", 2);
    }
}


//https://examples.javacodegeeks.com/core-java/mockito/mockito-mock-database-connection-example/