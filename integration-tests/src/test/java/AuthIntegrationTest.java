import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }


    @Test
    public void shouldReturnOKWithValidToken() {
        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
                """;

        // esto es el body que se envia al endpoint de login para obtener el token
        Response response = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login") // esto es para hacer login y obtener el token
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response(); //esto es para extraer la respuesta del login


        System.out.println("Generated Token: " + response.jsonPath().getString("token") );
    }




    @Test
    public void shouldReturnUnauthorizedOnInvalidLogin() {
        String loginPayload = """
                {
                    "email": "invalid_user_test@test.com",
                    "password": "wrongpassword"
                }
                """;

        // esto es el body que se envia al endpoint de login para obtener el token
        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login") // esto es para hacer login y obtener el token
                .then()
                .statusCode(401);
    }
}
