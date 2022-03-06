package src;

public class Person {
	protected String nome;
	protected String cognome;
	protected String codfisc;
	protected int anno;
	protected boolean allocated=false;
	protected Hub hub;
	public Hub getHub() {
		return hub;
	}
	public void setHub(Hub hub) {
		this.hub = hub;
	}
	public Person(String nome, String cognome, String codfisc, int anno) {
		super();
		this.nome = nome;
		this.cognome = cognome;
		this.codfisc = codfisc;
		this.anno = anno;
	}
	public String getNome() {
		return nome;
	}
	public String getCognome() {
		return cognome;
	}
	public String getCodfisc() {
		return codfisc;
	}
	public int getAnno() {
		return anno;
	}
	@Override
	public String toString() {
		return codfisc+","+cognome+","+nome;
	}
	public boolean isAllocated() {
		return allocated;
	}
	public void setAllocated(boolean allocated) {
		this.allocated = allocated;
	}
	
}