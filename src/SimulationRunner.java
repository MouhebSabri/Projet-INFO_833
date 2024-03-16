public class SimulationRunner {

    public static void main(String[] args) {
    	String configPath = "C:\\Users\\ASUS\\Desktop\\IDU-4\\Cours\\S8\\INFO 833\\src\\config.cfg";

        String[] peersimArgs = {"-config", configPath};

        peersim.Simulator.main(peersimArgs);
    }
}
