package mountainhuts;

import java.util.Optional;

/**
 * Represents a mountain hut.
 * 
 * It is linked to a {@link Municipality}
 *
 */
public class MountainHut {

	protected String nome;
	protected Optional<Integer> altitude;
	protected String category;
	protected Integer beds;
	protected Municipality muni;
	
	
	public MountainHut(String nome, Integer altitude, String category, Integer beds, Municipality muni) {
		this.nome = nome;
		this.altitude = Optional.ofNullable(altitude);
		this.category = category;
		this.beds = beds;
		this.muni = muni;
	}

	/**
	 * Unique name of the mountain hut
	 * @return name
	 */
	public String getName() {
		return nome;
	}

	/**
	 * Altitude of the mountain hut.
	 * May be absent, in this case an empty {@link java.util.Optional} is returned.
	 * 
	 * @return optional containing the altitude
	 */
	public Optional<Integer> getAltitude() {
		return altitude;
	}

	/**
	 * Category of the hut
	 * 
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Number of beds places available in the mountain hut
	 * @return number of beds
	 */
	public Integer getBedsNumber() {
		return beds;
	}

	/**
	 * Municipality where the hut is located
	 *  
	 * @return municipality
	 */
	public Municipality getMunicipality() {
		return muni;
	}

}
