package edu.wit.alr.database.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Email;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import edu.wit.alr.Config;

public abstract class Contact {

//	======================================== ============= ======================================== \\
//	======================================== Phone Contact ======================================== \\

	@Embeddable
	public static class PhoneContact extends Contact {
		private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();
		
		@Column(length = 15)
		private String phone; // E.164
		
		// no-agrs constructor
		PhoneContact() { }
		
		public PhoneContact(String number) {
			this.setPhoneNumber(number);
		}
		
		public void setPhoneNumber(String number) {
			try {
				PhoneNumber phoneNumber = PHONE_UTIL.parse(number, Config.getDefaulCountryCode());
				this.phone = PHONE_UTIL.format(phoneNumber, PhoneNumberFormat.E164);
				
			} catch (NumberParseException e) {
				throw new IllegalArgumentException("Invalid Phone-Number", e);
			}
		}
		
		public String getPhoneNumber() { return phone; }
		
		public String toString() {
			try {
				if(phone == null) {
					return null;
				}
				
				PhoneNumber phoneNumber = PHONE_UTIL.parse(this.phone, Config.getDefaulCountryCode());
				return PHONE_UTIL.format(phoneNumber, PhoneNumberFormat.NATIONAL);
				
			} catch (NumberParseException e) {
				throw new IllegalArgumentException("Invalid Phone-Number", e);
			}
		}
	}


//	======================================== ============= ======================================== \\
//	======================================== Email Contact ======================================== \\
	
	@Embeddable
	public static class EmailContact extends Contact {
		
		@Email
		@Column(length = 255)
		private String email;
		
		// no-agrs constructor
		EmailContact() { }
		
		public EmailContact(String email) {
			this.setEmail(email);
		}

		public void setEmail(String email) {
			// TODO: validate email
			this.email = email;
		}
		
		public String getEmail() { return email; }
		
		public String toString() {
			return email;
		}
	}
}
