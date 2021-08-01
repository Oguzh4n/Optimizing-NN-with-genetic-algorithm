package GA;
class Main
{
	
	static int maxGenerationen = 10;
	
	public static void main(String[] args)
	{	
		GA einGA = new GA();

		for(int g=0; g<maxGenerationen;g++)
		{
			System.out.println("Generation: " + g);
			einGA.selektierenRekombinieren();
			einGA.mutieren();
			einGA.dekodierenBewertenErsetzen();
			System.out.println("KNN min Fehler: " + einGA.getbestKNN().kleinsterFehler());
			if(einGA.getbestKNN().kleinsterFehler()==0) {
				break;
			}
		}
		
		einGA.auswerten();

	}
}