package model;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Rapport")
public class Rapport {
	
	//------------------ATTRIBUT-----------------
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id ;
	private String titre;
	private String contenu;
	
	//------POUR LES DATE -----------------
    @Temporal(TemporalType.TIMESTAMP)
	private Date datecreation ;
	//------------FK---------------------
    @ManyToOne
    	@JoinColumn(name = "idTechnicien") 
    private Technicien technicien;
    
    @OneToOne
    @JoinColumn(name = "idTicket") // FK vers Ticket → côté propriétaire
    private Ticket ticket;

	
	//--------------------CONSTRUCTEUR POUR HIBERNATE ----------------
		public Rapport(){}
		
	
	//--------------------CONSTRUCTEUR----------------
		public Rapport( int id , String titre , String contenu , Date datecreation , Technicien technicien , Ticket ticket){
			this.id=id;
			this.titre=titre;
			this.contenu=contenu;
			this.datecreation=datecreation;
			this.technicien=technicien;
			this.ticket=ticket;
		}

		//-------------------GETTERS && SETTERS-----------
		//---------ID-----------
		
		public void setId(int id ) {
			this.id=id;
		}
		public int getId() {
			return id;
		}
		
		//--------TITRE-----------
		
		public void setTitre(String titre ) {
			this.titre=titre;
		}
		public String getTitre() {
			return titre;
		}
		//--------CONTENU--------
		
		public void setContenu(String contenu ) {
			this.contenu=contenu;
		}
		public String getContenu() {
			return contenu;
		}
		
		//--------DATECREATION---------
		
		public void setDatecreation(Date datecreation ) {
			this.datecreation=datecreation;
		}
		public Date getDatecreation() {
			return datecreation;
		}
		
		//--------IDTECHNICIEN----
		
		public void setTechnicien(Technicien technicien ) {
			this.technicien=technicien;
		}
		public Technicien getTechnicien() {
			return technicien;
		}
		
		//--------IDTicket----
		
		public void setTicket(Ticket ticket) {
			this.ticket=ticket;
		}
		public Ticket getTicket() {
			return ticket;
		}
		

		
		
	}