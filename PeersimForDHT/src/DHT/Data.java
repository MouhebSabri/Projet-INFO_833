package DHT;

import java.util.Random;
public class Data {

	Random random = new Random();
	int ID;
	String data = "Bonsoir à toutes et tous";
	public Data(){
		this.ID = random.nextInt(1,10000);
	}

	public int getID(){
		return this.ID;
	}
	public String get_data(){return this.data;}
}
