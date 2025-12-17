
package model;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Technicien")
public class Technicien extends Utilisateur{
	
	//-----------------ATTRIBUT--------------------

	private String specialite;
	
	//-----------CHAMPS LIEE A LA TABLE TICKET -----------------------
	@OneToMany(mappedBy = "technicien", cascade = CascadeType.ALL)
	private List<Ticket> ticketaffecter = new ArrayList<>();


	//--------------------CONSTRUCTEUR POUR HIBERNATE ----------------
		public Technicien(){}
		
	  //--------------------CONSTRUCTEUR----------------
    public Technicien( int id, String nom, String prenom, String email, String motdepasse, Role role ,String specialite , Admin createur , List<Ticket> ticketaffecter){
    	super( id,  nom,  prenom,  email,  motdepasse,  role , createur);
    	this.specialite=specialite;
  		this.ticketaffecter=ticketaffecter;
  		
  	}
  //-------------------GETTERS && SETTERS-----------
  	//---------SPECIALITE-----------
  	
  	public void setSpecialite(String specialite ) {
  		this.specialite=specialite;
  	}
  	public String getSpecialite() {
  		return specialite;
  	}
  	
  	//--------LISTETCKETAFFECTER-----------
  	
  	public void setTicketaffecter( List<Ticket>ticketaffecter ) {
  		this.ticketaffecter=ticketaffecter;
  	}
  	public List<Ticket> getTicketaffecter() {
  		return ticketaffecter;
  	}
  	
}
