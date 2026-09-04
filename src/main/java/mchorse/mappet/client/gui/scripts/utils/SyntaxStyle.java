package mchorse.mappet.client.gui.scripts.utils;

import net.minecraft.class_2487;

public class SyntaxStyle {
   public String title;
   public boolean shadow;
   public int primary;
   public int secondary;
   public int identifier;
   public int special;
   public int strings;
   public int comments;
   public int numbers;
   public int other;
   public int lineNumbers;
   public int background;
   public int error;

   public SyntaxStyle() {
      this.title = "prime";
      this.shadow = false;
      this.primary = 16723752;
      this.secondary = 16749465;
      this.identifier = 10879035;
      this.special = 8066092;
      this.strings = 13788542;
      this.comments = 4264218;
      this.numbers = 8519723;
      this.other = 16777215;
      this.lineNumbers = 7734528;
      this.background = 657931;
      this.error = 14289664;
   }

   public SyntaxStyle(class_2487 tag) {
      this.title = tag.method_10558("Title");
      this.shadow = tag.method_10577("Shadow");
      this.primary = tag.method_10550("Primary");
      this.secondary = tag.method_10550("Secondary");
      this.identifier = tag.method_10550("Identifier");
      this.special = tag.method_10550("Special");
      this.strings = tag.method_10550("Strings");
      this.comments = tag.method_10550("Comments");
      this.numbers = tag.method_10550("Numbers");
      this.other = tag.method_10550("Other");
      this.lineNumbers = tag.method_10550("LineNumbers");
      this.background = tag.method_10550("Background");
      this.error = tag.method_10550("Error");
   }

   public class_2487 toNBT() {
      return this.toNBT(new class_2487());
   }

   public class_2487 toNBT(class_2487 tag) {
      tag.method_10582("Title", this.title);
      tag.method_10556("Shadow", this.shadow);
      tag.method_10569("Primary", this.primary);
      tag.method_10569("Secondary", this.secondary);
      tag.method_10569("Identifier", this.identifier);
      tag.method_10569("Special", this.special);
      tag.method_10569("Strings", this.strings);
      tag.method_10569("Comments", this.comments);
      tag.method_10569("Numbers", this.numbers);
      tag.method_10569("Other", this.other);
      tag.method_10569("LineNumbers", this.lineNumbers);
      tag.method_10569("Background", this.background);
      tag.method_10569("Error", this.error);
      return tag;
   }
}
