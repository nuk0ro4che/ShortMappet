package mchorse.mappet.api.scripts.code.mappet;

import java.util.Set;
import mchorse.mappet.api.scripts.user.mappet.IMappetStates;
import mchorse.mappet.api.states.States;

public class MappetStates implements IMappetStates {
   public States states;

   public MappetStates(States states) {
      this.states = states;
   }

   public double add(String id, double value) {
      this.states.add(id, value);
      return this.states.getNumber(id);
   }

   public void setNumber(String id, double value) {
      this.states.setNumber(id, value);
   }

   public void setString(String id, String value) {
      this.states.setString(id, value);
   }

   public double getNumber(String id) {
      return this.states.getNumber(id);
   }

   public boolean isNumber(String id) {
      return this.states.isNumber(id);
   }

   public String getString(String id) {
      return this.states.getString(id);
   }

   public boolean isString(String id) {
      return this.states.isString(id);
   }

   public void reset(String id) {
      this.states.reset(id);
   }

   public void resetMasked(String id) {
      this.states.resetMasked(id);
   }

   public void clear() {
      this.states.clear();
   }

   public boolean has(String id) {
      return this.states.values.containsKey(id);
   }

   public Set<String> keys() {
      return this.states.values.keySet();
   }
}
