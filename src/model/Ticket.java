package model;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Ticket")
public class Ticket {
	
	//-----------------ATTRIBUT----------------
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String titre;
	private String description ;
	
	//------POUR LES DATE -----------------
    @Temporal(TemporalType.TIMESTAMP)
	private Date datecreation;
	   @Enumerated(EnumType.STRING)
	private Status status ;
	   @Enumerated(EnumType.STRING)
	private Priorite priorite;
	
	//------------FK--------------
	 @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
	    private Rapport rapport;
	
	//------------FK---------------------
	 @ManyToOne
	    @JoinColumn(name = "idClient") 
	    private Client client;

	    @ManyToOne
	    @JoinColumn(name = "idTechnicien") 
	    private Technicien technicien;
	    
	//--------------------CONSTRUCTEUR POUR HIBERNATE ----------------
		public Ticket(){}
		
	
	//--------------------CONSTRUCTEUR----------------
		public Ticket( int id , String titre , String description , Date datecreation , Status status ,  Priorite priorite , Rapport rapport , Client client , Technicien technicien){
			this.id=id;
			this.titre=titre;
			this.description=description;
			this.datecreation=datecreation;
			this.status=status;
			this.priorite=priorite;
			this.rapport=rapport;
			this.client=client;
			this.technicien=technicien;			
		}
		//-------------------GETTERS && SETTERS-----------
		
		
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
		//--------DESCRIPTION--------
		
		public void setDescription(String description ) {
			this.description=description;
		}
		public String getDescription() {
			return description;
		}
		
		//--------DATECREATION---------
		
		public void setDatecreation(Date datecreation ) {
			this.datecreation=datecreation;
		}
		public Date getDatecreation() {
			return datecreation;
		}
		
		//--------STATUS----
		
		public void setStatus(Status status ) {
			this.status=status;
		}
		public Status getStatus() {
			return status;
		}
		
		//--------PRIOTITE----------
		
		public void setPriorite(Priorite priorite ) {
			this.priorite=priorite;
		}
		public Priorite getPriorite() {
			return priorite;
		}
		
		
		//--------RAPPORT----
		
		public void setRapport(Rapport rapport ) {
			this.rapport=rapport;
		}
		public Rapport getRapport() {
			return rapport;
		}
		
		//--------IDCLIENT----------
		
		public void setClient(Client client ) {
			this.client=client;
		}
		public Client getClient() {
			return client;
		}
		
		//--------IDTECHNICIEN----------
		
		public void setTechnicien(Technicien technicien ) {
			this.technicien=technicien;
		}
		public Technicien getTechnicien() {
			return technicien;
		}
		
		public String getNomClient() {
		    return client != null ? client.getNom() + " " + client.getPrenom() : "Non défini";
		}

		public String getNomTechnicien() {
		    return technicien != null ? technicien.getNom() + " " + technicien.getPrenom() : "Non défini";
		}
		
		
		
		
		
}