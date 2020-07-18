package edu.wit.alr.web.security.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.wit.alr.database.model.Account;
import edu.wit.alr.database.util.PasswordSpec;
import edu.wit.alr.services.AuthRedirectService;
import edu.wit.alr.services.SignupService;

@Controller
public class LoginController {
	
	@Autowired private SignupService service;
	@Autowired private AuthRedirectService redirectData;
	@Autowired private PasswordEncoder encoder;
	
	@GetMapping("login")
	public String loginPage() {
		return "login";
	}
	
	@GetMapping("signup")
	public String signupPage(HttpServletRequest request) {
		String data = redirectData.getRequestData(request);
		service.startSignup(Integer.valueOf(data));
		return "signup";
	}
	
	@PostMapping("signup")
	public String signup(@RequestParam("username") String username, @RequestParam("password") String password) {
		String hash = encoder.encode(password);
		// TODO: add error checks
		
		Account account = service.getCurrentSignupAccount();
		account.withLocalAuthentication(username, new PasswordSpec(hash, null));
		service.completeSignup(account);
		
		return "redirect:/";
	}
}
