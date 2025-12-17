package model;
import javax.persistence.*;

@Entity
@Table(name = "Utilisateur")

//-----------HERITAGE-------------------
@Inheritance(strategy = InheritanceType.JOINED)
public class Utilisateur {

	//------------------ATTRIBUT-------------------
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String nom;
	private String prenom;
	private String email;
	private String motdepasse;
	   @Enumerated(EnumType.STRING)
	private Role role;
	 //------------FK---------------------
		 @ManyToOne
		    @JoinColumn(name = "idAdmin") 
	private Admin createur;
	
	//--------------------CONSTRUCTEUR POUR HIBERNATE ----------------
	public Utilisateur(){}
	
	//--------------------CONSTRUCTEUR----------------
	public Utilisateur( int id, String nom, String prenom, String email, String motdepasse, Role role ,Admin createur){
		this.id=id;
		this.nom=nom;
		this.prenom=prenom;
		this.email=email;
		this.motdepasse=motdepasse;
		this.role=role;
		this.createur=createur;

		
	}
	//-------------------GETTERS && SETTERS-----------
	//---------ID-----------
	
	public void setId(int id ) {
		this.id=id;
	}
	public int getId() {
		return id;
	}
	
	//--------NOM-----------
	
	public void setNom(String nom ) {
		this.nom=nom;
	}
	public String getNom() {
		return nom;
	}
	//--------PRENOM--------
	
	public void setPrenom(String prenom ) {
		this.prenom=prenom;
	}
	public String getPrenom() {
		return prenom;
	}
	
	//--------EMAIL---------
	
	public void setEmail(String email ) {
		this.email=email;
	}
	public String getEmail() {
		return email;
	}
	
	//--------MOTDEPASSE----
	
	public void setMotdepasse(String motdepasse ) {
		this.motdepasse=motdepasse;
	}
	public String getMotdepasse() {
		return motdepasse;
	}
	
	//--------ROLE----------
	
	public void setRole(Role role ) {
		this.role=role;
	}
	public Role getRole() {
		return role;
	}
	
	//--------CREATEUR----------
	
	public void setCreateur(Admin createur ) {
		this.createur=createur;
	}
	public Admin getCreateur() {
		return createur;
	}
	
	
	
	
}