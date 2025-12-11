package model;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Client")
public class Client extends Utilisateur {
	
	//-----------------ATTRIBUT--------------------
	//-----------CHAMPS LIEE A LA TABLE TICKET -----------------------
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
	private List<Ticket> mestickets = new ArrayList<>();

	//--------------------CONSTRUCTEUR POUR HIBERNATE ----------------
		public Client(){}
		
	  //--------------------CONSTRUCTEUR----------------
    public Client( int id, String nom, String prenom, String email, String motdepasse, Role role , Admin createur ,List<Ticket> mestickets){
    	super( id,  nom,  prenom,  email,  motdepasse,  role , createur);
  		this.mestickets=mestickets;
  		
  	}
  //-------------------GETTERS && SETTERS-----------
  	//---------MESTICKET-----------
  	
  	public void setMestickets(List<Ticket> mestickets ) {
  		this.mestickets=mestickets;
  	}
  	public List<Ticket> getMestickets() {
  		return mestickets;
  	}
  	
}