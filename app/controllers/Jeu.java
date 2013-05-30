package controllers;

import models.Joueur;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import views.html.jeu.*;

@Security.Authenticated(Secured.class)
public class Jeu extends Controller {

	public static Result profil() {
		return ok(profil.render(
			Joueur.find.byId(request().username())
		));
	}
	
	public static Result quetes() {
		return TODO;
	}

}
