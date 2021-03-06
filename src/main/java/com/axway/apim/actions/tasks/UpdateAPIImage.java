package com.axway.apim.actions.tasks;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

import com.axway.apim.actions.rest.POSTRequest;
import com.axway.apim.actions.rest.RestAPICall;
import com.axway.apim.actions.rest.Transaction;
import com.axway.apim.lib.AppException;
import com.axway.apim.lib.ErrorCode;
import com.axway.apim.swagger.api.APIImportDefinition;
import com.axway.apim.swagger.api.IAPIDefinition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateAPIImage extends AbstractAPIMTask implements IResponseParser {

	public UpdateAPIImage(IAPIDefinition desiredState, IAPIDefinition actualState) {
		super(desiredState, actualState);
	}
	public void execute() throws AppException {
		if(!desiredState.getImage().isValid()) {
			LOG.info("No image configured, doing nothing.");
			return;
		}
		LOG.info("Updating API-Image from: " + desiredState.getImage().getFilename());
		
		URI uri;
		HttpEntity entity;
		
		Transaction context = Transaction.getInstance();
		
		try {
			uri = new URIBuilder(cmd.getAPIManagerURL()).setPath(RestAPICall.API_VERSION+"/proxies/"+actualState.getId()+"/image").build();
			
			entity = MultipartEntityBuilder.create()
					.addBinaryBody("file", ((APIImportDefinition)this.desiredState).getImage().getInputStream(), ContentType.create("image/jpeg"), desiredState.getImage().getFilename())
					.build();
			
			RestAPICall apiCall = new POSTRequest(entity, uri, this);
			apiCall.setContentType(null);
			apiCall.execute();
		} catch (Exception e) {
			throw new AppException("Can't update API-Image.", ErrorCode.UNXPECTED_ERROR, e);
		}
	}
	@Override
	public JsonNode parseResponse(HttpResponse httpResponse) throws AppException {
		String response = null;
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = null;
		try {
			response = EntityUtils.toString(httpResponse.getEntity());
			jsonNode = objectMapper.readTree(response);
			String status = jsonNode.findPath("status").asText();
		} catch (IOException e) {
			throw new AppException("Cannot parse JSON-Payload for create API-Proxy.", ErrorCode.CANT_CREATE_API_PROXY, e);
		}
		return jsonNode;
	}
}
