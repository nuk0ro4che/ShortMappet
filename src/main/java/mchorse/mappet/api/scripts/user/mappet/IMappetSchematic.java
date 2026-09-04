package mchorse.mappet.api.scripts.user.mappet;

public interface IMappetSchematic {
   IMappetSchematic loadFromWorld(int var1, int var2, int var3, int var4, int var5, int var6);

   IMappetSchematic place(int var1, int var2, int var3, boolean var4, boolean var5);

   IMappetSchematic place(int var1, int var2, int var3, boolean var4);

   IMappetSchematic place(int var1, int var2, int var3);

   IMappetSchematic saveToFile(String var1);

   IMappetSchematic loadFromFile(String var1);
}
