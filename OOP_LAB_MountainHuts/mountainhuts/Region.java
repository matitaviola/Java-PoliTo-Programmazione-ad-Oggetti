package mountainhuts;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class {@code Region} represents the main facade
 * class for the mountains hut system.
 * 
 * It allows defining and retrieving information about
 * municipalities and mountain huts.
 *
 */
public class Region{

	protected TreeSet<String> altitudini;
	protected HashMap<String,Municipality> municipi;
	protected TreeMap<String, MountainHut> mounHut;
	
	protected String nome;
	/**
	 * Create a region with the given name.
	 * 
	 * @param name
	 *            the name of the region
	 */
	public Region(String name) {
		this.nome=name;
		altitudini= new TreeSet<String>();
		municipi= new HashMap<String,Municipality>();
		mounHut= new TreeMap<String,MountainHut>();
	}

	/**
	 * Return the name of the region.
	 * 
	 * @return the name of the region
	 */
	public String getName() {
		return this.nome;
	}

	/**
	 * Create the ranges given their textual representation in the format
	 * "[minValue]-[maxValue]".
	 * 
	 * @param ranges
	 *            an array of textual ranges
	 */
	public void setAltitudeRanges(String... ranges) {
		for(String s: ranges) {
			altitudini.add(s);
		}
	}

	/**
	 * Return the textual representation in the format "[minValue]-[maxValue]" of
	 * the range including the given altitude or return the default range "0-INF".
	 * 
	 * @param altitude
	 *            the geographical altitude
	 * @return a string representing the range
	 */
	public String getAltitudeRange(Integer altitude) {
		String backy="0-INF";
		for(String s: altitudini) {
			String range[]=s.split("-");
			if(altitude<=Integer.parseInt(range[1]) && altitude>=Integer.parseInt(range[0])) {
				backy=s;
				break;
			}	
		}
		return backy;
	}

