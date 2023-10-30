package car.listing.tests;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import car.listing.beans.CarData;
import car.listing.tests.util.RequestResponseSpecifications;
import car.listing.utils.CommonUri;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PackageManagerTests {
    
    private static final String MODEL_PARAM = "model";

    private static final String BRAND_PARAM = "manufacturer";
    
    private final String carsUrI = String.format("%s/%s", CommonUri.ROOT_DEFAULT, CommonUri.CARS_URI);
    
    private CarData createdCarData;
    
    @BeforeEach
    public void setRequestResponseSpec() throws IOException {
        new RequestResponseSpecifications().buildRequestSpecification();
    }

    @ParameterizedTest(name="Verify creation of new car with parameters: {0}, {1}")
    @MethodSource("provideStringsForCarCreation")
    @Order(1)
    public void verifyNewCarCreation(String brand, String model) {
        Response response =
        given().
                formParam(BRAND_PARAM, brand).
                formParam(MODEL_PARAM, model).
        when().
                post(carsUrI).
        then().
                assertThat().statusCode(HttpStatus.SC_CREATED).extract().response();
        
        createdCarData = response.as(CarData.class);
        assertAll(
            () -> assertEquals(ContentType.JSON.toString(), response.getContentType().toString(), "Reposne body is not in JSON"),
            () -> assertEquals(brand, createdCarData.getManufacturer(), "Brand from query parameter, doesn`t match brand in response body"),
            () -> assertEquals(model, createdCarData.getModel(), "Model from query parameter, doesn`t match model in response body"),
            () -> assertTrue(createdCarData.getID() != 0, "Id is 0 or not present")
         );
    }
    
    
    @ParameterizedTest(name="Verify retrieval of new car with parameters: {0}")
    @MethodSource("provideStringsForCarCreation")
    @Order(2)
    public void verifyNewCarRetrieval(String brand) {
        Response response =
                        given().
                queryParams(BRAND_PARAM, brand).
        when().
                get(carsUrI).
        then().
                assertThat().statusCode(HttpStatus.SC_OK).extract().response();
        
        CarData[] responseCarDataArray = response.jsonPath().getObject("", CarData[].class);
        
        if(createdCarData == null) {
            fail("Car was not created");
        }
        
        for (int i = 0; i < responseCarDataArray.length; i++) {
            if(responseCarDataArray[i].getID() == createdCarData.getID()) {
                final String currentCarBrand = responseCarDataArray[i].getManufacturer();
                final String currentCarModel = responseCarDataArray[i].getModel();
                assertAll(
                    () -> assertEquals(createdCarData.getManufacturer(), currentCarBrand, "Car brand is not found"),
                    () -> assertEquals(createdCarData.getModel(), currentCarModel, "Car model is not found")
                );
            }else {
                fail("Car entry with ID : " + createdCarData.getID() + "is not found in the response");
            }
        }
    }
    
    private static Stream<Arguments> provideStringsForCarCreation() {
        return Stream.of(
          Arguments.of("Audi", "A6")
        );
    }
}
