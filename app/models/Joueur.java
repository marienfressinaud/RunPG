package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Joueur extends Model {

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

	public static Finder<String, Joueur> find = new Finder<String, Joueur>(
		String.class, Joueur.class
	);
	
	public static Joueur login(String pseudo, String password) {
		return find.where().eq("pseudo", pseudo)
		                   .eq("password", password)
		                   .findUnique();
	}
	
	public void augmenterScore(Integer add) {
		this.score += add;
	}
}
