package com.mroads.acyuta.gmailapi;

import lombok.Data;

@Data
@SuppressWarnings("squid:S1068")
public class UserInfo {
	
	
	public UserInfo() {
		super();
	}
	private  String id;
    private  String name;
    private  String given_name;
    private  String family_name;
    private  String gender;
    private  String picture;
    private  String link;
    private  String email;
    private  String verified_email;
    private  String hd;

 
}
