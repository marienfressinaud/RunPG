import play.*;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;
import java.util.*;

public class Global extends GlobalSettings {
	@SuppressWarnings("unchecked")
	@Override
	public void onStart(Application app) {
		// Check if the database is empty
		if (Joueur.find.findRowCount() == 0) {
			Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");

			Ebean.save(all.get("joueurs"));
			Ebean.save(all.get("chapitres"));
			Ebean.save(all.get("quetes"));
			Ebean.save(all.get("seances"));
		}
	}
}
