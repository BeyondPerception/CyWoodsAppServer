
public class Main {

	public static final String version = "0.0.1";

	public static void main(String... args) {
		if (args.length == 0) {
			bad();
		}
		String arg = args[0];
		if (arg.equals("--help") || arg.equals("-h")) {
			System.out.println("shed - The schedule file editing CLI tool");
			System.out.println("For safely editing the schedule file");
			System.out.println();

			System.out.println("Usage: shed [OPTION] PATH_TO_FILE");
			System.out.println();

			System.out.println("Options:");
			System.out.println("-c, --check\tCheck to see if the schedule file is correct");
			System.out.println("-h, --help\tShows this help page and exit");
			System.out.println("-v, --version\t Display version and exit");
			System.out.println();

			System.out.println("Note: arguments may not be combined");
			System.exit(0);
		}
		if (arg.equals("--version") || arg.equals("-v")) {
			System.out.println("shed " + version);
			System.out.println();

			System.out.println("Written by: Ronak Malik");
			System.exit(0);
		}
		if (arg.equals("--check") || arg.equals("-c")) {
			if (args.length < 2) {
				bad();
			}
			
			
			
		}
	}

	public static void bad() {
		System.out.println("Usage: shed [OPTION] FILE");
		System.out.println("Try 'shed --help' for more information.");
		System.exit(0);
	}
}
