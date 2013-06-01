package controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import models.Seance;
import models.Joueur;
import models.Quete;
import play.libs.Json;
import play.data.DynamicForm;
import static play.data.Form.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import views.html.jeu.*;

@Security.Authenticated(Secured.class)
public class Jeu extends Controller {
	private static class PointGPS {
		public double longitude;
		public double latitude;
		public double altitude;
		public Integer precision;
		public Date date;
	}

	public static Result profil() {
		return ok(profil.render(
			Joueur.find.byId(request().username())
		));
	}
	
	public static Result quetes(Integer id) {
		List<Quete> listeQuetes = Quete.listByJoueur(request().username());
		Quete queteActuelle = null;
		
		if(id > 0) {
			queteActuelle = Quete.find.byId(id);
		}
		
		if(queteActuelle == null) {
			for(Quete quete : listeQuetes) {
				Seance seance = quete.getSeance(request().username());
				if(seance.etat.equals(Seance.Etat.NOUVELLE) ||
				   seance.etat.equals(Seance.Etat.EN_COURS) ||
				   seance.etat.equals(Seance.Etat.TERMINEE)) {
					queteActuelle = Quete.find.byId(seance.quete.id);
				}
			}
		}
		
		return ok(quetes.render(
			Joueur.find.byId(request().username()),
			listeQuetes,
			queteActuelle
		));
	}

	public static Result validerQuete() {
		DynamicForm requestData = form().bindFromRequest();
		Integer idQuete = Integer.parseInt(requestData.get("idQuete"));
		String action = requestData.get("action");
		
		if(action.equals("next")) {
			Seance.valider(idQuete, request().username());
		} else if(action.equals("accept")) {
			Seance.accepter(idQuete, request().username());
		}
		
		return redirect(
			routes.Jeu.quetes(0)
		);
	}

	public static Result seance(Integer idQuete) {
		Quete queteActuelle = null;
		if(idQuete > 0) {
			queteActuelle = Quete.find.byId(idQuete);
		}
	
		return ok(seance.render(
			Joueur.find.byId(request().username()),
			queteActuelle
		));
	}

	public static Result validerSeance() {
		DynamicForm requestData = form().bindFromRequest();
		Integer idQuete = Integer.parseInt(requestData.get("idQuete"));
		
		Quete quete = Quete.find.byId(idQuete);
		Joueur joueur = Joueur.find.byId(request().username());
		List<PointGPS> liste = Jeu.parsePointsGPS(requestData.get("positions"));

		return TODO;
	}

	private static List<PointGPS> parsePointsGPS(String positionsJson) {
		List<PointGPS> listePoints = new ArrayList<PointGPS>();
		JsonNode jsonList = Json.parse(positionsJson);
		
		for(JsonNode jsonElt : jsonList) {
			PointGPS point = new PointGPS();
			point.longitude = jsonElt.findPath("lon").asDouble();
			point.latitude = jsonElt.findPath("lat").asDouble();
			point.altitude =  jsonElt.findPath("alt").asDouble();
			point.precision =  jsonElt.findPath("lon").asInt();
			point.date =  Date.valueOf(jsonElt.findPath("time").asText());
			listePoints.add(point);
		}
		
		return listePoints;
	}
}
