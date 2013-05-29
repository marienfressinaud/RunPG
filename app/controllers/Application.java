package controllers;

import models.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
  
	public static Result index() {
		return ok(index.render(
			Joueur.listByScore()
		));
	}
  
}
