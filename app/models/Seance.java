package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Seance extends Model {
	public static Finder<Integer, Seance> find = new Finder<Integer, Seance>(
		Integer.class, Seance.class
	);
	
	public enum Etat {
		NOUVELLE,
		EN_COURS,
		TERMINEE,
		VALIDEE
	}

	@Id
	public Integer id;
	public Integer distance;
	public Integer duree;
	public Etat etat;
	@ManyToOne
	public Joueur joueur;
	@ManyToOne
	public Quete quete;

	public Seance(Integer distance, Integer duree, Etat etat,
	              Joueur joueur, Quete quete) {
		this.distance = distance;
		this.duree = duree;
		this.etat = etat;
		this.joueur = joueur;
		this.quete = quete;
	}
	
	public static void create(Joueur j, Quete q) {
		new Seance(0, 0, Etat.NOUVELLE, j, q).save();
	}
}
