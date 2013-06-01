package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Quete extends Model {
	public static Finder<Integer, Quete> find = new Finder<Integer, Quete>(
		Integer.class, Quete.class
	);
	
	@Id
	public Integer id;
	public String titre;
	public String sequenceDeb;
	public String sequenceFin;
	public Integer objDistance;
	public Integer objDuree;

	@OneToOne
	public Quete suivante;
	
	@ManyToOne
	public Chapitre chapitre;


	public Quete(String titre, String sequenceDeb,
	             String sequenceFin, Integer objDistance,
	             Integer objDuree) {
		this.titre = titre;
		this.sequenceDeb = sequenceDeb;
		this.sequenceFin = sequenceFin;
		this.objDistance = objDistance;
		this.objDuree = objDuree;
	}
	
	public Integer getObjDistance(Joueur j) {
		return Math.max(this.objDistance - j.xpVitesse / 100, 2);
	}
	
	public Integer getObjDuree(Joueur j) {
		return Math.max(this.objDuree - 15 * j.xpEndurance / 200, 15);
	}
	
	public Seance getSeance(String pseudo) {
		return Seance.find.where().eq("joueur.pseudo", pseudo).eq("quete.id", this.id).findUnique();
	}
	
	public static Quete create(Quete quete, Integer suivante, Integer numChapitre) {
		if(suivante != null) {
			quete.suivante = Quete.find.ref(suivante);
		} else {
			quete.suivante = null;
		}
		quete.chapitre = Chapitre.find.ref(numChapitre);

		quete.save();
		return quete;
	}

	public static List<Quete> findByChapitre(Integer numero) {
		return find.where().eq("chapitre.numero", numero).findList();
	}

	public static List<Quete> listByJoueur(String pseudo) {
		List<Seance> seances = Seance.find.where().eq("joueur.pseudo", pseudo).findList();
		
		List<Quete> quetes = new ArrayList<Quete>();
		for(Seance s : seances) {
			quetes.add(find.byId(s.quete.id));
		}
		
		return quetes;
	}
	
	public static Quete getQueteInitiale() {
		return find.where().eq("id", 1).findUnique();
	}
}
