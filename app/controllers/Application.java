package controllers;

import models.*;
import play.data.Form;
import play.data.validation.Constraints.Required;
import static play.data.Form.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
	public static class Login {
		@Required
		public String pseudo;
		public String password;
		public String newAccount;
		
		public String validate() {
			if (newAccount != null && Joueur.exist(pseudo)) {
				return "Ce pseudo est déjà utilisé par un autre joueur";
			}
			if (newAccount == null && Joueur.authenticate(pseudo, password) == null) {
				return "Pseudo ou mot de passe incorrect";
			}
			
			return null;
		}
	}
	
	public static Result index() {
		String pseudo = session().get("pseudo");
		Joueur joueur = null;
		if(pseudo != null) {
			joueur = Joueur.find.byId(pseudo);
		}
		
		return ok(index.render(
			joueur,
			Joueur.listByScore()
		));
	}
	
	public static Result login() {
		String pseudo = session().get("pseudo");
		
		if(pseudo == null) {
			return ok(login.render(form(Login.class)));
		} else {
			
			return redirect(
				routes.Jeu.profil()
			);
		}
	}
	
	public static Result authenticate() {
		Form<Login> loginForm = form(Login.class).bindFromRequest();
		
		if (loginForm.hasErrors()) {
			return badRequest(login.render(loginForm));
		}
		
		String pseudo = loginForm.get().pseudo;
		
		if(loginForm.get().newAccount != null) {
			Joueur.create(pseudo, loginForm.get().password);
		}
		
		session().clear();
		session("pseudo", pseudo);
		flash("success", "Vous êtes connecté sous le pseudo " + pseudo);
		
		return redirect(
			routes.Jeu.profil()
		);
	}
	
	public static Result logout() {
		session().clear();
		flash("success", "Vous venez de vous déconnecter");
		return redirect(
			routes.Application.login()
		);
	}

	@Security.Authenticated(Secured.class)
	public static Result classement() {
		return ok(classement.render(
			Joueur.find.byId(request().username()),
			Joueur.listByScore()
		));
	}
}
