package car.listing.tests.util;

import car.listing.utils.CommonUri;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

public class RequestResponseSpecifications {
    
    /**
     * Specify how the request will look like: base URL, base path, headers, content type, etc.
     */
    public void buildRequestSpecification(){
        //Log Request and Response data
        RestAssured.filters(RequestLoggingFilter.with(LogDetail.ALL), new ResponseLoggingFilter(LogDetail.ALL));

        //Build Request Specification
        RestAssured.requestSpecification = new RequestSpecBuilder().
                setBaseUri(CommonUri.ROOT_DEFAULT).
                setUrlEncodingEnabled(false).
                setContentType(ContentType.JSON).setAccept(ContentType.JSON).build();
    }
}
