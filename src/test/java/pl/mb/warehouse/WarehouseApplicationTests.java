package pl.mb.warehouse;

import io.restassured.http.ContentType;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import pl.mb.warehouse.application.CreateDatasetCommand;
import pl.mb.warehouse.domain.DataAggregationType;
import pl.mb.warehouse.domain.VirtualColumnOperationType;
import pl.mb.warehouse.domain.dto.AggregationData;
import pl.mb.warehouse.domain.dto.AnalyticQuery;
import pl.mb.warehouse.domain.dto.DateRange;
import pl.mb.warehouse.domain.dto.Filter;
import pl.mb.warehouse.domain.dto.PlainQuery;
import pl.mb.warehouse.domain.dto.VirtualColumnDto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WarehouseApplicationTests {

	@LocalServerPort
	public int webServerPort;

	@Test
	public void mainTest() {
		val datasetName = "testDataset";

		given()
				.contentType(ContentType.JSON)
				.when()
				.body(new CreateDatasetCommand(
						datasetName,
						"http://adverity-challenge.s3-website-eu-west-1.amazonaws.com/PIxSyyrIKFORrCXfMYqZBI.csv"))
				.post(url("/warehouse/datasets"))
				.then()
				.assertThat().statusCode(HttpStatus.OK.value())
				.assertThat().body(Matchers.notNullValue())
				.assertThat().body("name", equalTo(datasetName));

		given()
				.contentType(ContentType.JSON)
				.when()
				.body(new PlainQuery(
						datasetName,
						Set.of("Datasource","Impressions", "Daily"),
						new Filter(
								Map.of("Datasource", Set.of("Google Ads")),
								Map.of("Daily", new DateRange(LocalDate.of(2019, 11, 20), LocalDate.of(2019, 11, 30)))
						)
				))
				.post(url("/warehouse/datasets/plainQuery"))
				.then()
				.assertThat().statusCode(HttpStatus.OK.value())
				.assertThat().body("[0][0]", equalTo("Datasource"))
				.assertThat().body("[0][1]", equalTo("Impressions"))
				.assertThat().body("[0][2]", equalTo("Daily"))
                .assertThat().body("[1][0]", equalTo("Google Ads"))
                .assertThat().body("[1][1]", equalTo("27841"))
                .assertThat().body("[1][2]", equalTo("2019-11-20"));
				;

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(new AnalyticQuery(
                        datasetName,
                        null,
                        new AggregationData(DataAggregationType.MIN, "Impressions", Set.of("Campaign")),
                        new Filter(
                                Map.of("Datasource", Set.of("Google Ads"), "Campaign", Set.of("Adventmarkt Touristik")),
                                null
                        )
                ))
                .post(url("/warehouse/datasets/analyticQuery"))
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().body("[1][0]", equalTo("Adventmarkt Touristik"))
                .assertThat().body("[1][1]", equalTo("7705.0"));


        given()
                .contentType(ContentType.JSON)
                .when()
                .body(new AnalyticQuery(
                        datasetName,
                        new VirtualColumnDto(
                            "CTR",
                            "Clicks",
                            "Impressions",
                            VirtualColumnOperationType.DIVISION
                        ),
                        new AggregationData(DataAggregationType.AVG, "CTR", Set.of("Datasource", "Campaign")),
                        null
                ))
                .post(url("/warehouse/datasets/analyticQuery"))
                .then()
                .assertThat().statusCode(HttpStatus.OK.value())
                .assertThat().body("[1][0]", equalTo("Google Ads"))
                .assertThat().body("[1][1]", equalTo("Adventmarkt Touristik"))
                .assertThat().body("[1][2]", equalTo("0.002907399087444901"));

    }

	private String url(String path) {
		return "http://127.0.0.1:" + webServerPort + path;
	}

}
