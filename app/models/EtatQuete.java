package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class EtatQuete extends Model {
	public static Finder<Integer, EtatQuete> find = new Finder<Integer, EtatQuete>(
		Integer.class, EtatQuete.class
	);
	
	public enum Etat {
		NOUVELLE,
		EN_COURS,
		TERMINEE,
		VALIDEE
	}

	@Id
	public Integer id;
	public Integer vitesseMoyenne;
	public Integer distance;
	public Integer duree;
	public String fichierParcours;
	public Etat etat;
	
	@ManyToOne
	public Joueur joueur;
	
	@ManyToOne
	public Quete quete;

	public EtatQuete(Integer vitesseMoyenne, Integer distance,
			Integer duree, String fichierParcours, Etat etat,
			Joueur joueur, Quete quete) {
		this.vitesseMoyenne = vitesseMoyenne;
		this.distance = distance;
		this.duree = duree;
		this.fichierParcours = fichierParcours;
		this.etat = etat;
		this.joueur = joueur;
		this.quete = quete;
	}
}