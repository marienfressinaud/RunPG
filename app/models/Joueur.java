package models;

import java.io.File;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.*;

@Entity
public class Joueur extends Model {
	public static Finder<String, Joueur> find = new Finder<String, Joueur>(
		String.class, Joueur.class
	);

	@Id
	public String pseudo;
	public String password;
	public Integer score;
	public Integer xpVitesse;
	public Integer xpEndurance;

	public Joueur(String pseudo, String password, Integer score,
	              Integer xpVitesse, Integer xpEndurance) {
		this.pseudo = pseudo;
		this.password = password;
		this.score = score;
		this.xpVitesse = xpVitesse;
		this.xpEndurance = xpEndurance;
	}
	public Joueur(String pseudo, String password) {
		this(pseudo, password, 0, 0, 0);
	}
	
	public static Joueur authenticate(String pseudo, String password) {
		return find.where().eq("pseudo", pseudo)
		                   .eq("password", password)
		                   .findUnique();
	}
	
	public void augmenterScore(Integer add) {
		this.score += add;
	}
	
/*
	public int getScore() {
		return this.score + (this.xpVitesse + this.xpEndurance) / 10;
	}
*/
	
	public int getProgression() {
		int totalQuetes = Quete.find.findList().size();
		int totalTerminee = 0;
		
		List<Seance> listeSeances = Seance.find.where().eq("joueur.pseudo", pseudo).findList();
		for(Seance s : listeSeances) {
			if(s.etat == Seance.Etat.TERMINEE ||
			   s.etat == Seance.Etat.VALIDEE) {
				totalTerminee++;
			}
		}
		
		return totalTerminee * 100 / totalQuetes;
	}
	
	public String getAvatar() {
		File file = new File("public/media/avatars/" + this.pseudo + ".png");
		if(file.exists()) {
			return "media/avatars/" + this.pseudo + ".png";
		} else {
			return "media/avatars/default.png";
		}
	}

	public static List<Joueur> listByScore() {
		return find.where().orderBy("score desc").findList();
	}
	
	public static Boolean exist(String pseudo) {
		return find.where().eq("pseudo", pseudo).findUnique() != null;
	}
	
	public static Joueur create(String pseudo, String password) {
		Joueur j = new Joueur(pseudo, password);
		j.save();
		
		Seance.create(j, Quete.getQueteInitiale());
		
		return j;
	}
}
