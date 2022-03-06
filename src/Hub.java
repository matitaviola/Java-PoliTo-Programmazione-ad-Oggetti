package src;

import java.util.*;

public class Hub {
	protected String name;
	protected int nDoc;
	protected int nNurse;
	protected int others;
	protected HashMap<Integer, ArrayList<Person>> allocted = new HashMap<>();
	public Hub(String name) {
		super();
		this.name = name;
		this.nDoc=0;
	}

	public String getName() {
		return name;
	}

	public int getnDoc() {
		return nDoc;
	}

	public void setnDoc(int nDoc) {
		this.nDoc = nDoc;
	}

	public int getnNurse() {
		return nNurse;
	}

	public void setnNurse(int nNurse) {
		this.nNurse = nNurse;
	}

	public int getOthers() {
		return others;
	}

	public void setOthers(int others) {
		this.others = others;
	}
	public void emptyAlloc() {
		allocted.values().stream().flatMap(l->l.stream()).forEach(p->p.setAllocated(false));
		allocted.clear();
	}
}