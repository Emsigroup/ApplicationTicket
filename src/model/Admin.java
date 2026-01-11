package model;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Admin")
public class Admin extends Utilisateur {
	
	//-----------------ATTRIBUT--------------------
	//--------------------RELATION AVEC UTILISATEUR ---------------
    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL)

    private List<Utilisateur> ListeCompteCree = new ArrayList<>();

  //--------------------CONSTRUCTEUR POUR HIBERNATE ----------------
  	public Admin(){}
  	
  //--------------------CONSTRUCTEUR----------------
  	public Admin(int id, String nom, String prenom, String email, String motdepasse, Role role, Admin createur, List<Utilisateur> ListeCompteCree){
  	    // Pour Admin lui-même, createur doit toujours être null !
  	    super(id, nom, prenom, email, motdepasse, role, null);
  	    this.ListeCompteCree = ListeCompteCree != null ? ListeCompteCree : new ArrayList<>();
  	}


	//-------------------GETTERS && SETTERS-----------
	
	//--------LISTECOMPTECREE-----------
	
	public void setListeCompteCree(List<Utilisateur> ListeCompteCree ) {
		this.ListeCompteCree=ListeCompteCree;
	}
	public List<Utilisateur> getListeCompteCree() {
		return ListeCompteCree;
	}
}