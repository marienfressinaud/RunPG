package controllers;

import models.*;
import play.data.Form;
import static play.data.Form.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
	public static class Login {
		public String pseudo;
		public String password;
	}
	
	public static Result index() {
		return ok(index.render(
			Joueur.listByScore()
		));
	}
	
	public static Result login() {
		return ok(login.render(form(Login.class)));
	}
	
	public static Result authenticate() {
		Form<Login> loginForm = form(Login.class).bindFromRequest();
		return ok();
	}
}
