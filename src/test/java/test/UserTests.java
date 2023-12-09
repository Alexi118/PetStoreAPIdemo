package test;

import com.github.javafaker.Faker;
import endpoints.UserEndPoints;
import io.restassured.response.Response;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import payload.User;

public class UserTests {
    Faker faker;
    User userPayload;

    @BeforeClass
    public void setUp(){
        faker = new Faker();
        userPayload = new User();

        userPayload.setId(faker.idNumber().hashCode());
        userPayload.setUsername(faker.name().username());
        userPayload.setFirstName(faker.name().firstName());
        userPayload.setLastName(faker.name().lastName());
        userPayload.setEmail(faker.internet().safeEmailAddress());
        userPayload.setPassword(faker.internet().password(5,10));
        userPayload.setPhone(faker.phoneNumber().cellPhone());

        System.out.println(userPayload.printPayload());
    }

    @Test(priority = 1)
    public void testPOSTCreateUserByName(){
        Response response = UserEndPoints.createUser(userPayload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(),200);
        Assert.assertEquals(response.getBody().jsonPath().get("type"),"unknown");
    }
    @Test(priority = 2)
    public void testGETReadUserByName(){
        Response response = UserEndPoints.readUser(this.userPayload.getUsername());
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(),200);

        //Verify Data is the same as in payload on CREATE
        Assert.assertEquals(userPayload.printPayload(), response.getBody().asString());
     }
    @Test(priority = 3)
    public void testPUTUpdateUserByName(){
        //Update user data
        userPayload.setFirstName(faker.name().firstName());
        userPayload.setLastName(faker.name().lastName());
        userPayload.setEmail(faker.internet().safeEmailAddress());

        Response response = UserEndPoints.updateUser(userPayload,this.userPayload.getUsername());
        response.then().log().body();
        Assert.assertEquals(response.getStatusCode(),200);

        //Verify user data is updated
        Response responseAfterUpdated = UserEndPoints.readUser(this.userPayload.getUsername());
        responseAfterUpdated.then().log().all();
        Assert.assertEquals(responseAfterUpdated.getStatusCode(),200);
    }
    @Test(priority = 4)
    public void testDELETEdeleteUserByName(){
        Response response = UserEndPoints.deleteUser(this.userPayload.getUsername());
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(),200);

        //Verify User is deleted
        Response responseAfterUpdated = UserEndPoints.readUser(this.userPayload.getUsername());
        responseAfterUpdated.then().log().all();
        Assert.assertEquals(responseAfterUpdated.getStatusCode(),404);
        Assert.assertEquals(responseAfterUpdated.getBody().jsonPath().get("type"),"error");
        Assert.assertEquals(responseAfterUpdated.getBody().jsonPath().get("message"),"User not found");
    }
}
