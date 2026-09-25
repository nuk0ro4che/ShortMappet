package mchorse.mappet.client;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

public class ClientVisualBlocks {
	public static final Map<class_2338, Entry> OVERRIDES = new HashMap<class_2338, Entry>();

	public static class Entry {
		public final class_2248 real;
		public final class_2680 override;

		public Entry(class_2248 real, class_2680 override) {
			this.real = real;
			this.override = override;
		}
	}

	public static void override(class_2338 pos, class_2248 real, class_2680 override) {
		OVERRIDES.put(pos, new Entry(real, override));
	}

	public static Entry get(class_2338 pos) {
		return OVERRIDES.get(pos);
	}

	public static void remove(class_2338 pos) {
		OVERRIDES.remove(pos);
	}

	public static void clear() {
		OVERRIDES.clear();
	}
}