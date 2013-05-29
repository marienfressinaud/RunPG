package models;

import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Chapitre extends Model {
	public static Finder<Integer, Chapitre> find = new Finder<Integer, Chapitre>(
		Integer.class, Chapitre.class
	);

	@Id
	public Integer numero;
	public String nom;

	public Chapitre(Integer numero, String nom) {
		this.numero = numero;
		this.nom = nom;
	}
}
