package models;

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
}