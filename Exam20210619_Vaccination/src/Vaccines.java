package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Vaccines {
	protected HashMap<String, Person> persone = new HashMap<>();
	protected HashMap<String, Hub> hubs = new HashMap<>();
	protected SortedSet<Integer> agegaps = new TreeSet<>();
	protected int[] hours;
	public BiConsumer<Integer, String> listener = null;
    public final static int CURRENT_YEAR = java.time.LocalDate.now().getYear();

    // R1
    /**
     * Add a new person to the vaccination system.
     *
     * Persons are uniquely identified by SSN (italian "codice fiscale")
     *
     * @param firstName first name
     * @param lastName last name
     * @param ssn italian "codice fiscale"
     * @param y birth year
     * @return {@code false} if ssn is duplicate,
     */
    public boolean addPerson(String firstName, String lastName, String ssn, int y) {
    	if(persone.containsKey(ssn))
    		return false;
    	persone.put(ssn, new Person(firstName, lastName, ssn, y));
    	return true;
    }

    /**
     * Count the number of people added to the system
     *
     * @return person count
     */
    public int countPeople() {
        return persone.size();
    }

    /**
     * Retrieves information about a person.
     * Information is formatted as ssn, last name, and first name
     * separate by {@code ','} (comma).
     *
     * @param ssn "codice fiscale" of person searched
     * @return info about the person
     */
    public String getPerson(String ssn) {
    	if(!persone.containsKey(ssn))
    		return null;
    	return persone.get(ssn).toString();
    }

    /**
     * Retrieves of a person given their SSN (codice fiscale).
     *
     * @param ssn "codice fiscale" of person searched
     * @return age of person (in years)
     */
    public int getAge(String ssn) {
    	if(!persone.containsKey(ssn))
    		return -1;
    	return java.time.LocalDate.now().getYear()-persone.get(ssn).getAnno();
    }

    /**
     * Define the age intervals by providing the breaks between intervals.
     * The first interval always start at 0 (non included in the breaks)
     * and the last interval goes until infinity (not included in the breaks).
     * All intervals are closed on the lower boundary and open at the upper one.
     * <p>
     * For instance {@code setAgeIntervals(40,50,60)}
     * defines four intervals {@code "[0,40)", "[40,50)", "[50,60)", "[60,+)"}.
     *
     * @param breaks the array of breaks
     */
    public void setAgeIntervals(int... breaks) {
    	if(agegaps.size()==0)
    		agegaps.add(0);
    	for(int i: breaks) {
    		if(i>0)
    			agegaps.add(i);
    	}
    }

    /**
     * Retrieves the labels of the age intervals defined.
     *
     * Interval labels are formatted as {@code "[0,10)"},
     * if the upper limit is infinity {@code '+'} is used
     * instead of the number.
     *
     * @return labels of the age intervals
     */
    public Collection<String> getAgeIntervals() {
    	ArrayList<String> back = new ArrayList<>();
    	int bf=0;
    	for(int i: agegaps) {
    		if(i!=0) {
    			back.add("["+bf+","+i+")");
    			bf=i;
    		}	
    	}
    	back.add("["+bf+",+)");
        return back;
    }

    /**
     * Retrieves people in the given interval.
     *
     * The age of the person is computed by subtracting
     * the birth year from current year.
     *
     * @param range age interval label
     * @return collection of SSN of person in the age interval
     */
    public Collection<String> getInInterval(String range) {
    	String[] val = range.split(",");
    	int low = Integer.parseInt(val[0].substring(1, val[0].length()));
    	int thisyear=java.time.LocalDate.now().getYear();
    	int supp;
    	try {
    		supp = Integer.parseInt(val[1].substring(0, val[0].length()-1));
    	}catch(NumberFormatException e) {
    		supp = 1200000; //numero assurdamente grande	
    	}
    	int hi=supp;
        return persone.values().stream().filter(p-> (thisyear-p.getAnno())>=low && (thisyear-p.getAnno())<hi).map(p->p.getCodfisc()).collect(Collectors.toList());
    }

    // R2
    /**
     * Define a vaccination hub
     *
     * @param name name of the hub
     * @throws VaccineException in case of duplicate name
     */
    public void defineHub(String name) throws VaccineException {
    	if(hubs.containsKey(name))
    		throw new VaccineException("Hub già presente");
    	hubs.put(name, new Hub(name));
    }

    /**
     * Retrieves hub names
     *
     * @return hub names
     */
    public Collection<String> getHubs() {
        return hubs.keySet().stream().collect(Collectors.toList());
    }

    /**
     * Define the staffing of a hub in terms of
     * doctors, nurses and other personnel.
     *
     * @param name name of the hub
     * @param doctors number of doctors
     * @param nNurses number of nurses
     * @param o number of other personnel
     * @throws VaccineException in case of undefined hub, or any number of personnel not greater than 0.
     */
    public void setStaff(String name, int doctors, int nNurses, int o) throws VaccineException {
    	if(!hubs.containsKey(name))
    		throw new VaccineException("Hub non presente");
    	if(doctors<=0 || nNurses<=0 || o<=0)
    		throw new VaccineException("Errore numero personale");
    	Hub h = hubs.get(name);
    	h.setnDoc(doctors);
    	h.setnNurse(nNurses);
    	h.setOthers(o);
    }

    /**
     * Estimates the hourly vaccination capacity of a hub
     *
     * The capacity is computed as the minimum among
     * 10*number_doctor, 12*number_nurses, 20*number_other
     *
     * @param hubName name of the hub
     * @return hourly vaccination capacity
     * @throws VaccineException in case of undefined or hub without staff
     */
    public int estimateHourlyCapacity(String hubName) throws VaccineException {
    	if(!hubs.containsKey(hubName))
    		throw new VaccineException("Hub non presente");
    	Hub h = hubs.get(hubName);
    	if(h.getnDoc()==0)
    		throw new VaccineException("Personale non presente");
        return Math.min(10*h.getnDoc(), Math.min(12*h.getnNurse(),20*h.getOthers()));
    }

    // R3
    /**
     * Load people information stored in CSV format.
     *
     * The header must start with {@code "SSN,LAST,FIRST"}.
     * All lines must have at least three elements.
     *
     * In case of error in a person line the line is skipped.
     *
     * @param people {@code Reader} for the CSV content
     * @return number of correctly added people
     * @throws IOException in case of IO error
     * @throws VaccineException in case of error in the header
     */
    public long loadPeople(Reader people) throws IOException, VaccineException {
    	// Hint:
    	BufferedReader br = new BufferedReader(people);
        int nl = 1;
        int wrongl=0;
        List<String> lines = br.lines().collect(Collectors.toList());
        String linea = lines.remove(0);
        String[] headers = linea.split(",");
        if(headers.length!=4 || headers[0].compareTo("SSN")!=0 || headers[1].compareTo("LAST")!=0 || headers[2].compareTo("FIRST")!=0 || headers[3].compareTo("YEAR")!=0) {
        	if(listener!=null) listener.accept(1, linea);
        	throw new VaccineException("Wrong header names");
        }
        Map<String,Integer> h2i = new HashMap<>();
		for(int i=0; i<headers.length; i++)
			h2i.put(headers[i], i);
		for(String row: lines) {
			try{
				String[] cells = row.split(","); 
				 if(cells.length!=4 || persone.containsKey(cells[h2i.get("SSN")])) {
			        throw new VaccineException("Wrong header length");
				 }
				String ssn = cells[h2i.get("SSN")];
				String last = cells[h2i.get("LAST")];
				String first = cells[h2i.get("FIRST")];
				int year = Integer.parseInt(cells[h2i.get("YEAR")]);
				if(addPerson(first,last,ssn,year))
					nl++;
			}catch(VaccineException v) {
				if(listener!=null) listener.accept(nl+(++wrongl), linea);
			}
		};
        br.close();
        return nl;
    }

    // R4
    /**
     * Define the amount of working hours for the days of the week.
     *
     * Exactly 7 elements are expected, where the first one correspond to Monday.
     *
     * @param h workings hours for the 7 days.
     * @throws VaccineException if there are not exactly 7 elements or if the sum of all hours is less than 0 ore greater than 24*7.
     */
    public void setHours(int... h) throws VaccineException {
    	if(h.length!=7)
    		throw new VaccineException("7 giorni nella settimana");
    	for(int i: h) if(i<0 || i>12) throw new VaccineException("Orario non valido");
    	this.hours=h;
    }

    /**
     * Returns the list of standard time slots for all the days of the week.
     *
     * Time slots start at 9:00 and occur every 15 minutes (4 per hour) and
     * they cover the number of working hours defined through method {@link #setHours}.
     * <p>
     * Times are formatted as {@code "09:00"} with both minuts and hours on two
     * digits filled with leading 0.
     * <p>
     * Returns a list with 7 elements, each with the time slots of the corresponding day of the week.
     *
     * @return the list hours for each day of the week
     */
    public List<List<String>> getHours() {
    	ArrayList<List<String>> back = new ArrayList<>();
    	for(int d: hours) {
    		ArrayList<String> h = new ArrayList<>();
    		for(int i=0; i<d; i++) {
    			int print=9;
    			h.add(String.format("%02d", print)+":"+"00");
    			h.add(String.format("%02d", print)+":"+"15");
    			h.add(String.format("%02d", print)+":"+"30");
    			h.add(String.format("%02d", print)+":"+"45");
    			print++;
    		}
    		back.add(h);
    	}
        return back;
    }

    /**
     * Compute the available vaccination slots for a given hub on a given day of the week
     * <p>
     * The availability is computed as the number of working hours of that day
     * multiplied by the hourly capacity (see {@link #estimateCapacity} of the hub.
     *
     * @return
     */
    public int getDailyAvailable(String hubName, int d) {
    	if(!hubs.containsKey(hubName) || d<0 || d>6)
    		return -1;
    	try {
    		return hours[d]*this.estimateHourlyCapacity(hubName);
    		}catch(VaccineException e) {
    			return -1;
    			}
    }

    /**
     * Compute the available vaccination slots for each hub and for each day of the week
     * <p>
     * The method returns a map that associates the hub names (keys) to the lists
     * of number of available hours for the 7 days.
     * <p>
     * The availability is computed as the number of working hours of that day
     * multiplied by the capacity (see {@link #estimateCapacity} of the hub.
     *
     * @return
     */
    public Map<String, List<Integer>> getAvailable() {
    	HashMap <String, List<Integer>> back = new HashMap<>();
    	for(Hub h: hubs.values()) {
    		ArrayList<Integer> val = new ArrayList<>();
    		for(int i=0; i<7; i++) {
    			val.add(getDailyAvailable(h.getName(),i));
    		}
    		back.put(h.getName(), val);
    	}
        return back;
    }

    /**
     * Computes the general allocation plan a hub on a given day.
     * Starting with the oldest age intervals 40%
     * of available places are allocated
     * to persons in that interval before moving the the next
     * interval and considering the remaining places.
     * <p>
     * The returned value is the list of SSNs (codice fiscale) of the
     * persons allocated to that day
     * <p>
     * <b>N.B.</b> no particular order of allocation is guaranteed
     *
     * @param hubName name of the hub
     * @param d day of week index (0 = Monday)
     * @return the list of daily allocations
     */
    public List<String> allocate(String hubName, int d) {
    	Hub h = hubs.get(hubName);
    	int n=this.getAvailable().get(h.getName()).get(d); //posti disponibili quel giorno in quel Hub
    	int[] ages = agegaps.stream().mapToInt(Integer::intValue).toArray();
    	ArrayList<Person> allocati;
    	for(int a=ages.length-1; a>=0; a--) {
    		final int ar = a;
    		if(a==ages.length-1) 
    			allocati = new ArrayList<>(persone.values().stream().filter(p-> this.getAge(p.codfisc)>=ages[ar] && !p.isAllocated()).limit(n*4/10).collect(Collectors.toList()));
    		else
    			allocati = new ArrayList<>(persone.values().stream().filter(p-> this.getAge(p.codfisc)>=ages[ar] &&  this.getAge(p.codfisc)<ages[ar+1] && !p.isAllocated()).limit(n*4/10).collect(Collectors.toList()));
    		n = n-allocati.size();
    		h.allocted.computeIfAbsent(d, k->new ArrayList<>()).addAll(allocati);
    		allocati.stream().forEach(p->p.setAllocated(true));
    	}
    	if(n!=0) {
    		allocati = new ArrayList<>(persone.values().stream().sorted((a,b)-> -this.getAge(a.getCodfisc())+this.getAge(b.getCodfisc())).filter(p->!p.isAllocated()).limit(n).collect(Collectors.toList()));
    		h.allocted.computeIfAbsent(d, k->new ArrayList<>()).addAll(allocati);
    		allocati.stream().forEach(p->p.setAllocated(true));
    	}
        return h.allocted.get(d).stream().map(p->p.getCodfisc()).collect(Collectors.toList());
    }

    /**
     * Removes all people from allocation lists and
     * clears their allocation status
     */
    public void clearAllocation() {
    	hubs.values().stream().forEach(h->h.emptyAlloc());
    }

    /**
     * Computes the general allocation plan for the week.
     * For every day, starting with the oldest age intervals
     * 40% available places are allocated
     * to persons in that interval before moving the the next
     * interval and considering the remaining places.
     * <p>
     * The returned value is a list with 7 elements, one
     * for every day of the week, each element is a map that
     * links the name of each hub to the list of SSNs (codice fiscale)
     * of the persons allocated to that day in that hub
     * <p>
     * <b>N.B.</b> no particular order of allocation is guaranteed
     * but the same invocation (after {@link #clearAllocation}) must return the same
     * allocation.
     *
     * @return the list of daily allocations
     */
    public List<Map<String, List<String>>> weekAllocate() {
    	ArrayList<Map<String, List<String>>> list = new ArrayList<>();
    	Stream.iterate(0, a-> a+1).limit(7).forEach(i-> 
    	list.add(hubs.values().stream().collect(Collectors.toMap(Hub::getName,hu-> allocate(hu.getName(), i)))));
        return list;
    }

    // R5
    /**
     * Returns the proportion of allocated people
     * w.r.t. the total number of persons added
     * in the system
     *
     * @return proportion of allocated people
     */
    public double propAllocated() {
        return (double)persone.values().stream().filter(p->p.allocated).count()/persone.size();
    }

    /**
     * Returns the proportion of allocated people
     * w.r.t. the total number of persons added
     * in the system, divided by age interval.
     * <p>
     * The map associates the age interval label
     * to the proportion of allocates people in that interval
     *
     * @return proportion of allocated people by age interval
     */
    public Map<String, Double> propAllocatedAge() {
    	Map<String, Double> back = new HashMap<>();
    	int[] bf = new int[]{100000};
    	agegaps.stream().sorted((a,b)->-(a-b)).forEach(i->{
	    			back.put("["+i+","+((bf[0]==100000)? "+": bf[0])+")",(double)persone.values().stream().filter(p->p.allocated && getAge(p.getCodfisc())<bf[0] && getAge(p.getCodfisc())>=i).count()/persone.size());
	    			bf[0]=i;
    			});
    	return back;
    }

    /**
     * Retrieves the distribution of allocated persons
     * among the different age intervals.
     * <p>
     * For each age intervals the map reports the
     * proportion of allocated persons in the corresponding
     * interval w.r.t the total number of allocated persons
     *
     * @return
     */
    public Map<String, Double> distributionAllocated() {
    	Map<String, Double> back=new HashMap<>();
    	int[] bf = new int[]{100000};
    	long alloct=persone.values().stream().filter(p->p.isAllocated()).count();
    	agegaps.stream().sorted((a,b)->-(a-b)).forEach(i->{
	    			back.put("["+i+","+((bf[0]==100000)? "+": bf[0])+")",(double)persone.values().stream().filter(p->p.allocated && getAge(p.getCodfisc())<bf[0] && getAge(p.getCodfisc())>=i).count()/alloct);
	    			bf[0]=i;
    			});
    	return back;
    }

    // R6
    /**
     * Defines a listener for the file loading method.
     * The {@ accept()} method of the listener is called
     * passing the line number and the offending line.
     * <p>
     * Lines start at 1 with the header line.
     *
     * @param listener the listener for load errors
     */
    public void setLoadListener(BiConsumer<Integer, String> listener) {
    	this.listener=listener;
    }
}