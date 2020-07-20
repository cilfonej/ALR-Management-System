package edu.wit.alr.services;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import edu.wit.alr.database.model.Account;
import edu.wit.alr.database.model.AuthorizedRedirect;
import edu.wit.alr.database.model.Person;
import edu.wit.alr.database.repository.AccountRepository;
import edu.wit.alr.database.repository.AuthorizedRedirectRepository;

@Service
public class SignupService {
	
	@Autowired private AccountRepository repository;
	@Autowired private AuthorizedRedirectRepository redirectRepository;
	
	@Autowired
	private InvitationData invitaion;
	
	public void generateInvitation(Person person) {
		// TODO: check if person has account already
		//			if so,
		//				if registered, error
		//				else generate a new signup-link
		
		Account account = new Account(person);
		repository.save(account);

		AuthorizedRedirect redirect = new AuthorizedRedirect(account, "/signup");
		redirect.setExpiration(null); // link is only valid once
		redirect.addPermittedResource("/signup");
		redirect.setRequestData(String.valueOf(account.getId()));
		redirectRepository.save(redirect);
		
		// TODO: send invitation email
	}
	
	public void startSignup(int accountID) {
		// TODO: implement method
		invitaion.accountId = accountID;
		// TODO: validate user has not signed-up 
	}
	
	public Account getCurrentSignupAccount() {
		// if there's no currently loaded invitation, return null;
		if(invitaion == null || invitaion.accountId == null)
			return null;
		
		// attempt to find the account specified in the invitation
		return repository.findById(invitaion.accountId).orElse(null);
		
//		return repository.findAll().iterator().next();
	}
	
	public void completeSignup(Account changed) {
		// clear the invitation
		invitaion.accountId = null;
		
		// save the modified Account
		repository.save(changed);
	}
	
	
	@Component
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	static class InvitationData implements Serializable {
		private static final long serialVersionUID = 4952348831056309189L;
		
		private Integer accountId;
	}
}
