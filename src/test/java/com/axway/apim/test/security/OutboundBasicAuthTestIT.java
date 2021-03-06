package com.axway.apim.test.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import com.axway.apim.test.SwaggerImportTestAction;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.functions.core.RandomNumberFunction;
import com.consol.citrus.message.MessageType;

@Test(testName = "OutboundBasicAuthTest")
public class OutboundBasicAuthTestIT extends TestNGCitrusTestDesigner {

	@Autowired
	private SwaggerImportTestAction swaggerImport;

	@CitrusTest(name = "OutboundBasicAuthTest")
	public void setupDevOrgTest() {
		description("Test to validate API-Outbound-AuthN set to HTTP-Basic.");

		variable("apiNumber", RandomNumberFunction.getRandomNumber(3, true));
		variable("apiPath", "/outbound-authn-test-${apiNumber}");
		variable("apiName", "Outbound AuthN Test ${apiNumber}");

		echo("####### Importing API: '${apiName}' on path: '${apiPath}' with following settings: #######");
		createVariable("swaggerFile", "/com/axway/apim/test/files/security/petstore.json");
		createVariable("configFile", "/com/axway/apim/test/files/security/5_api_outbound-basic.json");
		createVariable("state", "unpublished");
		createVariable("expectedReturnCode", "0");
		action(swaggerImport);
		
		echo("####### No-Change test for '${apiName}' on path: '${apiPath}' #######");
		createVariable("swaggerFile", "/com/axway/apim/test/files/security/petstore.json");
		createVariable("configFile", "/com/axway/apim/test/files/security/5_api_outbound-basic.json");
		createVariable("state", "unpublished");
		createVariable("expectedReturnCode", "10");
		action(swaggerImport);

		echo("####### Validate API: '${apiName}' on path: '${apiPath}' with outbound security set to HTTP-Basic. #######");
		http().client("apiManager").send().get("/proxies").name("api").header("Content-Type", "application/json");

		http().client("apiManager").receive().response(HttpStatus.OK).messageType(MessageType.JSON)
				.validate("$.[?(@.path=='${apiPath}')].name", "${apiName}")
				.validate("$.[?(@.path=='${apiPath}')].state", "unpublished")
				.validate("$.[?(@.path=='${apiPath}')].authenticationProfiles[0].name", "_default")
				.validate("$.[?(@.path=='${apiPath}')].authenticationProfiles[0].type", "http_basic")
				.extractFromPayload("$.[?(@.path=='${apiPath}')].id", "apiId");
		
		echo("####### Change API to status published: #######");
		createVariable("swaggerFile", "/com/axway/apim/test/files/security/petstore.json");
		createVariable("configFile", "/com/axway/apim/test/files/security/5_api_outbound-basic.json");
		createVariable("state", "published");
		createVariable("expectedReturnCode", "0");
		action(swaggerImport);
		
		echo("####### Validate API: '${apiName}' on path: '${apiPath}' has status published. #######");
		http().client("apiManager").send().get("/proxies").name("api").header("Content-Type", "application/json");

		http().client("apiManager").receive().response(HttpStatus.OK).messageType(MessageType.JSON)
				.validate("$.[?(@.id=='${apiId}')].name", "${apiName}")
				.validate("$.[?(@.id=='${apiId}')].state", "published")
				.validate("$.[?(@.id=='${apiId}')].authenticationProfiles[0].name", "_default")
				.validate("$.[?(@.path=='${apiPath}')].authenticationProfiles[0].type", "http_basic")
				.extractFromPayload("$.[?(@.path=='${apiPath}')].id", "apiId");
		
		echo("####### Re-Import same API: '${apiName}' on path: '${apiPath}' with status published & API-Key (default): #######");
		createVariable("swaggerFile", "/com/axway/apim/test/files/security/petstore.json");
		createVariable("configFile", "/com/axway/apim/test/files/security/5_2_api_outbound-apikey.json");
		createVariable("state", "published");
		createVariable("expectedReturnCode", "0");
		action(swaggerImport);
		
		echo("####### Validate API: '${apiName}' on path: '${apiPath}' now configured with API-Key #######");
		http().client("apiManager").send().get("/proxies").name("api").header("Content-Type", "application/json");

		http().client("apiManager").receive().response(HttpStatus.OK).messageType(MessageType.JSON)
				.validate("$.[?(@.path=='${apiPath}')].name", "${apiName}")
				.validate("$.[?(@.path=='${apiPath}')].state", "published")
				.validate("$.[?(@.path=='${apiPath}')].authenticationProfiles[0].type", "apiKey")
				.validate("$.[?(@.path=='${apiPath}')].authenticationProfiles[0].name", "_default")
				.extractFromPayload("$.[?(@.path=='${apiPath}')].id", "apiId");
		
		echo("####### No-Change test for '${apiName}' on path: '${apiPath}' #######");
		createVariable("swaggerFile", "/com/axway/apim/test/files/security/petstore.json");
		createVariable("configFile", "/com/axway/apim/test/files/security/5_2_api_outbound-apikey.json");
		createVariable("state", "published");
		createVariable("expectedReturnCode", "10");
		action(swaggerImport);
	}


}
