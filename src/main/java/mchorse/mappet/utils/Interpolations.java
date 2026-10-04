package mchorse.mappet.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mchorse.mclib.utils.Interpolation;

/**
 * Central resolver of the interpolations (easings) supported by Blockbuster.
 *
 * <p>All methods are null and typo safe: an unknown name falls back to the given
 * default instead of throwing, so scripts, packets and UI files can't be broken
 * by a misspelled interpolation.
 *
 * <p>Besides the canonical keys (ex. "bounce_out"), the resolver accepts any
 * case, dashes or spaces instead of underscores (ex. "Bounce Out"), keys
 * without the underscore (ex. "bounceout") and a few legacy names.
 */
public final class Interpolations
{
   public static final String DEFAULT_KEY = "linear";

   private static final String LANG_PREFIX = "mclib.interpolations.";

   private static final Map<String, Interpolation> BY_KEY = new HashMap<String, Interpolation>();
   private static final Map<String, String> ALIASES = new HashMap<String, String>();
   private static final List<String> KEYS = new ArrayList<String>();

   static
   {
      for (Interpolation interpolation : Interpolation.values())
      {
         String key = interpolation.name().toLowerCase(Locale.ROOT);

         KEYS.add(key);
         BY_KEY.put(key, interpolation);
         BY_KEY.put(interpolation.getKey().toLowerCase(Locale.ROOT), interpolation);
      }

      /* Legacy names that were hardcoded in some methods before */
      ALIASES.put("easein", "quad_in");
      ALIASES.put("easeout", "quad_out");
      ALIASES.put("easeinout", "quad_inout");
      ALIASES.put("ease_in", "quad_in");
      ALIASES.put("ease_out", "quad_out");
      ALIASES.put("ease_inout", "quad_inout");
      ALIASES.put("smoothstep", "sine_inout");
      ALIASES.put("in", "sine_in");
      ALIASES.put("out", "sine_out");
      ALIASES.put("inout", "sine_inout");
      ALIASES.put("ease", "sine_inout");
   }

   private Interpolations()
   {
   }

   /** All canonical interpolation keys, in Blockbuster's order. */
   public static List<String> getKeys()
   {
      return Collections.unmodifiableList(KEYS);
   }

   public static boolean exists(String name)
   {
      return resolve(name, null) != null;
   }

   /**
    * Resolves an interpolation by its key. Accepts any case, dashes, spaces,
    * mclib's translation keys and legacy aliases, as well as keys without the
    * underscore (ex. "bounceout").
    */
   public static Interpolation resolve(String name, Interpolation fallback)
   {
      if (name == null)
      {
         return fallback;
      }

      String key = name.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');

      if (key.startsWith(LANG_PREFIX))
      {
         key = key.substring(LANG_PREFIX.length());
      }

      if (key.isEmpty())
      {
         return fallback;
      }

      Interpolation interpolation = BY_KEY.get(key);

      if (interpolation != null)
      {
         return interpolation;
      }

      String alias = ALIASES.get(key);

      if (alias != null)
      {
         interpolation = BY_KEY.get(alias);

         if (interpolation != null)
         {
            return interpolation;
         }
      }

      if (key.indexOf('_') < 0)
      {
         interpolation = resolveLoose(key);

         if (interpolation != null)
         {
            return interpolation;
         }
      }

      return fallback;
   }

   /**
    * Resolves keys written without the underscore, ex. "bounceout" or
    * "quadin". A bare prefix (ex. "bounce") defaults to the "inout" variant.
    */
   private static Interpolation resolveLoose(String key)
   {
      String[] suffixes = {"inout", "in", "out"};

      for (String suffix : suffixes)
      {
         if (key.length() > suffix.length() && key.endsWith(suffix))
         {
            Interpolation interpolation = BY_KEY.get(key.substring(0, key.length() - suffix.length()) + "_" + suffix);

            if (interpolation != null)
            {
               return interpolation;
            }
         }
      }

      for (String suffix : suffixes)
      {
         Interpolation interpolation = BY_KEY.get(key + "_" + suffix);

         if (interpolation != null)
         {
            return interpolation;
         }
      }

      return null;
   }

   /** Returns the canonical key of the given interpolation, or the fallback one. */
   public static String normalize(String name, String fallback)
   {
      Interpolation interpolation = resolve(name, null);

      return interpolation == null ? fallback : interpolation.name().toLowerCase(Locale.ROOT);
   }

   public static String normalize(String name)
   {
      return normalize(name, DEFAULT_KEY);
   }

   /**
    * Eases a 0..1 progress with the given interpolation, ex. to turn a raw
    * progress into a bounced or elastic one. Returns the progress as is when
    * the interpolation is unknown.
    */
   public static double apply(String name, double progress, Interpolation fallback)
   {
      Interpolation interpolation = resolve(name, fallback);

      return interpolation == null ? progress : interpolation.interpolate(0.0D, 1.0D, progress);
   }

   public static double apply(String name, double progress)
   {
      return apply(name, progress, Interpolation.LINEAR);
   }

   public static float apply(String name, float from, float to, float progress, Interpolation fallback)
   {
      Interpolation interpolation = resolve(name, fallback);

      return interpolation == null ? from + (to - from) * progress : interpolation.interpolate(from, to, progress);
   }

   public static double apply(String name, double from, double to, double progress, Interpolation fallback)
   {
      Interpolation interpolation = resolve(name, fallback);

      return interpolation == null ? from + (to - from) * progress : interpolation.interpolate(from, to, progress);
   }

   public static double apply(String name, double from, double to, double progress)
   {
      return apply(name, from, to, progress, Interpolation.LINEAR);
   }
}
