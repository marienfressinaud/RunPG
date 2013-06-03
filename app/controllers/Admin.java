package controllers;

import static play.data.Form.form;

import models.Joueur;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import views.html.admin.*;

@Security.Authenticated(SecuredAdmin.class)
public class Admin extends Controller {
	public static class Update {
		@Required
		public String pseudo;
		public Integer score;
		public Integer xpVitesse;
		public Integer xpEndurance;
		public String submit;
		
		public String validate() {
			if(!submit.equals("update") && !submit.equals("delete")) {
				return "Il semblerait que vous n'ayez pas cliqué sur le bon bouton ! ";
			}
			if(score < 0) {
				return "Le score doit être supérieur ou égal à 0";
			}
			if(xpVitesse < 0 || xpEndurance < 0) {
				return "L'expérience d'un joueur doit être supérieur ou égal à 0";
			}
			
			return null;
		}
	}

	public static Result index() {
		return ok(administration.render(
			Joueur.find.byId(request().username()),
			Joueur.listByPseudo(),
			form(Update.class)
		));
	}

	public static Result update() {
		Form<Update> updateForm = form(Update.class).bindFromRequest();
		
		if (updateForm.hasErrors()) {
			return badRequest(administration.render(
				Joueur.find.byId(request().username()),
				Joueur.listByPseudo(),
				updateForm
			));
		}

		String pseudo = updateForm.get().pseudo;
		Integer score = updateForm.get().score;
		Integer xpVit = updateForm.get().xpVitesse;
		Integer xpEnd = updateForm.get().xpEndurance;
		String submit = updateForm.get().submit;
		
		if(submit.equals("update")) {
			Joueur.update(pseudo, score, xpVit, xpEnd);
			flash("success", "Le joueur " + pseudo + " a été mis à jour");
		} else if(submit.equals("delete")) {
			Joueur j = Joueur.find.byId(pseudo);
			if(j != null) {
				Joueur.delete(j);
			}
			flash("success", "Le joueur " + j.pseudo + " a été supprimé");
		}
		
		return redirect(
			routes.Admin.index()
		);
	}
}
