package mchorse.mappet.api.scripts.user.mappet;

import java.util.Set;

public interface IMappetQuests {
   boolean has(String var1);

   boolean add(String var1);

   boolean isComplete(String var1);

   boolean complete(String var1);

   boolean decline(String var1);

   Set<String> getIds();
}
