package mchorse.mappet.client;






public final class ClientFirstPersonLight {
   private static volatile int packedLight = 15728880;

   private ClientFirstPersonLight() {
   }

   public static void capture(int light) {
      packedLight = light;
   }

   public static int get() {
      return packedLight;
   }
}
