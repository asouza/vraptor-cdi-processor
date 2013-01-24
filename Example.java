//@lombok.NoArgsConstructor
public class Example {

//	private final static String xpto1 = "xxx";
//	private final	 String xpto;
	private String chuchuzinho;

	public void someMethod(final String oioioi){
		final int blabla = 0;
	}
	
	public static void main(String[] args) {

		Abstrata ab = new Abstrata("xpto"){
			public void metodoAbs(){
				System.out.println("ooo");
			}
		};
		
	}

	public class Teste{

		public void metodo(){}

	}

}

abstract class Abstrata{

	public Abstrata(String h){

	}

	public abstract void metodoAbs();


}