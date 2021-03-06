package com.quinn.xmpp.ui.drawer;

import java.io.Serializable;

import org.jivesoftware.smackx.packet.VCard;

/**
 * @author Quinn
 * @date 2015-3-23
 * 
 * Vcard of the login-ing user
 */
public class UserVCard implements Serializable {
	
	public final static int FIELDS_SUM = 8;
	
	private byte[] avatarBytes;
	private String nickname;
	private String gender;
	private String email;
	private String telephone;
	private String description;
	private String jid;
	private String location;
	
	public UserVCard(){
		
	}
	
	public UserVCard(VCard vcard){
		avatarBytes = vcard.getAvatar();
		nickname = vcard.getNickName();
		email = vcard.getEmailHome();
		
	}
	
	public byte[] getAvatarBytes() {
		return avatarBytes;
	}
	public void setAvatarBytes(byte[] avatarBytes) {
		this.avatarBytes = avatarBytes;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	

	
}


