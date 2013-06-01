package models;

import java.util.List;

import models.Seance.Etat;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

// TODO utiliser fichier Yaml pour automatiser la création de la base de tests
public class ModelsTest extends WithApplication {
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void createAndRetrieveJoueur() {
		Joueur Julien = Joueur.find.where().eq("pseudo", "Julien").findUnique();
		assertNotNull(Julien);
		assertEquals("Julien", Julien.pseudo);
	}

	@Test
	public void tryAuthenticateJoueur() {
		assertNotNull(Joueur.authenticate("Julien", "jujudu38"));
		assertNull(Joueur.authenticate("Julien", "mybadpassword"));
		assertNull(Joueur.authenticate("MauvaisJulien", "mysecretpassword"));
	}
	
	@Test
	public void IncreaseScoreJoueur() {
		Joueur Julien = Joueur.find.where().eq("pseudo", "Julien").findUnique();
		Julien.augmenterScore(100);
		
		assertEquals(110, Julien.score.intValue());
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

		Joueur Julien = Joueur.find.where().eq("pseudo", "Julien").findUnique();
		
		new Seance(9, 9, 60, "2013-05-29.gpx", Etat.TERMINEE, Julien, quete).save();
		Seance etat = Seance.find.where().eq("id", 1).findUnique();
		
		assertNotNull(etat);
		assertEquals(9, (int)etat.distance);
		assertEquals(Etat.TERMINEE, etat.etat);
		assertEquals("Julien", etat.joueur.pseudo);
	}
}
