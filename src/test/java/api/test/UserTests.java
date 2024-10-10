package api.test;

import static org.testng.Assert.ARRAY_MISMATCH_TEMPLATE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import api.endpoints.UserEndPoints;
import api.payload.User;
import io.restassured.response.Response;

public class UserTests {
	
	Faker faker;
	User userPayload;
	
	public Logger logger;
	
	@BeforeClass
	public void setupData()
	{
		faker=new Faker();
		userPayload=new User();
		
		userPayload.setId(faker.idNumber().hashCode());
		userPayload.setUsername(faker.name().username());
		userPayload.setFirstName(faker.name().firstName());
		userPayload.setLastName(faker.name().lastName());
		userPayload.setEmail(faker.internet().safeEmailAddress());
		userPayload.setPassword(faker.internet().password(5, 10));
		userPayload.setPhone(faker.phoneNumber().cellPhone());
		
		//Logs
		logger = LogManager.getLogger(this.getClass());
	}
	
	@Test(priority=1)
	public void testPostUser()
	{
		logger.info("***************  Creating User  *********************");
		Response response=UserEndPoints.createUser(userPayload);
		response.then().log().all()
		.statusCode(200);
		
		Assert.assertEquals(response.getStatusCode(), 200);
		
		
	}
	@Test(priority=2)
	public void testGetUserByName()
	{
		logger.info("***************  Getting User Info  *********************");
		Response response=UserEndPoints.readUser(this.userPayload.getUsername());
		response.then().log().all();
		Assert.assertEquals(response.statusCode(), 200);
		
		logger.info("***************  User Info Is Displayed  *********************");
		
	}
	@Test(priority=3)
	public void testUpdateUserByName()
	{
		logger.info("***************  Update User Info  *********************");
		Response originalResponse = UserEndPoints.readUser(this.userPayload.getUsername());
	    originalResponse.then().log().all();
	    
	    String originalFirstName = originalResponse.jsonPath().getString("firstName");
	    String originalLastName = originalResponse.jsonPath().getString("lastName");
	    String originalEmail = originalResponse.jsonPath().getString("email");
		
		//Updating Data using the same pay load.
		userPayload.setFirstName(faker.name().firstName());
		userPayload.setLastName(faker.name().lastName());
		userPayload.setEmail(faker.internet().safeEmailAddress());
		
		Response response=UserEndPoints.updateUser(this.userPayload.getUsername(),userPayload);
		response.then().log().body().statusCode(200);
		
		Assert.assertEquals(response.getStatusCode(), 200);
		
		logger.info("***************  Confirming User Info Is Updated  *********************");
		//Checking the data after update
		Response responseAfterUpdate=UserEndPoints.readUser(this.userPayload.getUsername());
		response.then().log().all();
		Assert.assertNotEquals(responseAfterUpdate.jsonPath().getString("firstName"), originalFirstName);
	    Assert.assertNotEquals(responseAfterUpdate.jsonPath().getString("lastName"), originalLastName);
	    Assert.assertNotEquals(responseAfterUpdate.jsonPath().getString("email"), originalEmail);
	}
	
	@Test(priority=4)
	public void testDeleteUserByName()
	{
		logger.info("***************  Deleting User  *********************");
		Response response=UserEndPoints.deleteUser(this.userPayload.getUsername());
				response.then().log().all();
		
		Assert.assertEquals(response.statusCode(), 200);
		
	}

}
