import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:4004";
    }



    @Test
    public void shouldReturnPatientsWithValidToken() {

        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
                """;

        // esto es el body que se envia al endpoint de login para obtener el token
        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login") // esto es para hacer login y obtener el token
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token"); //esto es para extraer la respuesta del login


        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue());

        //TODO: extraer la respuesta para mostrar la lista de pacientes
    }
}
