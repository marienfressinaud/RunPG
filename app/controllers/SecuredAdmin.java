package controllers;

import models.Joueur;
import play.mvc.*;
import play.mvc.Http.*;

public class SecuredAdmin extends Security.Authenticator {

	@Override
	public String getUsername(Context ctx) {
		String pseudo = ctx.session().get("pseudo");
		if(pseudo != null && !Joueur.estAdmin(pseudo)) {
			pseudo = null;
		}
		return pseudo;
	}
	
	@Override
	public Result onUnauthorized(Context ctx) {
		// TODO erreur authentification
		return redirect(routes.Application.login());
	}
}
