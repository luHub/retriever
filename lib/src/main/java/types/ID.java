package types;

public final class ID {

	
	public static String reverseID(String name, String zipCode){
		String id = name.replaceAll("[^a-zA-Z0-9]", "")+zipCode.replaceAll("[^a-zA-Z0-9]", "");
		return id+zipCode;
	}
	
}
