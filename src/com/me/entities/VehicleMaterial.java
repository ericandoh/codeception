package com.me.entities;

public class VehicleMaterial {
	
	public static String[] TIER_1 = {"Wood", "Iron", "Copper", "Tin", "Aluminum", "Netrasic"};
	public static String[] TIER_2 = {"Gold", "Lead", "Quicksilver", "Silver", "Mithril", "Adamantium"};
	public static String[] TIER_3 = {"Topaz", "Diamond", "Sapphire", "Ruby", "Opal", "Netramond"};
	
	public static float[] getMaterialProperties(String whole) {
		String n;
		for (int i = 0; i < TIER_1.length; i++) {
			n = TIER_1[i];
			if (whole.startsWith(n))
				return getProperties(i, 0);
		}
		for (int i = 0; i < TIER_2.length; i++) {
			n = TIER_2[i];
			if (whole.startsWith(n))
				return getProperties(i, 1);
		}
		for (int i = 0; i < TIER_3.length; i++) {
			n = TIER_3[i];
			if (whole.startsWith(n))
				return getProperties(i, 2);
		}
		return new float[] {1f, 1f, 1f};
	}
	private static float[] getProperties(int p, int tier) {
		float[] properties = {1f, 1f, 1f};
		//properties[0] = innate weight of object
		//properties[1] = innate force/power of object
		//properties[2] = innate health of an object
		//default = 1, so think of these as multipliers to a normal average item.
		
		if (tier == 0) {
			//{"Wood", "Iron", "Copper", "Tin", "Aluminum", "Netrasic"};
			//weight, force, hp
			if (p == 0)
				properties = new float[] {0.8f, 0.2f, 0.5f};
			else if (p == 1)
				properties = new float[] {3f, 1f, 1f};
			else if (p == 2)
				properties = new float[] {2f, 0.6f, 0.8f};
			else if (p == 3)
				properties = new float[] {2f, 0.6f, 0.8f};
			else if (p == 4)
				properties = new float[] {1.5f, 0.65f, 0.9f};
			else if (p == 5)
				properties = new float[] {1f, 2f, 2f};
		}
		else if (tier == 1) {
			//{"Gold", "Lead", "Quicksilver", "Silver", "Mithril", "Adamantium"};
			//weight, force, hp
			if (p == 0)
				properties = new float[] {5f, 3f, 0.5f};
			else if (p == 1)
				properties = new float[] {6f, 2f, 2f};
			else if (p == 2)
				properties = new float[] {3.5f, 3f, 0.2f};
			else if (p == 3)
				properties = new float[] {3.5f, 1.5f, 0.6f};
			else if (p == 4)
				properties = new float[] {5f, 2.5f, 5f};
			else if (p == 5)
				properties = new float[] {6f, 3f, 6.5f};
		}
		else if (tier == 2) {
			//{"Topaz", "Diamond", "Sapphire", "Ruby", "Opal", "Netramond"};
			//weight, force, hp
			if (p == 0)
				properties = new float[] {0.1f, 1.5f, 2f};
			else if (p == 1)
				properties = new float[] {0.1f, 4.5f, 5.5f};
			else if (p == 2)
				properties = new float[] {0.1f, 2.5f, 4f};
			else if (p == 3)
				properties = new float[] {0.1f, 2f, 4.5f};
			else if (p == 4)
				properties = new float[] {0.1f, 1f, 1.5f};
			else if (p == 5)
				properties = new float[] {0.1f, 3f, 7f};
		}
		else {
		}
		return properties;
	}
}
