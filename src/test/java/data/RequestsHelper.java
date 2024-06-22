package data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RequestsHelper {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
        .setBaseUri("http://localhost")
        .setPort(9999)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build();

    private static void sendLoginRequest(DataHelper.UserInfo user) {
        given()
            .spec(requestSpec)
            .body(user)
            .when()
            .post("/api/auth")
            .then()
            .statusCode(200);
    }

    private static String sendVerifyRequest(String code, String username) {
        Response response = given()
            .spec(requestSpec)
            .body(DataHelper.getCorrectVerification(code, username))
            .when()
            .post("/api/auth/verification")
            .then()
            .statusCode(200)
            .extract()
            .response();

        String responseBody = response.getBody().asString();
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

        return jsonResponse.get("token").getAsString();
    }

    public static void transferMoney(String cardFrom, String cardTo, int amount, String token) {
        Response response = given()
            .spec(requestSpec)
            .auth().oauth2(token)
            .body(DataHelper.TransferInfo.createTransferInfo(cardFrom, cardTo, amount))
            .when()
            .post("/api/transfer")
            .then()
            .statusCode(200)
            .extract()
            .response();

    }

    public static void transferMoneyWithoutAmount(String cardFrom, String cardTo,  String token) {
        Response response = given()
            .spec(requestSpec)
            .auth().oauth2(token)
            .body(DataHelper.TransferWithoutBalanceInfo.createTransferWithoutBalanceInfo(cardFrom, cardTo))
            .when()
            .post("/api/transfer")
            .then()
            .statusCode(400)
            .extract()
            .response();

    }

    public static void transferMoneyWithError(String cardFrom, String cardTo, int amount, String token) {
        Response response = given()
            .spec(requestSpec)
            .auth().oauth2(token)
            .body(DataHelper.TransferInfo.createTransferInfo(cardFrom, cardTo, amount))
            .when()
            .post("/api/transfer")
            .then()
            .statusCode(400)
            .extract()
            .response();

    }

    public static String loginUser(DataHelper.UserInfo user) {
        sendLoginRequest(user);
        return sendVerifyRequest(user.getLogin(), SQLHelper.getCode());
    }
}
