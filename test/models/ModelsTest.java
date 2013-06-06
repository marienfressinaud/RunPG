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
	public void retrieveJoueur() {
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
		
		assertEquals(100, Julien.score.intValue());
	}

	@Test
	public void retrieveChapitre() {
		Chapitre chapitre = Chapitre.find.where().eq("numero", 1).findUnique();
		assertNotNull(chapitre);
		assertEquals("Enfance", chapitre.nom);
	}

	@Test
	public void retrieveQuete() {
		Quete premiere = Quete.find.where().eq("id", 1).findUnique();
		Quete suivante = Quete.find.where().eq("id", premiere.suivante.id).findUnique();
		Chapitre chapitre = Chapitre.find.byId(premiere.chapitre.numero);

		assertNotNull(premiere);
		assertNotNull(suivante);
		assertNotNull(chapitre);
		assertEquals("Les poules", premiere.titre);
		assertEquals("Le maniement de la fuite", suivante.titre);
		assertEquals("Enfance", chapitre.nom);
	}
	
	@Test
	public void findQuetesByChapitre() {
		List<Quete> quetes = Quete.listByChapitre(1);
		assertEquals(1, quetes.size());
		assertEquals("Les poules", quetes.get(0).titre);
	}
	
	@Test
	public void retrieveSeance() {
		Quete premiere = Quete.find.where().eq("id", 1).findUnique();
		Joueur marien = Joueur.find.where().eq("pseudo", "Marien").findUnique();
		
		Seance s = premiere.getSeance(marien.pseudo);
		
		assertNotNull(s);
		assertEquals(4, (int)s.distance);
		assertEquals(Etat.TERMINEE, s.etat);
		assertEquals("Marien", s.joueur.pseudo);
	}
}
