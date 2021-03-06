package com.quinn.xmpp;

import java.io.Serializable;

import com.quinn.xmpp.ui.contacts.ContactsDataItem;
import com.quinn.xmpp.ui.drawer.UserVCard;

import android.content.ComponentName;
import android.content.Intent;

public class Intents {

	public static final String INTENT_PREFIX = "com.quinn.xmpp.";
	public static final String INTENT_EXTRA_PREFIX = INTENT_PREFIX + "extra.";
	
	public static final String EXTRA_RESULT = INTENT_PREFIX + "START_FOR_RESULT";
	public static final String EXTRA_RESULT_ACCOUNT = INTENT_PREFIX + "ACCOUNT";
	public static final String EXTRA_RESULT_PASSWORD = INTENT_PREFIX + "PASSWORD";
	
	
	public static final String EXTRA_VCARD = INTENT_EXTRA_PREFIX + "vcard";
	public static final String EXTRA_JID_CHATTING_WITH_WHO = INTENT_EXTRA_PREFIX + "jid";
	public static final String EXTRA_NICKNAME_CHATTING_WITH_WHO = INTENT_EXTRA_PREFIX + "nickname";
	public static final String EXTRA_SERVICE_CHATTING_WITH_WHO = INTENT_EXTRA_PREFIX + "service";
	public static final String EXTRA_JID = INTENT_EXTRA_PREFIX + "jid";
	
	
	public static class NotificationAction{
		public static final String SUBSCRIBE_ACTION = "SUBSCRIBE_ACTION";
		
	}
	
	
	public static class Builder {
		
		
		private final Intent intent;

		
		public Builder(){
			intent = new Intent();
		}
		
		public Builder(String actionSuffix) {
			intent = new Intent(INTENT_PREFIX + actionSuffix);
		}
		
		public Builder addVcard(UserVCard vcard){
			intent.putExtra(EXTRA_VCARD, vcard);
			return this;
		}
		
		public Builder addJID(String jid){
			intent.putExtra(EXTRA_JID, jid);
			return this;
		}
		
		public Builder add(String keyNanme, String value){
			intent.putExtra(keyNanme, value);
			return this;
		}
		
		public Builder add(String keyName, Boolean value){
			intent.putExtra(keyName, value);
			return this;
		}
		
		public Builder add(String keyName, int value){
			intent.putExtra(keyName, value);
			return this;
		}
		
		public Builder add(String keyName, Serializable value) {
            intent.putExtra(keyName, value);
            return this;
        }
		
		public Intent toIntent(){
			return intent;
		}

	}
}
