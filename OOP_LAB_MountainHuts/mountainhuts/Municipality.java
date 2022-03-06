package mountainhuts;

import java.util.HashMap;

/**
 * Represents a municipality
 *
 */
public class Municipality {

	protected String nome;
	protected String provincia;
	protected Integer altitudine;
	protected HashMap<String, MountainHut> mountHut;
	
	public Municipality(String nome, String provincia, Integer altitudine) {
		this.nome = nome;
		this.provincia = provincia;
		this.altitudine = altitudine;
		mountHut = new HashMap<String, MountainHut>();
	}
	
	/**
	 * Name of the municipality.
	 * 
	 * Within a region the name of a municipality is unique
	 * 
	 * @return name
	 */
	public String getName() {
		return nome;
	}

	/**
	 * Province of the municipality
	 * 
	 * @return province
	 */
	public String getProvince() {
		return provincia;
	}

	/**
	 * Altitude of the municipality
	 * 
	 * @return altitude
	 */
	public Integer getAltitude() {
		return altitudine;
	}
	
	public Long numMountHut() {
		return (long)mountHut.size();
	}
}
