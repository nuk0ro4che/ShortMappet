package mchorse.mappet.api.scripts.user.entities;

import java.util.List;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.entities.EntityNpc;

public interface IScriptNpc extends IScriptEntity {
   EntityNpc getMappetNpc();

   String getNpcId();

   String getNpcState();

   void setNpcState(String var1);

   void canPickUpLoot(boolean var1);

   void follow(String var1);

   String getFaction();

   void setCanBeSteered(boolean var1);

   boolean canBeSteered();

   void setSteeringOffset(int var1, float var2, float var3, float var4);

   void addSteeringOffset(float var1, float var2, float var3);

   List<ScriptVector> getSteeringOffsets();

   void setNpcSpeed(float var1);

   float getNpcSpeed();

   void setJumpPower(float var1);

   float getJumpPower();

   void setInvincible(boolean var1);

   boolean isInvincible();

   void setCanSwim(boolean var1);

   boolean canSwim();

   void setImmovable(boolean var1);

   boolean isImmovable();

   void setShadowSize(float var1);

   float getShadowSize();

   float setXpValue(int var1);

   int getXpValue();

   float getPathDistance();

   void setPathDistance(float var1);

   void setAttackRange(float var1);

   float getAttackRange();

   void setKillable(boolean var1);

   boolean isKillable();

   boolean canGetBurned();

   void canGetBurned(boolean var1);

   boolean canFallDamage();

   void canFallDamage(boolean var1);

   float getDamage();

   void setDamage(float var1);

   int getDamageDelay();

   void setDamageDelay(int var1);

   boolean doesWander();

   void setWander(boolean var1);

   boolean doesLookAround();

   void setLookAround(boolean var1);

   boolean doesLookAtPlayer();

   void setLookAtPlayer(boolean var1);

   void addPatrol(int var1, int var2, int var3);

   void addPatrol(int var1, int var2, int var3, String var4);

   void addPatrol(ScriptVector var1);

   void addPatrol(ScriptVector var1, String var2);

   void setPatrol(int var1, int var2, int var3, int var4);

   void setPatrol(int var1, int var2, int var3, int var4, String var5);

   void setPatrol(int var1, ScriptVector var2);

   void setPatrol(int var1, ScriptVector var2, String var3);

   void removePatrolPoint(int var1);

   void removePatrolPoint(int var1, int var2, int var3);

   void clearPatrolPoints();
}