	/**
	 * Create a new municipality if it is not already available or find it.
	 * Duplicates must be detected by comparing the municipality names.
	 * 
	 * @param name
	 *            the municipality name
	 * @param province
	 *            the municipality province
	 * @param altitude
	 *            the municipality altitude
	 * @return the municipality
	 */
	public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
		if(!municipi.containsKey(name))
			municipi.put(name, new Municipality(name,province,altitude));
		return municipi.get(name);
	}

	/**
	 * Return all the municipalities available.
	 * 
	 * @return a collection of municipalities
	 */
	public Collection<Municipality> getMunicipalities() {
		return municipi.values();
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 *
	 * @param name
	 *            the mountain hut name
	 * @param category
	 *            the mountain hut category
	 * @param bedsNumber
	 *            the number of beds in the mountain hut
	 * @param municipality
	 *            the municipality in which the mountain hut is located
	 * @return the mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, String category, Integer bedsNumber,
			Municipality municipality) {
		if(!municipality.mountHut.containsKey(name)) {
			MountainHut mH =  new MountainHut(name,null,category,bedsNumber,municipality);
			municipality.mountHut.put(name, mH);
			mounHut.put(name, mH);
		}
			return municipality.mountHut.get(name);
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 * 
	 * @param name
	 *            the mountain hut name
	 * @param altitude
	 *            the mountain hut altitude
	 * @param category
	 *            the mountain hut category
	 * @param bedsNumber
	 *            the number of beds in the mountain hut
	 * @param municipality
	 *            the municipality in which the mountain hut is located
	 * @return a mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, Integer altitude, String category, Integer bedsNumber,
			Municipality municipality) {
		if(!mounHut.containsKey(name)) {
			MountainHut mH=  new MountainHut(name,altitude,category,bedsNumber,municipality);
			municipality.mountHut.put(name, mH);
			mounHut.put(name, mH);
		}
		return mounHut.get(name);
	}

	/**
	 * Return all the mountain huts available.
	 * 
	 * @return a collection of mountain huts
	 */
	public Collection<MountainHut> getMountainHuts() {
		return mounHut.values();
	}

	/**
	 * Factory methods that creates a new region by loadomg its data from a file.
	 * 
	 * The file must be a CSV file and it must contain the following fields:
	 * <ul>
	 * <li>{@code "Province"},
	 * <li>{@code "Municipality"},
	 * <li>{@code "MunicipalityAltitude"},
	 * <li>{@code "Name"},
	 * <li>{@code "Altitude"},
	 * <li>{@code "Category"},
	 * <li>{@code "BedsNumber"}
	 * </ul>
	 * 
	 * The fields are separated by a semicolon (';'). The field {@code "Altitude"}
	 * may be empty.
	 * 
	 * @param name
	 *            the name of the region
	 * @param file
	 *            the path of the file
	 */
	public static Region fromFile(String name, String file) {
		Region r=new Region(name);
		List<String> rData = readData(file);
		String intestazione = rData.remove(0);
		//dovr� trovare cosa fare con questa lista
		for(String s: rData) {
			String values[]=s.split(";");
			Municipality mu=r.createOrGetMunicipality(values[1], values[0], Integer.parseInt(values[2]));
			if(values[4].equals(""))
				r.createOrGetMountainHut(values[3],values[5],Integer.parseInt(values[6]),mu);
			else
				r.createOrGetMountainHut(values[3],Integer.parseInt(values[4]),values[5],(Integer) Integer.parseInt(values[6]),mu);
			System.out.println(values[3]+":"+values[4]);
		}
		return r;
	}

	/**
	 * Internal class that can be used to read the lines of
	 * a text file into a list of strings.
	 * 
	 * When reading a CSV file remember that the first line
	 * contains the headers, while the real data is contained
	 * in the following lines.
	 * 
	 * @param file the file name
	 * @return a list containing the lines of the file
	 */
	@SuppressWarnings("unused")
	private static List<String> readData(String file) {
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			return in.lines().collect(toList());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Count the number of municipalities with at least a mountain hut per each
	 * province.
	 * 
	 * @return a map with the province as key and the number of municipalities as
	 *         value
	 */
	public Map<String, Long> countMunicipalitiesPerProvince() {
		return (municipi.values()).stream()
						.collect(
								Collectors.groupingBy(p->p.getProvince(),Collectors.counting()));
	}

	/**
	 * Count the number of mountain huts per each municipality within each province.
	 * 
	 * @return a map with the province as key and, as value, a map with the
	 *         municipality as key and the number of mountain huts as value
	 */
	public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
		return  mounHut.values().stream()
						.collect(
								Collectors.groupingBy(m->m.getMunicipality().getProvince(),
													  Collectors.groupingBy(m->m.getMunicipality().getName(),
																			Collectors.counting()
																			)
													)
								);
	}

	/**
	 * Count the number of mountain huts per altitude range. If the altitude of the
	 * mountain hut is not available, use the altitude of its municipality.
	 * 
	 * @return a map with the altitude range as key and the number of mountain huts
	 *         as value
	 */
	public Map<String, Long> countMountainHutsPerAltitudeRange() {
		return mounHut.values().stream()
				.collect(
						Collectors.groupingBy(
								m->getAltitudeRange(m.getAltitude().orElse(m.getMunicipality().getAltitude())),
								Collectors.counting()
										)
							);
	}

	/**
	 * Compute the total number of beds available in the mountain huts per each
	 * province.
	 * 
	 * @return a map with the province as key and the total number of beds as value
	 */
	public Map<String, Integer> totalBedsNumberPerProvince() {
		return  mounHut.values().stream()
				.collect(
						Collectors.groupingBy(m->m.getMunicipality().getProvince(),
												Collectors.summingInt(m->m.getBedsNumber())
								)
						);
	}

	/**
	 * Compute the maximum number of beds available in a single mountain hut per
	 * altitude range. If the altitude of the mountain hut is not available, use the
	 * altitude of its municipality.
	 * 
	 * @return a map with the altitude range as key and the maximum number of beds
	 *         as value
	 */
	public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
		Map<String, Optional<Integer>> maxBedPerAlt = mounHut.values().stream()
				.collect(Collectors.groupingBy(
								m->getAltitudeRange(m.getAltitude().orElse(m.getMunicipality().getAltitude())),
								Collectors.mapping(
										MountainHut::getBedsNumber,
										Collectors.maxBy(Comparator.naturalOrder())
										)
							)
						);
		altitudini.stream().forEach(a->maxBedPerAlt.putIfAbsent(a, Optional.ofNullable(0)));
		return maxBedPerAlt;
	}

	/**
	 * Compute the municipality names per number of mountain huts in a municipality.
	 * The lists of municipality names must be in alphabetical order.
	 * 
	 * @return a map with the number of mountain huts in a municipality as key and a
	 *         list of municipality names as value
	 */
	
	public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
		Map<Long, List<String>> map= mounHut.values().stream()
				.map(x->x.getMunicipality().getName()).
						collect(
								Collectors.groupingBy(x->x, 
										TreeMap::new,
										Collectors.counting()
								)
							).
						entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue,
									Collectors.mapping(Map.Entry::getKey, Collectors.toList())
								)
							)
						;
		return map;
	}
	
	public void printer() {
		mounHut.values().stream().forEach(m->System.out.println(m.getName()+" "+m.getAltitude().orElse(m.muni.getAltitude())));
	}

}
