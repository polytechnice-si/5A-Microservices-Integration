package gen;


public class  Generator {

	private String name;

	private int cpt = 0;

	public Generator(String s) {
		this.name = s;
	}

	public String run() {
		cpt++;
		return name+cpt;
	}


	public String getName() {
		return name;
	}

}
