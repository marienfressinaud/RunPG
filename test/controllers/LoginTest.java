package controllers;

import org.junit.*;

import static org.junit.Assert.*;

import play.mvc.*;
import play.test.*;
import static play.test.Helpers.*;

import com.google.common.collect.ImmutableMap;

public class LoginTest extends WithApplication {
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void authenticateSuccess() {
		Result result = callAction(
			controllers.routes.ref.Application.authenticate(),
			fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
				"pseudo", "Bob",
				"password", "bobsecret"
			))
		);
		assertEquals(303, status(result));
		assertEquals("Bob", session(result).get("pseudo"));
	}
	
	@Test
	public void authenticated() {
		Result result = callAction(
			controllers.routes.ref.Application.classement(),
			fakeRequest().withSession("pseudo", "Julien")
		);
		assertEquals(200, status(result));
	}    

	@Test
	public void notAuthenticated() {
		Result result = callAction(
			controllers.routes.ref.Jeu.profil(),
			fakeRequest()
		);
		assertEquals(303, status(result));
		assertEquals("/login", header("Location", result));
	}
	
	@Test
	public void authAdmin() {
		Result result = callAction(
			controllers.routes.ref.Admin.index(),
			fakeRequest().withSession("pseudo", "Marien")
		);
		assertEquals(200, status(result));

		result = callAction(
			controllers.routes.ref.Admin.index(),
			fakeRequest().withSession("pseudo", "Julien")
		);
		assertEquals(303, status(result));
	}
}