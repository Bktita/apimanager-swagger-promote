package com.axway.apim.swagger.api.properties.applications;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientApplication {
	private String id;
	private String name;
	private String oauthClientId;
	private String extClientId;
	private String apiKey;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOauthClientId() {
		return oauthClientId;
	}
	public void setOauthClientId(String oauthClientId) {
		this.oauthClientId = oauthClientId;
	}
	public String getExtClientId() {
		return extClientId;
	}
	public void setExtClientId(String extClientId) {
		this.extClientId = extClientId;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null) return false;
		if(other instanceof ClientApplication) {
			return StringUtils.equals(((ClientApplication)other).getId(), this.getId());
		}
		return false;
	}
	@Override
	public String toString() {
		return "[" + name + "]";
	}
}