package models;

import java.util.List;

import models.EtatQuete.Etat;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void createAndRetrieveJoueur() {
		new Joueur("Bob", "mysecretpassword", 42, 0, 0).save();
		Joueur bob = Joueur.find.where().eq("pseudo", "Bob").findUnique();
		assertNotNull(bob);
		assertEquals("Bob", bob.pseudo);
	}

	@Test
	public void tryAuthenticateJoueur() {
		new Joueur("Bob", "mysecretpassword", 42, 0, 0).save();
		
		assertNotNull(Joueur.login("Bob", "mysecretpassword"));
		assertNull(Joueur.login("Bob", "mybadpassword"));
		assertNull(Joueur.login("Bobby", "mysecretpassword"));
	}
	
	@Test
	public void IncreaseScoreJoueur() {
		Joueur bob = new Joueur("Bob", "mysecretpassword", 500, 0, 0);
		bob.augmenterScore(100);
		
		assertEquals(600, bob.score.intValue());
	}

	@Test
	public void createAndRetrieveChapitre() {
		new Chapitre(1, "Prologue").save();
		Chapitre chapitre = Chapitre.find.where().eq("numero", 1).findUnique();
		assertNotNull(chapitre);
		assertEquals("Prologue", chapitre.nom);
	}

	@Test
	public void createAndRetrieveQuete() {
		new Chapitre(1, "Prologue").save();
		
		Quete quete = new Quete(
			"Re-Sauver le monde",
			"Vous devez sauver le monde une nouvelle fois !!",
			"Vous avez encore sauvé le monde", 21, 60
		);
		quete = Quete.create(quete, null, 1);
		
		Quete quete2 = new Quete(
			"Sauver le monde",
			"Vous devez sauver le monde !!",
			"Vous avez sauvé le monde", 42, 120
		);
		quete2 = Quete.create(quete2, quete.id, 1);
		
		Quete premiere = Quete.find.where().eq("id", 2).findUnique();
		Quete suivante = Quete.find.where().eq("id", premiere.suivante.id).findUnique();
		Chapitre chapitre = Chapitre.find.where()
		                                 .eq("numero", premiere.chapitre.numero)
		                                 .findUnique();

		assertNotNull(premiere);
		assertNotNull(suivante);
		assertNotNull(chapitre);
		assertNull(suivante.suivante);
		assertEquals("Sauver le monde", premiere.titre);
		assertEquals("Re-Sauver le monde", suivante.titre);
		assertEquals("Prologue", chapitre.nom);
	}
	
	@Test
	public void findQuetesByChapitre() {
		new Chapitre(1, "Prologue").save();
		
		Quete quete = new Quete(
			"Re-Sauver le monde",
			"Vous devez sauver le monde une nouvelle fois !!",
			"Vous avez encore sauvé le monde", 21, 60
		);
		Quete.create(quete, null, 1);
		
		Quete quete2 = new Quete(
			"Sauver le monde",
			"Vous devez sauver le monde !!",
			"Vous avez sauvé le monde", 42, 120
		);
		Quete.create(quete2, quete.id, 1);
		
		List<Quete> quetes = Quete.findByChapitre(1);
		assertEquals(2, quetes.size());
		assertEquals("Re-Sauver le monde", quetes.get(0).titre);
	}
	
	@Test
	public void createAndRetrieveEtatQuete() {
		new Chapitre(1, "Prologue").save();
		
		Quete quete = new Quete(
			"Sauver le monde",
			"Vous devez sauver le monde !!",
			"Vous avez sauvé le monde", 8, 60
		);
		quete = Quete.create(quete, null, 1);

		new Joueur("Bob", "mysecretpassword", 42, 0, 0).save();
		Joueur bob = Joueur.find.where().eq("pseudo", "Bob").findUnique();
		
		new EtatQuete(9, 9, 60, "2013-05-29.gpx", Etat.TERMINEE, bob, quete).save();
		EtatQuete etat = EtatQuete.find.where().eq("id", 1).findUnique();
		
		assertNotNull(etat);
		assertEquals(9, (int)etat.distance);
		assertEquals(Etat.TERMINEE, etat.etat);
		assertEquals("Bob", etat.joueur.pseudo);
	}
}
